package com.vincentmet.customquests.api;

import com.google.gson.JsonObject;
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
            public static void requestUpdateIcon(int chapterId, ResourceLocation newValue){
                ClientUtils.EditorMessages.Update.Chapter.requestUpdateChapterIcon(chapterId, newValue);
            }
            public static class Title{
                public static void requestUpdateType(int chapterId, ResourceLocation newValue){
                    ClientUtils.EditorMessages.Update.Chapter.Title.requestUpdateChapterTitleType(chapterId, newValue);
                }
                public static void requestUpdateText(int chapterId, String newValue){
                    ClientUtils.EditorMessages.Update.Chapter.Title.requestUpdateChapterTitleText(chapterId, newValue);
                }
            }

            public static class Text{
                public static void requestUpdateType(int chapterId, ResourceLocation newValue){
                    ClientUtils.EditorMessages.Update.Chapter.Text.requestUpdateChapterTextType(chapterId, newValue);
                }

                public static void requestUpdateText(int chapterId, String newValue){
                    ClientUtils.EditorMessages.Update.Chapter.Text.requestUpdateChapterTextText(chapterId, newValue);
                }
            }

            public static class QuestList{
                public static void requestAddQuestIdToQuestList(int chapterId, int questIdToAdd){
                    ClientUtils.EditorMessages.Update.Chapter.requestAddQuestIdToChapterQuestList(chapterId, questIdToAdd);
                }

                public static void requestRemoveQuestIdFromQuestList(int chapterId, int questIdToRemove){
                    ClientUtils.EditorMessages.Update.Chapter.requestRemoveQuestIdFromChapterQuestList(chapterId, questIdToRemove);
                }
            }
        }

        public static class Quest{
            public static class Button{
                public static void requestUpdateShape(int questId, ResourceLocation newValue){
                    ClientUtils.EditorMessages.Update.Quest.Button.requestUpdateQuestButtonShape(questId, newValue);
                }

                public static void requestUpdateIcon(int questId, ResourceLocation newValue){
                    ClientUtils.EditorMessages.Update.Quest.Button.requestUpdateQuestButtonIcon(questId, newValue);
                }

                public static void requestUpdateScale(int questId, double newValue){
                    ClientUtils.EditorMessages.Update.Quest.Button.requestUpdateQuestButtonScale(questId, newValue);
                }
            }

            public static class Title{
                public static void requestUpdateType(int questId, ResourceLocation newValue){
                    ClientUtils.EditorMessages.Update.Quest.Title.requestUpdateQuestTitleType(questId, newValue);
                }
                public static void requestUpdateText(int questId, String newValue){
                    ClientUtils.EditorMessages.Update.Quest.Title.requestUpdateQuestTitleText(questId, newValue);
                }
            }

            public static class Subtitle{
                public static void requestUpdateType(int questId, ResourceLocation newValue){
                    ClientUtils.EditorMessages.Update.Quest.Subtitle.requestUpdateQuestSubtitleType(questId, newValue);
                }

                public static void requestUpdateText(int questId, String newValue){
                    ClientUtils.EditorMessages.Update.Quest.Subtitle.requestUpdateQuestSubtitleText(questId, newValue);
                }
            }

            public static class Text{
                public static void requestUpdateType(int questId, ResourceLocation newValue){
                    ClientUtils.EditorMessages.Update.Quest.Text.requestUpdateQuestTextType(questId, newValue);
                }

                public static void requestUpdateText(int questId, String newValue){
                    ClientUtils.EditorMessages.Update.Quest.Text.requestUpdateQuestTextText(questId, newValue);
                }
            }

            public static class Dependencies{
                public static void requestUpdateLogic(int questId, LogicType newValue){
                    ClientUtils.EditorMessages.Update.Quest.Dependencies.requestUpdateDependenciesLogic(questId, newValue);
                }

                public static class Entries{
                    public static void requestUpdateAddDependencyQuestId(int questId, int newValue){
                        ClientUtils.EditorMessages.Update.Quest.Dependencies.requestUpdateAddDependencyQuestId(questId, newValue);
                    }

                    public static void requestUpdateRemoveDependencyQuestId(int questId, int newValue){
                        ClientUtils.EditorMessages.Update.Quest.Dependencies.requestUpdateRemoveDependencyQuestId(questId, newValue);
                    }
                }
            }

            public static class Tasks{
                public static void requestUpdateLogic(int questId, LogicType newValue){
                    ClientUtils.EditorMessages.Update.Quest.Tasks.requestUpdateTasksLogic(questId, newValue);
                }

                public static class Task{
                    public static void requestUpdateTaskType(int questId, int taskId, ResourceLocation newValue){
                        ClientUtils.EditorMessages.Update.Quest.Tasks.Task.requestUpdateTaskType(questId, taskId, newValue);
                    }

                    public static class Subtasks{
                        public static void requestUpdateSubtasksLogic(int questId, int taskId, LogicType newValue){
                            ClientUtils.EditorMessages.Update.Quest.Tasks.Task.Subtasks.requestUpdateSubtasksLogic(questId, taskId, newValue);
                        }

                        public static class Subtask{
                            public static void requestUpdateSubtaskContent(int questId, int taskId, int subtaskId, JsonObject newValue){
                                ClientUtils.EditorMessages.Update.Quest.Tasks.Task.Subtasks.Subtask.requestUpdateSubtaskCustomContent(questId, taskId, subtaskId, newValue);
                            }
                        }
                    }
                }
            }

            public static class Rewards{
                public static void requestUpdateLogic(int questId, LogicType newValue){
                    ClientUtils.EditorMessages.Update.Quest.Tasks.requestUpdateTasksLogic(questId, newValue);
                }

                public static class Reward{
                    public static class Subrewards{
                        public static class Subreward{
                            public static void requestUpdateSubrewardType(int questId, int rewardId, int subrewardId, ResourceLocation newValue){
                                ClientUtils.EditorMessages.Update.Quest.Rewards.Reward.Subrewards.Subreward.requestUpdateSubrewardType(questId, rewardId, subrewardId, newValue);
                            }

                            public static void requestUpdateSubrewardContent(int questId, int rewardId, int subrewardId, JsonObject newValue){
                                ClientUtils.EditorMessages.Update.Quest.Rewards.Reward.Subrewards.Subreward.requestUpdateSubrewardCustomContent(questId, rewardId, subrewardId, newValue);
                            }
                        }
                    }
                }
            }

            public static class Position{
                public static void requestUpdatePositionX(int questId, int newValue){
                    ClientUtils.EditorMessages.Update.Quest.Position.requestUpdateX(questId, newValue);
                }

                public static void requestUpdatePositionY(int questId, int newValue){
                    ClientUtils.EditorMessages.Update.Quest.Position.requestUpdateY(questId, newValue);
                }
            }
        }
    }
}