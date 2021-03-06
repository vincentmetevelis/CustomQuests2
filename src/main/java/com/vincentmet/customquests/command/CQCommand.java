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
import net.minecraft.command.*;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.items.ItemHandlerHelper;

public class CQCommand{
    public static void register(CommandDispatcher<CommandSource> dispatcher){
        dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal(Ref.MODID)
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
                        context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.use_subcommand"), false);
                        return 0;
                })
        );
        
    }
    
    public static ArgumentBuilder<CommandSource, ?> registerPartyCommand(){
        return Commands.literal("party")
                .then(Commands.literal("list")
                    .executes(context -> {
                        if(QuestingStorage.getSidedPartiesMap().size()>=1){
                            QuestingStorage.getSidedPartiesMap().values().forEach(party->context.getSource().sendFeedback(new StringTextComponent("ID: " + party.getId() + ", " + new TranslationTextComponent(Ref.MODID + ".general.name").getString() + ": " + party.getName()), false));
                        }else{
                            context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.list.no_parties"), false);
                        }
                        return 0;
                    })
                )
                .then(Commands.literal("create")
                    .then(Commands.argument("party_name", StringArgumentType.string())
                        .executes(context -> {
                            String name = context.getArgument("party_name", String.class);
                            if(Pattern.matches("[A-Za-z0-9]+?", name)){
                                int partyId = PartyHelper.createParty(context.getSource().asPlayer().getUniqueID(), name);
                                if(partyId == Ref.NO_PARTY){
                                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.create.error"), false);
                                }else{
                                    String devModeMessage = Config.SidedConfig.isDebugModeOn() ? " (ID: "+partyId+", name:'" + name + "')" : "";
                                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.create.success", devModeMessage), false);
                                    context.getSource().getServer().getPlayerList().getPlayers().forEach(ServerUtils::sendProgressAndParties);
                                }
                            }else{
                                context.getSource().sendErrorMessage(new TranslationTextComponent(Ref.MODID + ".command.party.create.invalid_name"));
                            }
                            return 0;
                        })
                    )
                    .executes(context -> {
                        context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.create.no_name"), false);
                        return 0;
                    })
                )
                .then(Commands.literal("leave")
                    .executes(context -> {
                        UUID uuid = context.getSource().asPlayer().getUniqueID();
                        int playerParty = ProgressHelper.getPlayerParty(uuid);
                        List<UUID> uuidsInParty = PartyHelper.getAllUUIDsInParty(playerParty);
                        if(playerParty == Ref.NO_PARTY){
                            context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.leave.not_in_party"), false);
                            return 0;
                        }
                        if(PartyHelper.isPlayerPartyOwner(uuid, playerParty) && uuidsInParty.size() > 1){
                            context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.leave.party_owner"), false);
                            return 0;
                        }
                        if(uuidsInParty.size() == 1){ //If player is last one in party
                            ProgressHelper.setPlayerParty(uuid, Ref.NO_PARTY);
                            PartyHelper.deleteParty(playerParty);
                            context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.delete.success"), false);
                            return 0;
                        }
                        ProgressHelper.setPlayerParty(uuid, Ref.NO_PARTY);
                        context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.leave.success"), false);
                        return 0;
                    })
                )
                .then(Commands.literal("setowner")
                    .then(Commands.argument("new_owner", EntityArgument.player())
                        .executes(context -> {
                            UUID uuid = context.getSource().asPlayer().getUniqueID();
                            int playerParty = ProgressHelper.getPlayerParty(uuid);
                            PlayerEntity newOwner = EntityArgument.getPlayer(context, "new_owner");
                            if(!ProgressHelper.isPlayerInParty(uuid)){
                                context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.set_owner.not_in_party"), false);
                            }else if(newOwner.getUniqueID().equals(uuid)){
                                context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.set_owner.already_owner"), false);
                            }else if(PartyHelper.isPlayerPartyOwner(uuid, playerParty) && ProgressHelper.getPlayerParty(newOwner.getUniqueID()) == playerParty){
                                PartyHelper.setPartyOwner(newOwner.getUniqueID(), playerParty);
                                context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.set_owner.success", newOwner.getDisplayName().getString()), false);
                            }else if(ProgressHelper.getPlayerParty(newOwner.getUniqueID()) == playerParty){
                                context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.set_owner.not_owner"), false);
                            }else{
                                context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.set_owner.new_owner_not_in_party"), false);
                            }
                            return 0;
                        })
                    )
                    .executes(context -> {
                        context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.set_owner.no_name"), false);
                        return 0;
                    })
                )
                .then(Commands.literal("invite")
                     .then(Commands.argument("player", EntityArgument.player())
                         .executes(context -> {
                             PlayerEntity inviter = context.getSource().asPlayer();
                             UUID inviterUUID = inviter.getUniqueID();
                             PlayerEntity invitedPlayer = EntityArgument.getPlayer(context, "player");
                             UUID invitedPlayerUUID = invitedPlayer.getUniqueID();
                             if(ProgressHelper.doesPlayerExist(inviterUUID) && ProgressHelper.isPlayerInParty(inviterUUID)){
                                 int partyId = ProgressHelper.getPlayerParty(inviterUUID);
                                 if(invitedPlayerUUID.equals(inviterUUID)){
                                     context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.invite.yourself"), false);
                                 }else if(PartyInviteCache.isPlayerInvitedToAnyParty(invitedPlayerUUID)){
                                     context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.invite.player_already_pending"), false);
                                 }else if(!PartyHelper.isPlayerPartyOwner(inviterUUID, partyId)){
                                     context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.invite.not_owner"), false);
                                 }else{
                                     PartyInviteCache.addInvite(invitedPlayerUUID, partyId);
                                     context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.invite.success.feedback", invitedPlayer.getDisplayName().getString()), false);
                                     invitedPlayer.sendMessage(new TranslationTextComponent(Ref.MODID + ".command.party.invite.success.invited_player_message", inviter.getDisplayName().getString()), inviterUUID);
                                 }
                             }
                             return 0;
                         })
                     )
                     .executes(context -> {
                         context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.invite.no_name"), false);
                         return 0;
                     })
                )
                .then(Commands.literal("accept")
                    .executes(context -> {
                        UUID accepter = context.getSource().asPlayer().getUniqueID();
                        if(ProgressHelper.isPlayerInParty(accepter)){
                            context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.accept.in_party"), false);
                        }else if(!PartyInviteCache.isPlayerInvitedToAnyParty(accepter)){
                            context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.accept.not_invited"), false);
                        }else{
                            int newPartyId = PartyInviteCache.getPartyForInvite(accepter);
                            ProgressHelper.setPlayerParty(accepter, newPartyId);
                            context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.accept.success"), false);
                        }
                        return 0;
                    })
                )
                .then(Commands.literal("deny")
                    .executes(context -> {
                        UUID denier = context.getSource().asPlayer().getUniqueID();
                        if(ProgressHelper.isPlayerInParty(denier)){
                            context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.deny.in_party"), false);
                        }else if(PartyInviteCache.isPlayerInvitedToAnyParty(denier)){
                            context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.deny.not_invited"), false);
                        }else{
                            PartyInviteCache.removePlayerInvite(denier);
                            context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.party.deny.success"), false);
                        }
                        return 0;
                    })
                )
                .executes(context->{
                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.use_subcommand"), false);
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSource, ?> registerProgressCommand(){
        return Commands.literal("progress")
                .requires(cs->{
                    if(cs.getEntity() instanceof ServerPlayerEntity){
                        return cs.hasPermissionLevel(2) || (cs.getEntity()).getDisplayName().getString().equals("vincentmet");//For troubleshooting
                    }
                    return cs.hasPermissionLevel(2);
                })
                .then(Commands.literal("delete")
                        .then(Commands.literal("all")
                                .executes(context -> {
                                    QuestingStorage.getSidedPlayersMap().forEach((uuid, questingPlayer) -> ProgressHelper.deleteProgress(UUID.fromString(uuid)));
                                    QuestingStorage.getSidedPartiesMap().forEach((partyId, party) -> PartyHelper.deleteProgress(partyId));
                                    context.getSource().getServer().getPlayerList().getPlayers().forEach(ServerUtils::sendProgressToClient);
                                    context.getSource().getServer().getPlayerList().getPlayers().forEach(ServerUtils::sendPartiesToClient);
                                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.progress.delete.all.success"), false);
                                    return 0;
                                })
                        )
                        .then(Commands.literal("player")
                            .then(Commands.argument("player_name", EntityArgument.players())
                                .executes(context -> {
                                    Collection<ServerPlayerEntity> serverPlayerEntities = EntityArgument.getPlayers(context, "player_name");
                                    serverPlayerEntities.forEach(playerEntity -> {
                                        ProgressHelper.deleteProgress(playerEntity.getUniqueID());
                                        context.getSource().getServer().getPlayerList().getPlayers().forEach(ServerUtils::sendProgressToClient);
                                        context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.progress.delete.player.success", playerEntity.getDisplayName()), false);
                                    });
                                    return 0;
                                })
                            )
                        )
                        .executes(context -> {
                            context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.use_subcommand"), false);
                            return 0;
                        })
                )
                .executes(context->{
                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.wip"), false);
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSource, ?> registerGiveCommand(){
        return Commands.literal("give")
                .executes(context->{
                        try{
                            ItemHandlerHelper.giveItemToPlayer(context.getSource().asPlayer(), new ItemStack(Objects.Items.QUESTING_DEVICE, 1));
                        }catch(CommandSyntaxException e){
                            e.printStackTrace();
                        }
                        return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSource, ?> registerEditorCommand(){
        return Commands.literal("editor")
                .executes(context->{
                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.wip"), false);
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSource, ?> registerSettingsCommand(){
        return Commands.literal("settings")
                .requires(cs->{
                    if(cs.getEntity() instanceof ServerPlayerEntity){
                        return cs.hasPermissionLevel(2) || (cs.getEntity()).getDisplayName().getString().equals("vincentmet");//For troubleshooting
                    }
                    return cs.hasPermissionLevel(2);
                })
                .then(Commands.literal("can_reward_only_be_claimed_once")
                     .then(Commands.argument("value", BoolArgumentType.bool())
                           .executes(context -> {
                               Config.ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE = BoolArgumentType.getBool(context, "value");
                               context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.settings.set", "can_reward_only_be_claimed_once", TextFormatting.GOLD + String.valueOf(Config.SidedConfig.canRewardOnlyBeClaimedOnce())), false);
                               ServerUtils.sendServerConfigToAllPlayers();
                               return 0;
                           })
                     )
                     .executes(context -> {
                         context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.settings.get", "can_reward_only_be_claimed_once", TextFormatting.GOLD + String.valueOf(Config.SidedConfig.canRewardOnlyBeClaimedOnce())), false);
                         return 0;
                     })
                )
                .then(Commands.literal("edit_mode")
                     .then(Commands.argument("value", BoolArgumentType.bool())
                           .executes(context -> {
                               Config.ServerConfig.EDIT_MODE = BoolArgumentType.getBool(context, "value");
                               context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.settings.set", "edit_mode", TextFormatting.GOLD + String.valueOf(Config.SidedConfig.isEditModeOnByDefault())), false);
                               ServerUtils.sendServerConfigToAllPlayers();
                               return 0;
                           })
                     )
                     .executes(context -> {
                         context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.settings.get", "edit_mode", TextFormatting.GOLD + String.valueOf(Config.SidedConfig.isEditModeOnByDefault())), false);
                         return 0;
                     })
                )
                .then(Commands.literal("give_device_on_first_login")
                     .then(Commands.argument("value", BoolArgumentType.bool())
                           .executes(context -> {
                               Config.ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN = BoolArgumentType.getBool(context, "value");
                               context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.settings.set", "give_device_on_first_login", TextFormatting.GOLD + String.valueOf(Config.SidedConfig.giveDeviceOnFirstLogin())), false);
                               ServerUtils.sendServerConfigToAllPlayers();
                               return 0;
                           })
                     )
                     .executes(context -> {
                         context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.settings.get", "give_device_on_first_login", TextFormatting.GOLD + String.valueOf(Config.SidedConfig.giveDeviceOnFirstLogin())), false);
                         return 0;
                     })
                )
                .then(Commands.literal("debug_mode")
                     .then(Commands.argument("value", BoolArgumentType.bool())
                           .executes(context -> {
                               Config.ServerConfig.DEBUG_MODE = BoolArgumentType.getBool(context, "value");
                               context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.settings.set", "debug_mode", TextFormatting.GOLD + String.valueOf(Config.SidedConfig.isDebugModeOn())), false);
                               ServerUtils.sendServerConfigToAllPlayers();
                               return 0;
                           })
                     )
                     .executes(context -> {
                         context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.settings.get", "debug_mode", TextFormatting.GOLD + String.valueOf(Config.SidedConfig.isDebugModeOn())), false);
                         return 0;
                     })
                )
                .then(Commands.literal("backups")
                     .then(Commands.argument("value", BoolArgumentType.bool())
                           .executes(context -> {
                               Config.ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN = BoolArgumentType.getBool(context, "value");
                               context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.settings.set", "backups", TextFormatting.GOLD + String.valueOf(Config.SidedConfig.areBackupsEnabled())), false);
                               ServerUtils.sendServerConfigToAllPlayers();
                               return 0;
                           })
                     )
                     .executes(context -> {
                         context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.settings.get", "backups", TextFormatting.GOLD + String.valueOf(Config.SidedConfig.areBackupsEnabled())), false);
                         return 0;
                     })
                )
                .executes(context->{
                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.use_subcommand"), false);
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSource, ?> registerInfoCommand(){
        return Commands.literal("info")
                .executes(context->{
                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.info.modid", Ref.MODID), false);
                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.info.mod_version", Ref.VERSION_MOD), false);
                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.info.mc_version", Ref.VERSION_MC), false);
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSource, ?> registerReloadCommand(){
        return Commands.literal("reload")
                .requires(cs->{
                    if(cs.getEntity() instanceof ServerPlayerEntity){
                        return cs.hasPermissionLevel(2) || (cs.getEntity()).getDisplayName().getString().equals("vincentmet");//For troubleshooting
                    }
                    return cs.hasPermissionLevel(2);
                })
                .executes(context->{
                    MinecraftForge.EVENT_BUS.post(new DataLoadingEvent.Pre());
                    CQHelper.readAllFilesAndPutIntoHashmaps();
                    MinecraftForge.EVENT_BUS.post(new DataLoadingEvent.Post());
                    context.getSource().getServer().getPlayerList().getPlayers().forEach(playerEntity->{
                        CQHelper.generateMissingProgress(playerEntity.getUniqueID());
                        CQHelper.generateMissingPartyProgress();
                        ServerUtils.sendQuestsAndChapters(playerEntity);
                        ServerUtils.sendProgressAndParties(playerEntity);
                    });
                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.reload.success"), false);
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSource, ?> registerQuestsCommand(){
        return Commands.literal("quests")//modify default quests /// remove, add, dupe, edit, stuff like that
                .executes(context->{
                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.wip"), false);
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSource, ?> registerUuidCommand(){
        return Commands.literal("uuid")
                .executes(context->{
                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.uuid", context.getSource().asPlayer().getUniqueID().toString()), false);
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSource, ?> registerDiscordCommand(){
        return Commands.literal("discord")
                .executes(context->{
                    try{
                        ServerPlayerEntity player = context.getSource().asPlayer();
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(()->player), new MessageDiscord());
                    }catch(CommandSyntaxException e){
                        e.printStackTrace();
                    }
                    return 0;
                })
        ;
    }
}
