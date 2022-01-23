package com.vincentmet.customquests.api;

import com.vincentmet.customquests.Config;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.network.messages.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class ServerUtils{
    public static void sendQuestsToClient(ServerPlayer player){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageClearQuests());
        QuestingStorage.getSidedQuestsMap().forEach((questId, quest) -> sendQuestToClient(player, questId));
    }
    
    public static void sendQuestToClient(ServerPlayer player, int questId){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(()->player), new MessageUpdateSingleQuest(questId));
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
    }
    
    public static void sendChaptersToClient(ServerPlayer player){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageClearChapters());
        QuestingStorage.getSidedChaptersMap().forEach((chapterId, chapter) -> sendChapterToClient(player, chapterId));
    }
    
    public static void clearSingleChapterAtClient(ServerPlayer player, int chapterId){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageClearSingleChapter(chapterId));
    }
    
    public static void clearSingleQuestAtClient(ServerPlayer player, int questId){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageClearSingleQuest(questId));
    }
    
    public static void sendChapterToClient(ServerPlayer player, int chapterId){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageUpdateSingleChapter(chapterId));
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
    }
    
    public static void sendProgressToClient(ServerPlayer player){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageClearPlayers());
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageUpdateSinglePlayer(player.getStringUUID()));
        QuestingStorage.getSidedPlayersMap().get(player.getStringUUID()).getIndividualProgress().forEach((progressQuestId, progressQuest) -> sendProgressSingleQuestToClient(player, progressQuestId));
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
    }
    
    public static void sendProgressSingleQuestToClient(ServerPlayer player, int questId){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageUpdateSinglePlayerQuestProgress(player.getStringUUID(), questId));
    }
    
    public static void sendPartiesToClient(ServerPlayer player){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageClearParties());
        QuestingStorage.getSidedPartiesMap().forEach((partyId, party) -> sendPartyToClient(player, partyId));
    }
    
    public static void sendPartyToClient(ServerPlayer player, int partyId){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageUpdateSingleParty(partyId));
    }
    
    public static void sendProgressAndParties(ServerPlayer player){
        sendProgressToClient(player);
        sendPartiesToClient(player);
    }
    
    public static void sendQuestsAndChapters(ServerPlayer player){
        sendQuestsToClient(player);
        sendChaptersToClient(player);
    }
    
    public static void sendServerConfigToClient(ServerPlayer player){
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageUpdateServerSettings(Config.ServerConfig.EDIT_MODE, Config.ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE, Config.ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN));
    }
    
    public static void sendServerConfigToAllPlayers(){
        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(ServerUtils::sendServerConfigToClient);
    }
    
    public static void sendAllChaptersToAllPlayers(){
        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(ServerUtils::sendChaptersToClient);
    }
    
    public static void sendChapterToAllPlayers(int chapterId){
        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> ServerUtils.sendChapterToClient(player, chapterId));
    }
    
    public static void sendAllQuestsToAllPlayers(){
        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(ServerUtils::sendQuestsToClient);
    }
    
    public static void sendQuestToAllPlayers(int questId){
        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> ServerUtils.sendQuestToClient(player, questId));
    }
}