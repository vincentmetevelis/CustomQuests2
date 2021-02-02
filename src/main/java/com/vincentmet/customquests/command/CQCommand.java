package com.vincentmet.customquests.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
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
                //.then(registerSettingsCommand())
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
                .then(Commands.literal("create")
                    .then(Commands.argument("party_name", StringArgumentType.string())
                        .executes(context -> {
                            String name = context.getArgument("party_name", String.class);
                            if(Pattern.matches("[A-Za-z0-9]+?", name)){
                                int partyId = PartyHelper.createParty(context.getSource().asPlayer().getUniqueID(), name);
                                if(partyId == -1){
                                    context.getSource().sendFeedback(new StringTextComponent("You're already in a party, leave it first before creating a new one!"), false);
                                }else{
                                    String devModeMessage = Ref.DEV_MODE ? " (ID: "+partyId+", name:'" + name + "')" : "";
                                    context.getSource().sendFeedback(new StringTextComponent("Successfully created a party!" + devModeMessage), false);
                                    context.getSource().getServer().getPlayerList().getPlayers().forEach(ServerUtils::sendProgressAndParties);
                                }
                            }else{
                                context.getSource().sendErrorMessage(new StringTextComponent("Invalid party name, please only use [A-Za-z0-9]!"));
                            }
                            return 0;
                        })
                    )
                    .executes(context -> {
                        context.getSource().sendFeedback(new StringTextComponent("Please specify a party name!"), false);
                        return 0;
                    })
                )
                .then(Commands.literal("leave")
                    .executes(context -> {
                        UUID uuid = context.getSource().asPlayer().getUniqueID();
                        int playerParty = ProgressHelper.getPlayerParty(uuid);
                        List<UUID> uuidsInParty = PartyHelper.getAllUUIDsInParty(playerParty);
                        if(playerParty == Ref.NO_PARTY){
                            context.getSource().sendFeedback(new StringTextComponent("You aren't in a party, so can't leave one!"), false);
                            return 0;
                        }
                        if(PartyHelper.isPlayerPartyOwner(uuid, playerParty) && uuidsInParty.size() > 1){
                            context.getSource().sendFeedback(new StringTextComponent("You are the party owner, please assign someone else before leaving! (/customquests party setowner <player>)"), false);
                            return 0;
                        }
                        if(uuidsInParty.size() == 1){ //If player is last one in party
                            ProgressHelper.setPlayerParty(uuid, Ref.NO_PARTY);
                            PartyHelper.deleteParty(playerParty);
                            context.getSource().sendFeedback(new StringTextComponent("Party deleted!"), false);
                            return 0;
                        }
                        ProgressHelper.setPlayerParty(uuid, Ref.NO_PARTY);
                        context.getSource().sendFeedback(new StringTextComponent("Leaving party..."), false);
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
                                context.getSource().sendFeedback(new StringTextComponent("You're not in a party..."), false);
                            }else if(newOwner.getUniqueID().equals(uuid)){
                                context.getSource().sendFeedback(new StringTextComponent("You're already the owner of the party..."), false);
                            }else if(PartyHelper.isPlayerPartyOwner(uuid, playerParty) && ProgressHelper.getPlayerParty(newOwner.getUniqueID()) == playerParty){
                                PartyHelper.setPartyOwner(newOwner.getUniqueID(), playerParty);
                                context.getSource().sendFeedback(new StringTextComponent("Successfully made '" + newOwner.getDisplayName().getString() + "' the new party owner!"), false);
                            }else if(ProgressHelper.getPlayerParty(newOwner.getUniqueID()) == playerParty){
                                context.getSource().sendFeedback(new StringTextComponent("You are not the owner of the party!"), false);
                            }else{
                                context.getSource().sendFeedback(new StringTextComponent("The specified player is not in your party!"), false);
                            }
                            return 0;
                        })
                    )
                    .executes(context -> {
                        context.getSource().sendFeedback(new StringTextComponent("Please specify a new owner!"), false);
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
                                     context.getSource().sendFeedback(new StringTextComponent("You're already the party owner, you can't invite yourself..."), false);
                                 }else if(PartyInviteCache.isPlayerInvitedToAnyParty(invitedPlayerUUID)){
                                     context.getSource().sendFeedback(new StringTextComponent("Given player already has a pending invite, they have to deny it first before you can send them a new one!"), false);
                                 }else if(!PartyHelper.isPlayerPartyOwner(inviterUUID, partyId)){
                                     context.getSource().sendFeedback(new StringTextComponent("Only the party owner can invite new people to the party!"), false);
                                 }else{
                                     PartyInviteCache.addInvite(invitedPlayerUUID, partyId);
                                     context.getSource().sendFeedback(new StringTextComponent("You invited '" + invitedPlayer.getDisplayName().getString() + "' to your party!"), false);
                                     invitedPlayer.sendMessage(new StringTextComponent(inviter.getDisplayName().getString() + " invited you to their party. Use '/customquests party accept' to accept or '/customquests party deny' to deny the invite!"), inviterUUID);
                                 }
                             }
                             return 0;
                         })
                     )
                     .executes(context -> {
                         context.getSource().sendFeedback(new StringTextComponent("Please specify the online player you want to invite!"), false);
                         return 0;
                     })
                )
                .then(Commands.literal("accept")
                    .executes(context -> {
                        UUID accepter = context.getSource().asPlayer().getUniqueID();
                        if(ProgressHelper.isPlayerInParty(accepter)){
                            context.getSource().sendFeedback(new StringTextComponent("Leave current party first before accepting a new invite!"), false);
                        }else if(!PartyInviteCache.isPlayerInvitedToAnyParty(accepter)){
                            context.getSource().sendFeedback(new StringTextComponent("You're not invited to any party! :("), false);
                        }else{
                            int newPartyId = PartyInviteCache.getPartyForInvite(accepter);
                            ProgressHelper.setPlayerParty(accepter, newPartyId);
                            context.getSource().sendFeedback(new StringTextComponent("You successfully joined a party!"), false);
                        }
                        return 0;
                    })
                )
                .then(Commands.literal("deny")
                    .executes(context -> {
                        UUID denier = context.getSource().asPlayer().getUniqueID();
                        if(ProgressHelper.isPlayerInParty(denier)){
                            context.getSource().sendFeedback(new StringTextComponent("Cant deny an invite because you're already in a party!"), false);
                        }else if(PartyInviteCache.isPlayerInvitedToAnyParty(denier)){
                            context.getSource().sendFeedback(new StringTextComponent("You're not invited to any party! :("), false);
                        }else{
                            PartyInviteCache.removePlayerInvite(denier);
                            context.getSource().sendFeedback(new StringTextComponent("You successfully denied the party invite! #SocialDistancing"), false);
                        }
                        return 0;
                    })
                )
                .executes(context->{
                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.wip"), false);
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
                                    QuestingStorage.getSidedPartiesMap().forEach((partyId, party) -> {
                                        party.getCollectivelyCompletedQuestList().clear();
                                        party.getCollectiveProgress().clear();
                                        CQHelper.generateMissingPartyProgress();
                                    });
                                    context.getSource().getServer().getPlayerList().getPlayers().forEach(ServerUtils::sendProgressToClient);
                                    context.getSource().getServer().getPlayerList().getPlayers().forEach(ServerUtils::sendPartiesToClient);
                                    context.getSource().sendFeedback(new StringTextComponent("Successfully deleted all player and party progress!"), false);
                                    return 0;
                                })
                        )
                        .then(Commands.literal("player")
                            .then(Commands.argument("player_name", EntityArgument.players())
                                .executes(context -> {
                                    Collection<ServerPlayerEntity> serverPlayerEntities = EntityArgument.getPlayers(context, "player_name");
                                    serverPlayerEntities.forEach(playerEntity -> {
                                    
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
                .executes(context->{
                    context.getSource().sendFeedback(new TranslationTextComponent(Ref.MODID + ".command.wip"), false);//todo add config settings here
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSource, ?> registerInfoCommand(){
        return Commands.literal("info")
                .executes(context->{
                    context.getSource().sendFeedback(new StringTextComponent("Modid: " + Ref.MODID), false);
                    context.getSource().sendFeedback(new StringTextComponent("Mod Version: " + Ref.VERSION_MOD), false);
                    context.getSource().sendFeedback(new StringTextComponent("MC Version: " + Ref.VERSION_MC), false);
                    return 0;
                })
        ;
    }
    
    public static ArgumentBuilder<CommandSource, ?> registerReloadCommand(){
        return Commands.literal("reload")
                .requires(cs->{
                    try{
                        return cs.hasPermissionLevel(2) || cs.asPlayer().getDisplayName().getString().equals("vincentmet");//For troubleshooting
                    }catch(CommandSyntaxException e){
                        e.printStackTrace();
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
                    context.getSource().sendFeedback(new StringTextComponent("Your UUID is: " + context.getSource().asPlayer().getUniqueID().toString()), false);
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
