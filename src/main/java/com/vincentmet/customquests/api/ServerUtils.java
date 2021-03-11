package com.vincentmet.customquests.api;

import com.vincentmet.customquests.*;
import com.vincentmet.customquests.network.messages.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public class ServerUtils{
    public static void reloadClientResources(ServerPlayerEntity player){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReloadResources());
    }
    
    public static void sendQuestsToClient(ServerPlayerEntity player){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageClearQuests());
        QuestingStorage.getSidedQuestsMap().forEach((questId, quest) -> sendQuestToClient(player, questId));
        reloadClientResources(player);
    }
    
    public static void sendQuestToClient(ServerPlayerEntity player, int questId){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(()->player), new MessageUpdateSingleQuest(questId));
    }
    
    public static void sendChaptersToClient(ServerPlayerEntity player){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageClearChapters());
        QuestingStorage.getSidedChaptersMap().forEach((chapterId, chapter) -> sendChapterToClient(player, chapterId));
    }
    
    public static void sendChapterToClient(ServerPlayerEntity player, int chapterId){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageUpdateSingleChapter(chapterId));
    }
    
    public static void sendProgressToClient(ServerPlayerEntity player){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageClearPlayers());
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageUpdateSinglePlayer(player.getUniqueID().toString()));
    }
    
    public static void sendPartiesToClient(ServerPlayerEntity player){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageClearParties());
        QuestingStorage.getSidedPartiesMap().forEach((partyId, party) -> sendPartyToClient(player, partyId));
    }
    
    public static void sendPartyToClient(ServerPlayerEntity player, int partyId){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageUpdateSingleParty(partyId));
    }
    
    public static void sendProgressAndParties(ServerPlayerEntity player){
        sendProgressToClient(player);
        sendPartiesToClient(player);
    }
    
    public static void sendQuestsAndChapters(ServerPlayerEntity player){
        sendQuestsToClient(player);
        sendChaptersToClient(player);
    }
    
    public static void sendServerConfigToClient(ServerPlayerEntity player){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageUpdateServerSettings(Config.ServerConfig.EDIT_MODE, Config.ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE, Config.ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN));
    }
    
    public static void sendServerConfigToAllPlayers(){
        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(ServerUtils::sendServerConfigToClient);
    }
}