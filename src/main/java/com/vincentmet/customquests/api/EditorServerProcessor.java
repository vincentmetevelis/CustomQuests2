package com.vincentmet.customquests.api;

import com.google.gson.JsonObject;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.hierarchy.chapter.Chapter;
import com.vincentmet.customquests.hierarchy.quest.*;
import net.minecraft.resources.ResourceLocation;

public class EditorServerProcessor {
    public static class Create{
        public static void createChapter(){
            int newChapterId = ChapterHelper.getNextAvailableChapterId();
            Chapter c = new Chapter(newChapterId);
            c.processJson(new JsonObject());
            QuestingStorage.getSidedChaptersMap().put(newChapterId, c);
            ServerUtils.Packets.SyncToClient.Data.Chapters.syncChapter(newChapterId);
        }

        public static void createQuest(){
            int newQuestId = QuestHelper.getNextAvailableQuestId();
            Quest q = new Quest(newQuestId);
            q.processJson(new JsonObject());
            QuestingStorage.getSidedQuestsMap().put(newQuestId, q);
            ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(newQuestId);
        }

        public static void createTask(int questId){
            if(QuestHelper.doesQuestExist(questId)){
                int newTaskId = QuestHelper.getNextAvailableTaskId(questId);
                Task t = new Task(questId, newTaskId, new ResourceLocation(Ref.MODID, "item_detect"));//todo check out if it's possible to have a Task constructor without a type parameter!
                QuestingStorage.getSidedQuestsMap().get(questId).getTasks().put(newTaskId, t);
                ServerUtils.Packets.SyncToClient.Data.Quests.syncTask(questId, newTaskId);
            }
        }

        public static void createSubtask(int questId, int taskId){
            if(QuestHelper.doesTaskExist(questId, taskId)){
                int newSubtaskId = QuestHelper.getNextAvailableSubtaskId(questId, taskId);
                SubTask st = new SubTask(questId, taskId, newSubtaskId, new ResourceLocation(Ref.MODID, "item_detect"));//todo check out if it's possible to have a SubTask constructor without a type parameter!
                QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().put(newSubtaskId, st);
                ServerUtils.Packets.SyncToClient.Data.Quests.syncSubtask(questId, taskId, newSubtaskId);
            }
        }

        public static void createReward(int questId){
            if(QuestHelper.doesQuestExist(questId)){
                int newRewardId = QuestHelper.getNextAvailableRewardId(questId);
                Reward r = new Reward(questId, newRewardId);
                QuestingStorage.getSidedQuestsMap().get(questId).getRewards().put(newRewardId, r);
                ServerUtils.Packets.SyncToClient.Data.Quests.syncReward(questId, newRewardId);
            }
        }

        public static void createSubreward(int questId, int rewardId){
            if(QuestHelper.doesRewardExist(questId, rewardId)){
                int newSubrewardId = QuestHelper.getNextAvailableSubrewardId(questId, rewardId);
                SubReward sr = new SubReward(questId, rewardId, newSubrewardId);
                QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId).getSubRewards().put(newSubrewardId, sr);
                ServerUtils.Packets.SyncToClient.Data.Quests.syncSubreward(questId, rewardId, newSubrewardId);
            }
        }
    }

    public static class Delete{
        public static void deleteChapter(int chapterId){
            //todo
        }

        public static void deleteQuest(int questId){
            //todo
        }

        public static void deleteTask(int questId, int taskId){
            //todo
        }

        public static void deleteSubtask(int questId, int taskId, int subtaskId){
            //todo
        }

        public static void deleteReward(int questId, int rewardId){
            //todo
        }

        public static void deleteSubreward(int questId, int rewardId, int subrewardId){
            //todo
        }
    }

    public static class Update{
        public static class Chapter{
            public static void updateIcon(int chapterId, ResourceLocation newIcon){
                //todo
            }

            public static class Title{
                public static void updateType(int chapterId, ResourceLocation newType){
                    //todo
                }

                public static void updateText(int chapterId, String newText){
                    //todo
                }
            }

            public static class Text{
                public static void updateType(int chapterId, ResourceLocation newType){
                    //todo
                }

                public static void updateText(int chapterId, String newText){
                    //todo
                }
            }

            public static class QuestList{
                public static void addQuestId(int chapterId, int questIdToAdd){
                    //todo
                }

                public static void removeQuestId(int chapterId, int questIdToRemove){
                    //todo
                }
            }
        }

        public static class Quest{
            private static class Button{
                public static void updateShape(int questId, ResourceLocation newShape){
                    //todo
                }

                public static void updateIcon(int questId, ResourceLocation newIcon){
                    //todo
                }

                public static void updateScale(int questId, double newScale){
                    //todo
                }
            }

            public static class Title{
                public static void updateType(int chapterId, ResourceLocation newType){
                    //todo
                }

                public static void updateText(int chapterId, String newText){
                    //todo
                }
            }

            public static class Text{
                public static void updateType(int chapterId, ResourceLocation newType){
                    //todo
                }

                public static void updateText(int chapterId, String newText){
                    //todo
                }
            }

            public static class QuestList{
                public static void addQuestId(int chapterId, int questIdToAdd){
                    //todo
                }

                public static void removeQuestId(int chapterId, int questIdToRemove){
                    //todo
                }
            }
        }
    }
}