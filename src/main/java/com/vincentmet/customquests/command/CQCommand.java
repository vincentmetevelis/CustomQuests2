package com.vincentmet.customquests.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vincentmet.customquests.Objects;
import com.vincentmet.customquests.*;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.event.DataLoadingEvent;
import com.vincentmet.customquests.helpers.PartyInviteCache;
import com.vincentmet.customquests.network.messages.*;
import java.util.*;
import java.util.regex.Pattern;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.PacketDistributor;

public class CQCommand{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal(Ref.MODID)
                .then(registerPartyCommand())
                .then(registerProgressCommand())
                .then(registerGiveCommand())
                //.then(registerEditorCommand())
                .then(registerSettingsCommand())
                .then(registerInfoCommand())
                .then(registerReloadCommand())
                //.then(registerQuestsCommand())
                .then(registerDiscordCommand())
                .then(registerUuidCommand())
                .executes(context->{
                        context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.use_subcommand"));
                        return 0;
                })
        );
        
    }
    
    public static ArgumentBuilder<CommandSourceStack, ?> registerPartyCommand(){
        return Commands.literal("party")
                .then(Commands.literal("list")
                    .executes(context -> {
                        if(QuestingStorage.getSidedPartiesMap().size()>=1){
                            QuestingStorage.getSidedPartiesMap().values().forEach(party->context.getSource().sendSuccess(new TextComponent("ID: " + party.getId() + ", " + new TranslatableComponent(Ref.MODID + ".general.name").getString() + ": " + party.getName()), false));
                        }else{
                            context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.list.no_parties"));
                        }
                        return 0;
                    })
                )
                .then(Commands.literal("create")
                    .then(Commands.argument("party_name", StringArgumentType.string())
                        .executes(context -> {
                            String name = context.getArgument("party_name", String.class);
                            if(Pattern.matches("[A-Za-z0-9]+?", name)){
                                int partyId = PartyHelper.createParty(context.getSource().getPlayerOrException().getUUID(), name);
                                if(partyId == Ref.NO_PARTY){
                                    context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.create.error"));
                                }else{
                                    String devModeMessage = Config.SidedConfig.isDebugModeOn() ? " (ID: "+partyId+", name:'" + name + "')" : "";
                                    context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.party.create.success", devModeMessage), false);
                                    context.getSource().getServer().getPlayerList().getPlayers().forEach(ServerUtils::sendProgressAndParties);
                                }
                            }else{
                                context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.create.invalid_name"));
                            }
                            return 0;
                        })
                    )
                    .executes(context -> {
                        context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.create.no_name"));
                        return 0;
                    })
                )
                .then(Commands.literal("leave")
                    .executes(context -> {
                        UUID uuid = context.getSource().getEntityOrException().getUUID();
                        int playerParty = ProgressHelper.getPlayerParty(uuid);
                        List<UUID> uuidsInParty = PartyHelper.getAllUUIDsInParty(playerParty);
                        if(playerParty == Ref.NO_PARTY){
                            context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.leave.not_in_party"));
                            return 0;
                        }
                        if(PartyHelper.isPlayerPartyOwner(uuid, playerParty) && uuidsInParty.size() > 1){
                            context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.leave.party_owner"));
                            return 0;
                        }
                        if(uuidsInParty.size() == 1){ //If player is last one in party
                            ProgressHelper.setPlayerParty(uuid, Ref.NO_PARTY);
                            PartyHelper.deleteParty(playerParty);
                            context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.party.delete.success"), false);
                            return 0;
                        }
                        ProgressHelper.setPlayerParty(uuid, Ref.NO_PARTY);
                        context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.party.leave.success"), false);
                        return 0;
                    })
                )
                .then(Commands.literal("setowner")
                    .then(Commands.argument("new_owner", EntityArgument.player())
                        .executes(context -> {
                            UUID uuid = context.getSource().getPlayerOrException().getUUID();
                            int playerParty = ProgressHelper.getPlayerParty(uuid);
                            Player newOwner = EntityArgument.getPlayer(context, "new_owner");
                            if(!ProgressHelper.isPlayerInParty(uuid)){
                                context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.set_owner.not_in_party"));
                            }else if(newOwner.getUUID().equals(uuid)){
                                context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.set_owner.already_owner"));
                            }else if(PartyHelper.isPlayerPartyOwner(uuid, playerParty) && ProgressHelper.getPlayerParty(newOwner.getUUID()) == playerParty){
                                PartyHelper.setPartyOwner(newOwner.getUUID(), playerParty);
                                context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.party.set_owner.success", newOwner.getDisplayName().getString()), false);
                            }else if(ProgressHelper.getPlayerParty(newOwner.getUUID()) == playerParty){
                                context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.set_owner.not_owner"));
                            }else{
                                context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.set_owner.new_owner_not_in_party"));
                            }
                            return 0;
                        })
                    )
                    .executes(context -> {
                        context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.set_owner.no_name"));
                        return 0;
                    })
                )
                .then(Commands.literal("invite")
                     .then(Commands.argument("player", EntityArgument.player())
                         .executes(context -> {
                             Player inviter = context.getSource().getPlayerOrException();
                             UUID inviterUUID = inviter.getUUID();
                             Player invitedPlayer = EntityArgument.getPlayer(context, "player");
                             UUID invitedPlayerUUID = invitedPlayer.getUUID();
                             if(ProgressHelper.doesPlayerExist(inviterUUID) && ProgressHelper.isPlayerInParty(inviterUUID)){
                                 int partyId = ProgressHelper.getPlayerParty(inviterUUID);
                                 if(invitedPlayerUUID.equals(inviterUUID)){
                                     context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.invite.yourself"));
                                 }else if(PartyInviteCache.isPlayerInvitedToAnyParty(invitedPlayerUUID)){
                                     context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.invite.player_already_pending"));
                                 }else if(!PartyHelper.isPlayerPartyOwner(inviterUUID, partyId)){
                                     context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.invite.not_owner"));
                                 }else{
                                     PartyInviteCache.addInvite(invitedPlayerUUID, partyId);
                                     context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.party.invite.success.feedback", invitedPlayer.getDisplayName().getString()), false);
                                     invitedPlayer.sendMessage(new TranslatableComponent(Ref.MODID + ".command.party.invite.success.invited_player_message", inviter.getDisplayName().getString()), inviterUUID);
                                 }
                             }
                             return 0;
                         })
                     )
                     .executes(context -> {
                         context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.invite.no_name"));
                         return 0;
                     })
                )
                .then(Commands.literal("accept")
                    .executes(context -> {
                        UUID accepter = context.getSource().getPlayerOrException().getUUID();
                        if(ProgressHelper.isPlayerInParty(accepter)){
                            context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.accept.in_party"));
                        }else if(!PartyInviteCache.isPlayerInvitedToAnyParty(accepter)){
                            context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.accept.not_invited"));
                        }else{
                            int newPartyId = PartyInviteCache.getPartyForInvite(accepter);
                            ProgressHelper.setPlayerParty(accepter, newPartyId);
                            context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.party.accept.success"), false);
                        }
                        return 0;
                    })
                )
                .then(Commands.literal("deny")
                    .executes(context -> {
                        UUID denier = context.getSource().getPlayerOrException().getUUID();
                        if(ProgressHelper.isPlayerInParty(denier)){
                            context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.deny.in_party"));
                        }else if(PartyInviteCache.isPlayerInvitedToAnyParty(denier)){
                            context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.party.deny.not_invited"));
                        }else{
                            PartyInviteCache.removePlayerInvite(denier);
                            context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.party.deny.success"), false);
                        }
                        return 0;
                    })
                )
                .executes(context->{
                    context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.use_subcommand"));
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSourceStack, ?> registerProgressCommand(){
        return Commands.literal("progress")
                .requires(cs->{
                    if(cs.getEntity() instanceof ServerPlayer){
                        return cs.hasPermission(2) || (cs.getEntity()).getDisplayName().getString().equals("vincentmet");//For troubleshooting
                    }
                    return cs.hasPermission(2);
                })
                //todo add progress give command
                .then(Commands.literal("delete")
                        .then(Commands.literal("all")
                                .executes(context -> {
                                    QuestingStorage.getSidedPlayersMap().forEach((uuid, questingPlayer) -> ProgressHelper.deleteProgress(UUID.fromString(uuid)));
                                    QuestingStorage.getSidedPartiesMap().forEach((partyId, party) -> PartyHelper.deleteProgress(partyId));
                                    context.getSource().getServer().getPlayerList().getPlayers().forEach(ServerUtils::sendProgressToClient);
                                    context.getSource().getServer().getPlayerList().getPlayers().forEach(ServerUtils::sendPartiesToClient);
                                    context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.progress.delete.all.success"), false);
                                    return 0;
                                })
                        )
                        .then(Commands.literal("player")
                            .then(Commands.argument("player_name", EntityArgument.players())
                                .executes(context -> {
                                    Collection<ServerPlayer> serverPlayerEntities = EntityArgument.getPlayers(context, "player_name");
                                    serverPlayerEntities.forEach(playerEntity -> {
                                        ProgressHelper.deleteProgress(playerEntity.getUUID());
                                        context.getSource().getServer().getPlayerList().getPlayers().forEach(ServerUtils::sendProgressToClient);
                                        context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.progress.delete.player.success", playerEntity.getDisplayName()), false);
                                    });
                                    return 0;
                                })
                            )
                        )
                        .then(Commands.literal("party")
                            .then(Commands.argument("party_id", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    int partyId = IntegerArgumentType.getInteger(context, "party_id");
                                    if(PartyHelper.doesPartyExist(partyId)){
                                        PartyHelper.deleteProgress(partyId);
                                        context.getSource().getServer().getPlayerList().getPlayers().forEach(ServerUtils::sendPartiesToClient);
                                        context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.progress.delete.party.success", partyId, PartyHelper.getPartyName(partyId)), false);
                                    }else{
                                        context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.progress.delete.party.failed", partyId));
                                        
                                    }
                                    return 0;
                                })
                            )
                        )
                        .executes(context -> {
                            context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.use_subcommand"));
                            return 0;
                        })
                )
                .executes(context->{
                    context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.wip"));
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSourceStack, ?> registerGiveCommand(){
        return Commands.literal("give")
                .executes(context->{
                        try{
                            ItemHandlerHelper.giveItemToPlayer(context.getSource().getPlayerOrException(), new ItemStack(Objects.Items.QUESTING_DEVICE, 1));
                        }catch(CommandSyntaxException e){
                            e.printStackTrace();
                        }
                        return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSourceStack, ?> registerEditorCommand(){
        return Commands.literal("editor")
                .executes(context->{
                    try{
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(()->player), new MessageOpenEditor());
                    }catch(CommandSyntaxException ignored){}
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSourceStack, ?> registerSettingsCommand(){
        return Commands.literal("settings")
                .requires(cs->{
                    if(cs.getEntity() instanceof ServerPlayer){
                        return cs.hasPermission(2) || (cs.getEntity()).getDisplayName().getString().equals("vincentmet");//For troubleshooting
                    }
                    return cs.hasPermission(2);
                })
                .then(Commands.literal("can_reward_only_be_claimed_once")
                     .then(Commands.argument("value", BoolArgumentType.bool())
                           .executes(context -> {
                               Config.ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE = BoolArgumentType.getBool(context, "value");
                               context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.settings.set", "can_reward_only_be_claimed_once", ChatFormatting.GOLD + String.valueOf(Config.SidedConfig.canRewardOnlyBeClaimedOnce())), false);
                               ServerUtils.sendServerConfigToAllPlayers();
                               return 0;
                           })
                     )
                     .executes(context -> {
                         context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.settings.get", "can_reward_only_be_claimed_once", ChatFormatting.GOLD + String.valueOf(Config.SidedConfig.canRewardOnlyBeClaimedOnce())), false);
                         return 0;
                     })
                )
                .then(Commands.literal("edit_mode")
                     .then(Commands.argument("value", BoolArgumentType.bool())
                           .executes(context -> {
                               Config.ServerConfig.EDIT_MODE = BoolArgumentType.getBool(context, "value");
                               context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.settings.set", "edit_mode", ChatFormatting.GOLD + String.valueOf(Config.SidedConfig.isEditModeOn())), false);
                               ServerUtils.sendServerConfigToAllPlayers();
                               return 0;
                           })
                     )
                     .executes(context -> {
                         context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.settings.get", "edit_mode", ChatFormatting.GOLD + String.valueOf(Config.SidedConfig.isEditModeOn())), false);
                         return 0;
                     })
                )
                .then(Commands.literal("give_device_on_first_login")
                     .then(Commands.argument("value", BoolArgumentType.bool())
                           .executes(context -> {
                               Config.ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN = BoolArgumentType.getBool(context, "value");
                               context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.settings.set", "give_device_on_first_login", ChatFormatting.GOLD + String.valueOf(Config.SidedConfig.giveDeviceOnFirstLogin())), false);
                               ServerUtils.sendServerConfigToAllPlayers();
                               return 0;
                           })
                     )
                     .executes(context -> {
                         context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.settings.get", "give_device_on_first_login", ChatFormatting.GOLD + String.valueOf(Config.SidedConfig.giveDeviceOnFirstLogin())), false);
                         return 0;
                     })
                )
                .then(Commands.literal("debug_mode")
                     .then(Commands.argument("value", BoolArgumentType.bool())
                           .executes(context -> {
                               Config.ServerConfig.DEBUG_MODE = BoolArgumentType.getBool(context, "value");
                               context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.settings.set", "debug_mode", ChatFormatting.GOLD + String.valueOf(Config.SidedConfig.isDebugModeOn())), false);
                               ServerUtils.sendServerConfigToAllPlayers();
                               return 0;
                           })
                     )
                     .executes(context -> {
                         context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.settings.get", "debug_mode", ChatFormatting.GOLD + String.valueOf(Config.SidedConfig.isDebugModeOn())), false);
                         return 0;
                     })
                )
                .then(Commands.literal("backups")
                     .then(Commands.argument("value", BoolArgumentType.bool())
                           .executes(context -> {
                               Config.ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN = BoolArgumentType.getBool(context, "value");
                               context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.settings.set", "backups", ChatFormatting.GOLD + String.valueOf(Config.SidedConfig.areBackupsEnabled())), false);
                               ServerUtils.sendServerConfigToAllPlayers();
                               return 0;
                           })
                     )
                     .executes(context -> {
                         context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.settings.get", "backups", ChatFormatting.GOLD + String.valueOf(Config.SidedConfig.areBackupsEnabled())), false);
                         return 0;
                     })
                )
                .executes(context->{
                    context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.use_subcommand"));
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSourceStack, ?> registerInfoCommand(){
        return Commands.literal("info")
                .executes(context->{
                    context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.info.modid", Ref.MODID), false);
                    context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.info.mod_version", Ref.VERSION_MOD), false);
                    context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.info.mc_version", Ref.VERSION_MC), false);
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSourceStack, ?> registerReloadCommand(){
        return Commands.literal("reload")
                .requires(cs->{
                    if(cs.getEntity() instanceof ServerPlayer){
                        return cs.hasPermission(2) || (cs.getEntity()).getDisplayName().getString().equals("vincentmet");//For troubleshooting
                    }
                    return cs.hasPermission(2);
                })
                .executes(context->{
                    MinecraftForge.EVENT_BUS.post(new DataLoadingEvent.Pre());
                    CQHelper.readAllFilesAndPutIntoHashmaps();
                    MinecraftForge.EVENT_BUS.post(new DataLoadingEvent.Post());
                    context.getSource().getServer().getPlayerList().getPlayers().forEach(playerEntity->{
                        CQHelper.generateMissingProgress(playerEntity.getUUID());
                        CQHelper.generateMissingPartyProgress();
                        ServerUtils.sendQuestsAndChapters(playerEntity);
                        ServerUtils.sendProgressAndParties(playerEntity);
                    });
                    context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.reload.success"), false);
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSourceStack, ?> registerQuestsCommand(){
        return Commands.literal("quests")//modify default quests /// remove, add, dupe, edit, stuff like that
                .executes(context->{
                    context.getSource().sendFailure(new TranslatableComponent(Ref.MODID + ".command.wip"));
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSourceStack, ?> registerUuidCommand(){
        return Commands.literal("uuid")
                .executes(context->{
                    context.getSource().sendSuccess(new TranslatableComponent(Ref.MODID + ".command.uuid", context.getSource().getPlayerOrException().getStringUUID()), false);
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSourceStack, ?> registerDiscordCommand(){
        return Commands.literal("discord")
                .executes(context->{
                    try{
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(()->player), new MessageDiscord());
                    }catch(CommandSyntaxException e){
                        e.printStackTrace();
                    }
                    return 0;
                })
        ;
    }
}
