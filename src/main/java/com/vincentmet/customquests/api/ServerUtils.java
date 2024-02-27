package com.vincentmet.customquests.api;

import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.network.messages.MessageReinitQuestingCanvas;
import com.vincentmet.customquests.network.messages.PacketHandler;
import com.vincentmet.customquests.network.messages.sync.*;
import com.vincentmet.customquests.network.messages.sync.stc.clear.MessageStcSyncTempClearAllChapters;
import com.vincentmet.customquests.network.messages.sync.stc.clear.MessageStcSyncTempClearAllParties;
import com.vincentmet.customquests.network.messages.sync.stc.clear.MessageStcSyncTempClearAllPlayers;
import com.vincentmet.customquests.network.messages.sync.stc.clear.MessageStcSyncTempClearAllQuests;
import com.vincentmet.customquests.network.messages.sync.stc.delete.*;
import com.vincentmet.customquests.network.messages.sync.stc.update.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

public class ServerUtils{
    public static class Packets{
        public static class Delete{//These actually delete the chapter/quests/etc., including all the references (i.e. progress)
            public static void deleteEverythingAtAllClients(){
                deleteAllChaptersAtAllClients();
                deleteAllQuestsAtAllClients();
            }

            public static void deleteAllChaptersAtAllClients(){
                Ref.currentServerInstance.getPlayerList().getPlayers().forEach(Delete::deleteAllChaptersAtSingleClient);
            }

            public static void deleteAllChaptersAtSingleClient(ServerPlayer player){
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncDeleteAllChapters());
            }

            public static void deleteAllQuestsAtAllClients(){
                Ref.currentServerInstance.getPlayerList().getPlayers().forEach(Delete::deleteAllQuestsAtSingleClient);
            }

            public static void deleteAllQuestsAtSingleClient(ServerPlayer player){
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncDeleteAllQuests());
            }

            public static void deleteSinglePartyAtAllClients(int partyId){
                Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> deleteSinglePartyAtSingleClient(player, partyId));
            }

            public static void deleteSinglePartyAtSingleClient(ServerPlayer player, int partyId){
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncDeleteSingleParty(partyId));
            }

            public static void deleteSinglePlayerAtAllClients(UUID playerId){
                Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> deleteSinglePlayerAtSingleClient(player, playerId));
            }

            public static void deleteSinglePlayerAtSingleClient(ServerPlayer player, UUID playerId){
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncDeleteSinglePlayer(playerId));
            }

            public static void deleteSingleChapterAtAllClients(int chapterId){
                Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> deleteSingleChapterAtSingleClient(player, chapterId));
            }

            public static void deleteSingleChapterAtSingleClient(ServerPlayer player, int chapterId){
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncDeleteSingleChapter(chapterId));
            }

            public static void deleteSingleQuestAtAllClients(int questId){
                Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> deleteSingleQuestAtSingleClients(player, questId));
            }

            public static void deleteSingleQuestAtSingleClients(ServerPlayer player, int questId){
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncDeleteSingleQuest(questId));
            }

            public static void deleteSingleTaskAtAllClients(int questId, int taskId){
                Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> deleteSingleTaskAtSingleClient(player, questId, taskId));
            }

            public static void deleteSingleTaskAtSingleClient(ServerPlayer player, int questId, int taskId){
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncDeleteSingleTask(questId, taskId));
            }

            public static void deleteSingleSubtaskAtAllClients(int questId, int taskId, int subtaskId){
                Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> deleteSingleSubtaskAtSingleClient(player, questId, taskId, subtaskId));
            }

            public static void deleteSingleSubtaskAtSingleClient(ServerPlayer player, int questId, int taskId, int subtaskId){
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncDeleteSingleSubtask(questId, taskId, subtaskId));
            }

            public static void deleteSingleRewardAtAllClients(int questId, int rewardId){
                Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> deleteSingleRewardAtSingleClient(player, questId, rewardId));
            }

            public static void deleteSingleRewardAtSingleClient(ServerPlayer player, int questId, int rewardId){
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncDeleteSingleReward(questId, rewardId));
            }

            public static void deleteSingleSubrewardAtAllClients(int questId, int rewardId, int subrewardId){
                Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> deleteSingleSubrewardAtSingleClient(player, questId, rewardId, subrewardId));
            }

            public static void deleteSingleSubrewardAtSingleClient(ServerPlayer player, int questId, int rewardId, int subrewardId){
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncDeleteSingleSubreward(questId, rewardId, subrewardId));
            }
        }

        public static class SyncToClient{//These temporarily remove the quest, and then rebuild the quest from memory with the updated values form the server
            public static class Progress{
                public static void syncAllProgressAndPartiesToPlayers(){
                    Players.syncAllPlayerProgress();
                    Parties.syncAllParties();
                }

                public static void syncAllProgressAndPartiesToPlayer(ServerPlayer player){
                    Players.syncAllPlayerProgressToPlayer(player);
                    Parties.syncAllPartiesToPlayer(player);
                }

                public static class Parties{
                    public static void syncAllParties(){
                        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(Parties::syncAllPartiesToPlayer);
                    }

                    public static void syncAllPartiesToPlayer(ServerPlayer player){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncTempClearAllParties());
                        QuestingStorage.getSidedPartiesMap().keySet().forEach(partyId -> syncPartyToPlayer(player, partyId));
                    }

                    public static void syncParty(int partyId){
                        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> {
                            syncPartyToPlayer(player, partyId);
                        });
                    }

                    public static void syncPartyToPlayer(ServerPlayer player, int partyId){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSingleParty(partyId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }
                }

                public static class Players{
                    public static void syncAllPlayerProgress(){
                        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(Players::syncAllPlayerProgressToPlayer);
                    }

                    public static void syncAllPlayerProgressToPlayer(ServerPlayer player){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncTempClearAllPlayers());
                        QuestingStorage.getSidedPlayersMap().keySet().forEach(uuid -> syncPlayerToPlayer(player, UUID.fromString(uuid)));
                    }

                    public static void syncPlayer(UUID uuid){
                        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> syncPlayerToPlayer(player, uuid));
                    }

                    public static void syncPlayerToPlayer(ServerPlayer player, UUID uuid){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSinglePlayer(uuid));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }
                }
            }

            public static class Data{
                public static void syncAllChaptersAndQuests(){
                    Ref.currentServerInstance.getPlayerList().getPlayers().forEach(Data::syncAllChaptersAndQuestsToPlayer);
                }

                public static void syncAllChaptersAndQuestsToPlayer(ServerPlayer player){
                    Quests.syncAllQuestsToPlayer(player);
                    Chapters.syncAllChaptersToPlayer(player);
                }

                public static class Quests{
                    public static void syncAllQuests(){
                        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(Quests::syncAllQuestsToPlayer);
                    }

                    public static void syncAllQuestsToPlayer(ServerPlayer player){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncTempClearAllQuests());
                        QuestingStorage.getSidedQuestsMap().keySet().forEach(questId -> syncQuestToPlayer(player, questId));
                    }

                    public static void syncQuest(int questId){//todo maybe add tasks in for loop instead of full quest sync, otherwise datacap crash will happen again
                        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> syncQuestToPlayer(player, questId));
                    }

                    public static void syncQuestToPlayer(ServerPlayer player, int questId){//todo maybe add tasks in for loop instead of full quest sync, otherwise datacap crash will happen again
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSingleQuest(questId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }

                    public static void syncTask(int questId, int taskId){//todo maybe add subtasks tasks in for loop instead of full task sync, otherwise datacap crash will happen again
                        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> syncTaskToPlayer(player, questId, taskId));
                    }

                    public static void syncTaskToPlayer(ServerPlayer player, int questId, int taskId){//todo maybe add subtasks in for loop instead of full task sync, otherwise datacap crash will happen again
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSingleTask(questId, taskId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }

                    public static void syncSubtask(int questId, int taskId, int subtaskId){
                        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> syncSubtaskToPlayer(player, questId, taskId, subtaskId));
                    }

                    public static void syncSubtaskToPlayer(ServerPlayer player, int questId, int taskId, int subtaskId){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSingleSubtask(questId, taskId, subtaskId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }

                    public static void syncReward(int questId, int rewardId){//todo maybe add subrewards tasks in for loop instead of full reward sync, otherwise datacap crash will happen again
                        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> syncRewardToPlayer(player, questId, rewardId));
                    }

                    public static void syncRewardToPlayer(ServerPlayer player, int questId, int rewardId){//todo maybe add subrewards in for loop instead of full reward sync, otherwise datacap crash will happen again
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSingleReward(questId, rewardId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }

                    public static void syncSubreward(int questId, int rewardId, int subrewardId){
                        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> {
                            syncSubrewardToPlayer(player, questId, rewardId, subrewardId);
                        });
                    }

                    public static void syncSubrewardToPlayer(ServerPlayer player, int questId, int rewardId, int subrewardId){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSingleSubreward(questId, rewardId, subrewardId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }
                }

                public static class Chapters{
                    public static void syncAllChapters(){
                        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(Chapters::syncAllChaptersToPlayer);
                    }

                    public static void syncAllChaptersToPlayer(ServerPlayer player){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncTempClearAllChapters());
                        QuestingStorage.getSidedChaptersMap().keySet().forEach(chapterId -> syncChapterToPlayer(player, chapterId));
                    }

                    public static void syncChapter(int chapterId){
                        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> {
                            syncChapterToPlayer(player, chapterId);
                        });
                    }

                    public static void syncChapterToPlayer(ServerPlayer player, int chapterId){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSingleChapter(chapterId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }
                }
            }

            public static class Config{
                public static void syncConfigToAllPlayers(){
                    Ref.currentServerInstance.getPlayerList().getPlayers().forEach(Config::syncConfigToPlayer);
                }

                public static void syncConfigToPlayer(ServerPlayer player){
                    PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageUpdateServerSettings(com.vincentmet.customquests.Config.ServerConfig.EDIT_MODE, com.vincentmet.customquests.Config.ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE, com.vincentmet.customquests.Config.ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN));
                }
            }
        }
    }
}