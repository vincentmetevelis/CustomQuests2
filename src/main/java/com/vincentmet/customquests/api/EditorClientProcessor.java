package com.vincentmet.customquests.api;

import com.google.gson.JsonObject;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.hierarchy.chapter.Chapter;
import com.vincentmet.customquests.hierarchy.party.Party;
import com.vincentmet.customquests.hierarchy.party.SingleQuestPartyProgress;
import com.vincentmet.customquests.hierarchy.progress.QuestingPlayer;
import com.vincentmet.customquests.hierarchy.progress.SingleQuestUserProgress;
import com.vincentmet.customquests.hierarchy.progress.UserProgress;
import com.vincentmet.customquests.hierarchy.quest.*;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class EditorClientProcessor {
    public static class Update{
        public static class Chapters{
            public static void updateSingleChapter(int chapterId, JsonObject json){
                Clear.Chapters.clearSingleChapter(chapterId);
                Create.createChapter(chapterId, json);
            }
        }

        public static class Quests{
            public static void updateSingleQuest(int questId, JsonObject json){
                Clear.Quests.clearSingleQuest(questId);
                Create.createQuest(questId, json);
            }

            public static void updateSingleTask(int questId, int taskId, JsonObject json){
                Clear.Quests.Tasks.clearSingleTask(questId, taskId);
                Create.createTask(questId, taskId, json);
            }

            public static void updateSingleSubtask(int questId, int taskId, int subtaskId, JsonObject json){
                Clear.Quests.Tasks.Subtasks.clearSingleSubtask(questId, taskId, subtaskId);
                Create.createSubtask(questId, taskId, subtaskId, json);
            }

            public static void updateSingleReward(int questId, int rewardId, JsonObject json){
                Clear.Quests.Rewards.clearSingleReward(questId, rewardId);
                Create.createReward(questId, rewardId, json);
            }

            public static void updateSingleSubreward(int questId, int rewardId, int subrewardId, JsonObject json){
                Clear.Quests.Rewards.Subrewards.clearSingleSubreward(questId, rewardId, subrewardId);
                Create.createSubreward(questId, rewardId, subrewardId, json);
            }
        }

        public static class Parties{
            public static void updateSingleParty(int partyId, JsonObject json){
                Clear.Parties.clearSingleParty(partyId);
                Create.createParty(partyId, json);
            }
        }

        public static class Players{
            public static void updateSinglePlayer(UUID uuid, JsonObject json){
                Clear.Players.clearSinglePlayer(uuid);
                Create.createPlayer(uuid, json);
            }
            public static class Progress{
                public static void updateSingleQuestingPlayer(UUID uuid, int questId, JsonObject json){
                    Clear.Players.Progress.clearSingleQuestProgress(uuid, questId);
                    Create.createPlayerQuestProgress(uuid, questId, json);
                }
            }
        }
    }

    public static class Create{
        public static void createChapter(int chapterId, JsonObject json){
            Chapter c = new Chapter(chapterId);
            c.processJson(json);
            QuestingStorage.getSidedChaptersMap().put(chapterId, c);
        }

        public static void createQuest(int questId, JsonObject json){
            Quest q = new Quest(questId);
            q.processJson(json);
            QuestingStorage.getSidedQuestsMap().put(questId, q);
        }

        public static void createTask(int questId, int taskId, JsonObject json){
            if(QuestHelper.doesQuestExist(questId)){
                Task t = new Task(questId, taskId, Ref.INVALID_RESOURCELOCATION);//gets updated by next line
                t.processJson(json);
                QuestingStorage.getSidedQuestsMap().get(questId).getTasks().put(taskId, t);
            }
        }

        public static void createSubtask(int questId, int taskId, int subtaskId, JsonObject json){
            if(QuestHelper.doesTaskExist(questId, taskId)){
                SubTask s = new SubTask(questId, taskId, subtaskId, QuestHelper.getTaskFromId(questId, taskId).getTaskType());//gets updated by next line
                s.processJson(json);
                QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().put(subtaskId, s);
            }
        }

        public static void createReward(int questId, int rewardId, JsonObject json){
            if(QuestHelper.doesQuestExist(questId)){
                Reward r = new Reward(questId, rewardId);
                r.processJson(json);
                QuestingStorage.getSidedQuestsMap().get(questId).getRewards().put(rewardId, r);
            }
        }

        public static void createSubreward(int questId, int rewardId, int subrewardId, JsonObject json){
            if(QuestHelper.doesRewardExist(questId, rewardId)){
                SubReward s = new SubReward(questId, rewardId, subrewardId);
                s.processJson(json);
                QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId).getSubRewards().put(subrewardId, s);
            }
        }

        public static void createParty(int partyId, JsonObject json){
            Party p = new Party(partyId);
            p.processJson(json);
            QuestingStorage.getSidedPartiesMap().put(partyId, p);
        }

        public static void createPartyQuestProgress(int partyId, int questId, JsonObject json){
            if(PartyHelper.doesPartyExist(partyId)){
                SingleQuestPartyProgress p = new SingleQuestPartyProgress(partyId, questId);
                p.processJson(json);
                QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().put(questId, p);
            }
        }

        public static void createPlayer(UUID uuid, JsonObject json){
            QuestingPlayer p = new QuestingPlayer(uuid);
            p.processJson(json);
            QuestingStorage.getSidedPlayersMap().put(uuid.toString(), p);
        }

        public static void createPlayerQuestProgress(UUID uuid, int questId, JsonObject json){
            if(ProgressHelper.doesPlayerExist(uuid)){
                SingleQuestUserProgress p = new SingleQuestUserProgress(uuid, questId);
                p.processJson(json);
                QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().put(questId, p);
            }
        }
    }

    public static class Clear{ //Just removing it from the database temporarily to immediately repopulate it with the synced quest
        public static class Chapters{
            public static void clearAllChapters(){
                QuestingStorage.getSidedChaptersMap().clear();
            }

            public static void clearSingleChapter(int chapterId){
                QuestingStorage.getSidedChaptersMap().remove(chapterId);
            }
        }

        public static class Quests{
            public static void clearAllQuests(){
                QuestingStorage.getSidedQuestsMap().clear();
            }

            public static void clearSingleQuest(int questId){
                QuestingStorage.getSidedQuestsMap().remove(questId);
            }

            public static class Tasks{
                public static void clearSingleTask(int questId, int taskId){
                    if(QuestHelper.doesQuestExist(questId)){
                        QuestingStorage.getSidedQuestsMap().get(questId).getTasks().remove(taskId);
                    }
                }

                public static class Subtasks{
                    public static void clearSingleSubtask(int questId, int taskId, int subtaskId){
                        if(QuestHelper.doesTaskExist(questId, taskId)){
                            QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().remove(subtaskId);
                        }
                    }
                }
            }

            public static class Rewards{
                public static void clearSingleReward(int questId, int rewardId){
                    if(QuestHelper.doesQuestExist(questId)){
                        QuestingStorage.getSidedQuestsMap().get(questId).getRewards().remove(rewardId);
                    }
                }

                public static class Subrewards{
                    public static void clearSingleSubreward(int questId, int rewardId, int subrewardId){
                        if(QuestHelper.doesTaskExist(questId, rewardId)){
                            QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId).getSubRewards().remove(subrewardId);
                        }
                    }
                }
            }
        }

        public static class Parties{
            public static void clearAllParties(){
                QuestingStorage.getSidedPartiesMap().clear();
            }

            public static void clearSingleParty(int partyId){
                QuestingStorage.getSidedPartiesMap().remove(partyId);
            }
            public static class Progress{
                public static void clearSingleQuestProgress(int questId){
                    QuestingStorage.getSidedPartiesMap().values().forEach(party -> party.getCollectiveProgress().remove(questId));
                }

                public static class Tasks{
                    public static void clearSingleTaskProgress(int questId, int taskId){
                        QuestingStorage.getSidedPartiesMap().values().forEach(party -> party.getCollectiveProgress().get(questId).remove(taskId));
                    }

                    public static class Subtasks{
                        public static void clearSingleSubtaskProgress(int questId, int taskId, int subtaskId){
                            QuestingStorage.getSidedPartiesMap().values().forEach(party -> party.getCollectiveProgress().get(questId).get(taskId).remove(subtaskId));
                        }
                    }
                }
            }
        }

        public static class Players{
            public static void clearAllPlayers(){
                QuestingStorage.getSidedPlayersMap().clear();
            }

            public static void clearSinglePlayer(UUID uuid){
                QuestingStorage.getSidedPlayersMap().remove(uuid.toString());
            }

            public static class Progress{
                public static void clearSingleQuestProgress(int questId){
                    QuestingStorage.getSidedPlayersMap().values().forEach(questingPlayer -> {
                        questingPlayer.getIndividualProgress().remove(questId);
                    });
                }

                public static void clearSingleQuestProgress(UUID player, int questId){
                    if(ProgressHelper.doesPlayerExist(player)){
                        QuestingStorage.getSidedPlayersMap().get(player.toString()).getIndividualProgress().remove(questId);
                    }
                }

                public static class Tasks{
                    public static void clearSingleTaskProgress(int questId, int taskId){
                        QuestingStorage.getSidedPlayersMap().values().forEach(questingPlayer -> questingPlayer.getIndividualProgress().get(questId).remove(taskId));
                    }

                    public static void clearSingleTaskProgress(UUID uuid, int questId, int taskId){
                        if(ProgressHelper.doesPlayerExist(uuid)){
                            QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().get(questId).remove(taskId);
                        }
                    }

                    public static class Subtasks{
                        public static void clearSingleSubtaskProgress(int questId, int taskId, int subtaskId){
                            QuestingStorage.getSidedPlayersMap().values().forEach(questingPlayer -> questingPlayer.getIndividualProgress().get(questId).get(taskId).remove(subtaskId));
                        }

                        public static void clearSingleSubtaskProgress(UUID uuid, int questId, int taskId, int subtaskId){
                            if(ProgressHelper.doesPlayerExist(uuid)){
                                QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().get(questId).get(taskId).remove(subtaskId);
                            }
                        }
                    }
                }
            }
        }
    }

    public static class Delete{ //Deleting it, including all references
        public static void deleteAllChapters(){
            Clear.Chapters.clearAllChapters();
        }

        public static void deleteSingleChapter(int chapterId){
            Clear.Chapters.clearSingleChapter(chapterId);
        }

        public static void deleteAllQuests(){
            QuestingStorage.getSidedQuestsMap().keySet().forEach(Delete::deleteSingleQuest);
        }

        public static void deleteSingleQuest(int questId){
            Clear.Quests.clearSingleQuest(questId);
            Clear.Parties.Progress.clearSingleQuestProgress(questId);//Remove quest related party progress
            Clear.Players.Progress.clearSingleQuestProgress(questId);//Remove quest related player progress
            QuestingStorage.getSidedChaptersMap().values().forEach(chapter -> chapter.getQuests().remove(questId));//Delete the quest from all chapters
            QuestingStorage.getSidedQuestsMap().values().forEach(quest -> quest.getDependencyList().remove(questId));//Delete the quest from all quest dependencies
        }

        public static void deleteSingleParty(int partyId){
            Clear.Parties.clearSingleParty(partyId);
            QuestingStorage.getSidedPlayersMap().values().stream().filter(player -> player.getParty() == partyId).forEach(player -> player.setParty(Ref.NO_PARTY));//Sets the players party to none if they are in the party to delete
        }

        public static void deleteSinglePlayer(UUID uuid){
            Clear.Players.clearSinglePlayer(uuid);
        }

        public static void deleteSingleTask(int questId, int taskId){
            Clear.Quests.Tasks.clearSingleTask(questId, taskId);
            Clear.Parties.Progress.Tasks.clearSingleTaskProgress(questId, taskId);
            Clear.Players.Progress.Tasks.clearSingleTaskProgress(questId, taskId);
        }

        public static void deleteSingleSubtask(int questId, int taskId, int subtaskId){
            Clear.Quests.Tasks.Subtasks.clearSingleSubtask(questId, taskId, subtaskId);
            Clear.Parties.Progress.Tasks.Subtasks.clearSingleSubtaskProgress(questId, taskId, subtaskId);
            Clear.Players.Progress.Tasks.Subtasks.clearSingleSubtaskProgress(questId, taskId, subtaskId);
        }

        public static void deleteSingleReward(int questId, int rewardId){
            Clear.Quests.Rewards.clearSingleReward(questId, rewardId);
        }

        public static void deleteSingleSubreward(int questId, int rewardId, int subrewardId){
            Clear.Quests.Rewards.Subrewards.clearSingleSubreward(questId, rewardId, subrewardId);
        }
    }
}