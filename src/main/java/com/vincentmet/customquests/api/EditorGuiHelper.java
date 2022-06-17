package com.vincentmet.customquests.api;

import net.minecraft.resources.ResourceLocation;

//Everything in this class should only be called upon by the client via the editing gui
public class EditorGuiHelper{
    public static class Delete{
        public static void deleteChapter(int chapterId){
            ClientUtils.EditorMessages.Delete.requestDeleteChapter(chapterId);
        }

        public static void deleteQuest(int questId){
            ClientUtils.EditorMessages.Delete.requestDeleteQuest(questId);
        }

        public static void deleteTask(int questId, int taskId){
            ClientUtils.EditorMessages.Delete.requestDeleteTask(questId, taskId);
        }

        public static void deleteSubtask(int questId, int taskId, int subtaskId){
            ClientUtils.EditorMessages.Delete.requestDeleteSubtask(questId, taskId, subtaskId);
        }

        public static void deleteReward(int questId, int rewardId){
            ClientUtils.EditorMessages.Delete.requestDeleteReward(questId, rewardId);
        }

        public static void deleteSubreward(int questId, int rewardId, int subrewardId){
            ClientUtils.EditorMessages.Delete.requestDeleteSubreward(questId, rewardId, subrewardId);
        }
    }

    public static class Create{
        //todo
    }

    public static class Update{
        public static class Chapter{
            public static void updateChapterIcon(int chapterId, ResourceLocation newValue){
                ClientUtils.EditorMessages.Update.Chapter.requestUpdateChapterIcon(chapterId, newValue);
            }

            public static void updateChapterTitleType(int chapterId, ResourceLocation newValue){
                ClientUtils.EditorMessages.Update.Chapter.Title.requestUpdateChapterTitleType(chapterId, newValue);
            }

            public static void updateChapterTitleText(int chapterId, String newValue){
                ClientUtils.EditorMessages.Update.Chapter.Title.requestUpdateChapterTitleText(chapterId, newValue);
            }

            public static void updateChapterTextType(int chapterId, ResourceLocation newValue){
                ClientUtils.EditorMessages.Update.Chapter.Text.requestUpdateChapterTextType(chapterId, newValue);
            }

            public static void updateChapterTextText(int chapterId, String newValue){
                ClientUtils.EditorMessages.Update.Chapter.Text.requestUpdateChapterTextText(chapterId, newValue);
            }

            public static void addQuestIdToQuestList(int chapterId, int questIdToAdd){
                ClientUtils.EditorMessages.Update.Chapter.requestAddQuestIdToChapterQuestList(chapterId, questIdToAdd);
            }

            public static void removeQuestIdFromQuestList(int chapterId, int questIdToRemove){
                ClientUtils.EditorMessages.Update.Chapter.requestRemoveQuestIdFromChapterQuestList(chapterId, questIdToRemove);
            }
        }

        public static class Quest{
            //todo
        }
    }
}