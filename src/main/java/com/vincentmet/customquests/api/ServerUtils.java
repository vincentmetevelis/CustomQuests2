package com.vincentmet.customquests.api;

import com.vincentmet.customquests.network.messages.MessageReinitQuestingCanvas;
import com.vincentmet.customquests.network.messages.PacketHandler;
import com.vincentmet.customquests.network.messages.sync.*;
import com.vincentmet.customquests.network.messages.sync.stc.clear.MessageStcSyncTempClearAllChapters;
import com.vincentmet.customquests.network.messages.sync.stc.clear.MessageStcSyncTempClearAllParties;
import com.vincentmet.customquests.network.messages.sync.stc.clear.MessageStcSyncTempClearAllPlayers;
import com.vincentmet.customquests.network.messages.sync.stc.clear.MessageStcSyncTempClearAllQuests;
import com.vincentmet.customquests.network.messages.sync.stc.delete.MessageStcSyncDeleteAllChapters;
import com.vincentmet.customquests.network.messages.sync.stc.delete.MessageStcSyncDeleteAllQuests;
import com.vincentmet.customquests.network.messages.sync.stc.update.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

public class ServerUtils{
    public static class Packets{
        public static class Delete{//These actually delete the chapter/quests/etc, including all the references (i.e. progress)
            public static void deleteEverythingAtAllClients(){
                deleteAllChaptersAtAllClients();
                deleteAllQuestsAtAllClients();
            }

            public static void deleteAllChaptersAtAllClients(){
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageStcSyncDeleteAllChapters());
            }

            public static void deleteAllQuestsAtAllClients(){
                PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageStcSyncDeleteAllQuests());
            }

            public static void deleteSinglePartyAtAllClients(int partyId){
                //todo
            }

            public static void deleteSinglePlayerAtAllClients(UUID playerId){
                //todo
            }

            public static void deleteSingleChapterAtAllClients(int chapterId){
                //todo
            }

            public static void deleteSingleQuestAtAllClients(int questId){
                //todo
            }

            public static void deleteSingleTaskAtAllClients(int questId, int taskId){
                //todo
            }

            public static void deleteSingleSubtaskAtAllClients(int questId, int taskId, int subtaskId){
                //todo
            }

            public static void deleteSingleRewardAtAllClients(int questId, int rewardId){
                //todo
            }

            public static void deleteSingleSubrewardAtAllClients(int questId, int rewardId, int subrewardId){
                //todo
            }
        }

        public static class Create{
            //todo
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
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageStcSyncTempClearAllParties());
                        QuestingStorage.getSidedPartiesMap().keySet().forEach(Parties::syncParty);
                    }

                    public static void syncAllPartiesToPlayer(ServerPlayer player){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncTempClearAllParties());
                        QuestingStorage.getSidedPartiesMap().keySet().forEach(partyId -> syncPartyToPlayer(player, partyId));
                    }

                    public static void syncParty(int partyId){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageStcSyncUpdateSingleParty(partyId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageReinitQuestingCanvas());
                    }

                    public static void syncPartyToPlayer(ServerPlayer player, int partyId){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSingleParty(partyId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }
                }

                public static class Players{
                    public static void syncAllPlayerProgress(){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageStcSyncTempClearAllPlayers());
                        QuestingStorage.getSidedPlayersMap().keySet().forEach(uuid -> syncPlayer(UUID.fromString(uuid)));
                    }

                    public static void syncAllPlayerProgressToPlayer(ServerPlayer player){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncTempClearAllPlayers());
                        QuestingStorage.getSidedPlayersMap().keySet().forEach(uuid -> syncPlayerToPlayer(player, UUID.fromString(uuid)));
                    }

                    public static void syncPlayer(UUID uuid){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageStcSyncUpdateSinglePlayer(uuid));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageReinitQuestingCanvas());
                    }

                    public static void syncPlayerToPlayer(ServerPlayer player, UUID uuid){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSinglePlayer(uuid));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }
                }
            }

            public static class Data{
                public static void syncAllChaptersAndQuests(){
                    Quests.syncAllQuests();
                    Chapters.syncAllChapters();
                }

                public static void syncAllChaptersAndQuestsToPlayer(ServerPlayer player){
                    Quests.syncAllQuestsToPlayer(player);
                    Chapters.syncAllChaptersToPlayer(player);
                }

                public static class Quests{
                    public static void syncAllQuests(){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageStcSyncTempClearAllQuests());
                        QuestingStorage.getSidedQuestsMap().keySet().forEach(Quests::syncQuest);
                    }

                    public static void syncAllQuestsToPlayer(ServerPlayer player){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncTempClearAllQuests());
                        QuestingStorage.getSidedQuestsMap().keySet().forEach(questId -> syncQuestToPlayer(player, questId));
                    }

                    public static void syncQuest(int questId){//todo maybe add tasks in for loop instead of full quest sync, otherwise datacap crash will happen again
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageStcSyncUpdateSingleQuest(questId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageReinitQuestingCanvas());
                    }

                    public static void syncQuestToPlayer(ServerPlayer player, int questId){//todo maybe add tasks in for loop instead of full quest sync, otherwise datacap crash will happen again
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSingleQuest(questId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }

                    public static void syncTask(int questId, int taskId){//todo maybe add subtasks tasks in for loop instead of full task sync, otherwise datacap crash will happen again
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageStcSyncUpdateSingleTask(questId, taskId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageReinitQuestingCanvas());
                    }

                    public static void syncTaskToPlayer(ServerPlayer player, int questId, int taskId){//todo maybe add subtasks in for loop instead of full task sync, otherwise datacap crash will happen again
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSingleTask(questId, taskId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }

                    public static void syncSubtask(int questId, int taskId, int subtaskId){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageStcSyncUpdateSingleSubtask(questId, taskId, subtaskId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageReinitQuestingCanvas());
                    }

                    public static void syncSubtaskToPlayer(ServerPlayer player, int questId, int taskId, int subtaskId){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSingleSubtask(questId, taskId, subtaskId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }

                    public static void syncReward(int questId, int rewardId){//todo maybe add subrewards tasks in for loop instead of full reward sync, otherwise datacap crash will happen again
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageStcSyncUpdateSingleReward(questId, rewardId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageReinitQuestingCanvas());
                    }

                    public static void syncRewardToPlayer(ServerPlayer player, int questId, int rewardId){//todo maybe add subrewards in for loop instead of full reward sync, otherwise datacap crash will happen again
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSingleReward(questId, rewardId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }

                    public static void syncSubreward(int questId, int rewardId, int subrewardId){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageStcSyncUpdateSingleSubreward(questId, rewardId, subrewardId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageReinitQuestingCanvas());
                    }

                    public static void syncSubrewardToPlayer(ServerPlayer player, int questId, int rewardId, int subrewardId){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSingleSubreward(questId, rewardId, subrewardId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }
                }

                public static class Chapters{
                    public static void syncAllChapters(){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageStcSyncTempClearAllChapters());
                        QuestingStorage.getSidedChaptersMap().keySet().forEach(Chapters::syncChapter);
                    }

                    public static void syncAllChaptersToPlayer(ServerPlayer player){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncTempClearAllChapters());
                        QuestingStorage.getSidedChaptersMap().keySet().forEach(chapterId -> syncChapterToPlayer(player, chapterId));
                    }

                    public static void syncChapter(int chapterId){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageStcSyncUpdateSingleChapter(chapterId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageReinitQuestingCanvas());
                    }

                    public static void syncChapterToPlayer(ServerPlayer player, int chapterId){
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageStcSyncUpdateSingleChapter(chapterId));
                        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageReinitQuestingCanvas());
                    }
                }
            }

            public static class Config{
                public static void syncConfigToAllPlayers(){
                    PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.noArg(), new MessageUpdateServerSettings(com.vincentmet.customquests.Config.ServerConfig.EDIT_MODE, com.vincentmet.customquests.Config.ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE, com.vincentmet.customquests.Config.ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN));
                }

                public static void syncConfigToPlayer(ServerPlayer player){
                    PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MessageUpdateServerSettings(com.vincentmet.customquests.Config.ServerConfig.EDIT_MODE, com.vincentmet.customquests.Config.ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE, com.vincentmet.customquests.Config.ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN));
                }
            }
        }
    }
}