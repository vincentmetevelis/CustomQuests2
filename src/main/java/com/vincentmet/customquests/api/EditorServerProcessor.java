package com.vincentmet.customquests.api;

import com.google.gson.JsonObject;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.hierarchy.chapter.Chapter;
import com.vincentmet.customquests.hierarchy.chapter.ChapterTextTextType;
import com.vincentmet.customquests.hierarchy.chapter.ChapterTitleTextType;
import com.vincentmet.customquests.hierarchy.quest.*;
import net.minecraft.resources.ResourceLocation;

public class EditorServerProcessor {
    public static class Create {
        public static void createChapter() {
            int newChapterId = ChapterHelper.getNextAvailableChapterId();
            Chapter c = new Chapter(newChapterId);
            c.processJson(new JsonObject());
            QuestingStorage.getSidedChaptersMap().put(newChapterId, c);
            ServerUtils.Packets.SyncToClient.Data.Chapters.syncChapter(newChapterId);
        }

        public static void createQuest() {
            int newQuestId = QuestHelper.getNextAvailableQuestId();
            Quest q = new Quest(newQuestId);
            q.processJson(new JsonObject());
            QuestingStorage.getSidedQuestsMap().put(newQuestId, q);
            ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(newQuestId);
        }

        public static void createTask(int questId) {
            if (QuestHelper.doesQuestExist(questId)) {
                int newTaskId = QuestHelper.getNextAvailableTaskId(questId);
                Task t = new Task(questId, newTaskId, new ResourceLocation(Ref.MODID, "item_detect"));//todo check out if it's possible to have a Task constructor without a type parameter!
                QuestingStorage.getSidedQuestsMap().get(questId).getTasks().put(newTaskId, t);
                ServerUtils.Packets.SyncToClient.Data.Quests.syncTask(questId, newTaskId);
            }
        }

        public static void createSubtask(int questId, int taskId) {
            if (QuestHelper.doesTaskExist(questId, taskId)) {
                int newSubtaskId = QuestHelper.getNextAvailableSubtaskId(questId, taskId);
                SubTask st = new SubTask(questId, taskId, newSubtaskId, new ResourceLocation(Ref.MODID, "item_detect"));//todo check out if it's possible to have a SubTask constructor without a type parameter!
                QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().put(newSubtaskId, st);
                ServerUtils.Packets.SyncToClient.Data.Quests.syncSubtask(questId, taskId, newSubtaskId);
            }
        }

        public static void createReward(int questId) {
            if (QuestHelper.doesQuestExist(questId)) {
                int newRewardId = QuestHelper.getNextAvailableRewardId(questId);
                Reward r = new Reward(questId, newRewardId);
                QuestingStorage.getSidedQuestsMap().get(questId).getRewards().put(newRewardId, r);
                ServerUtils.Packets.SyncToClient.Data.Quests.syncReward(questId, newRewardId);
            }
        }

        public static void createSubreward(int questId, int rewardId) {
            if (QuestHelper.doesRewardExist(questId, rewardId)) {
                int newSubrewardId = QuestHelper.getNextAvailableSubrewardId(questId, rewardId);
                SubReward sr = new SubReward(questId, rewardId, newSubrewardId);
                QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId).getSubRewards().put(newSubrewardId, sr);
                ServerUtils.Packets.SyncToClient.Data.Quests.syncSubreward(questId, rewardId, newSubrewardId);
            }
        }
    }

    public static class Delete {//delete it, including all references

        public static void deleteChapter(int chapterId) {
            if (ChapterHelper.doesChapterExist(chapterId)) {
                QuestingStorage.getSidedChaptersMap().remove(chapterId);
                ServerUtils.Packets.SyncToClient.Data.Chapters.syncChapter(chapterId);
            }
        }

        public static void deleteQuest(int questId) {
            if (QuestHelper.doesQuestExist(questId)) {
                QuestingStorage.getSidedQuestsMap().remove(questId);//remove quest
                QuestingStorage.getSidedPartiesMap().values().forEach(party -> party.getCollectiveProgress().remove(questId));//Remove quest related party progress
                QuestingStorage.getSidedPlayersMap().values().forEach(questingPlayer -> questingPlayer.getIndividualProgress().remove(questId));//Remove quest related player progress
                QuestingStorage.getSidedChaptersMap().values().forEach(chapter -> chapter.getQuests().remove(questId));//Delete the quest from all chapters
                QuestingStorage.getSidedQuestsMap().values().forEach(quest -> quest.getDependencyList().remove(questId));//Delete the quest from all quest dependencies
                ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);//only have to sync the (deleted) quest, the client processor will take care of all the dependencies
            }
        }

        public static void deleteTask(int questId, int taskId) {
            if (QuestHelper.doesTaskExist(questId, taskId)) {
                QuestHelper.getQuestFromId(questId).getTasks().remove(taskId);//remove task
                QuestingStorage.getSidedPartiesMap().values().forEach(party -> party.getCollectiveProgress().get(questId).remove(taskId));//remove party progress
                QuestingStorage.getSidedPlayersMap().values().forEach(questingPlayer -> questingPlayer.getIndividualProgress().get(questId).remove(taskId));//remove player progress
                ServerUtils.Packets.SyncToClient.Data.Quests.syncTask(questId, taskId);//only have to sync the (deleted) task, the client processor will take care of all the dependencies
            }
        }

        public static void deleteSubtask(int questId, int taskId, int subtaskId) {
            if (QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)) {
                QuestHelper.getTaskFromId(questId, taskId).getSubtasks().remove(subtaskId);//remove subtask
                QuestingStorage.getSidedPartiesMap().values().forEach(party -> party.getCollectiveProgress().get(questId).get(taskId).remove(subtaskId));//remove party progress
                QuestingStorage.getSidedPlayersMap().values().forEach(questingPlayer -> questingPlayer.getIndividualProgress().get(questId).get(taskId).remove(subtaskId));//remove player progress
                ServerUtils.Packets.SyncToClient.Data.Quests.syncSubtask(questId, taskId, subtaskId);//only have to sync the (deleted) subtask, the client processor will take care of all the dependencies
            }
        }

        public static void deleteReward(int questId, int rewardId) {
            if (QuestHelper.doesRewardExist(questId, rewardId)) {
                QuestHelper.getQuestFromId(questId).getRewards().remove(rewardId);//remove reward
                ServerUtils.Packets.SyncToClient.Data.Quests.syncReward(questId, rewardId);
            }
        }

        public static void deleteSubreward(int questId, int rewardId, int subrewardId) {
            if (QuestHelper.doesSubrewardExist(questId, rewardId, subrewardId)) {
                QuestHelper.getRewardFromId(questId, rewardId).getSubRewards().remove(subrewardId);//remove subreward
                ServerUtils.Packets.SyncToClient.Data.Quests.syncSubreward(questId, rewardId, subrewardId);
            }
        }
    }

    public static class Update {
        public static class Chapter {
            public static void updateIcon(int chapterId, ResourceLocation newIcon) {
                if (ChapterHelper.doesChapterExist(chapterId)) {
                    ChapterHelper.getChapterFromId(chapterId).setIcon(newIcon);
                    ServerUtils.Packets.SyncToClient.Data.Chapters.syncChapter(chapterId);
                }
            }

            public static class Title {
                public static void updateType(int chapterId, ResourceLocation newType) {
                    if (ChapterHelper.doesChapterExist(chapterId)) {
                        ChapterHelper.getChapterFromId(chapterId).getTitle().setType(newType);
                        ServerUtils.Packets.SyncToClient.Data.Chapters.syncChapter(chapterId);
                    }
                }

                public static void updateText(int chapterId, String newText) {
                    if (ChapterHelper.doesChapterExist(chapterId)) {
                        ChapterHelper.getChapterFromId(chapterId).getTitle().setText(newText);
                        ServerUtils.Packets.SyncToClient.Data.Chapters.syncChapter(chapterId);
                    }
                }
            }

            public static class Text {
                public static void updateType(int chapterId, ResourceLocation newType) {
                    if (ChapterHelper.doesChapterExist(chapterId)) {
                        ChapterHelper.getChapterFromId(chapterId).getText().setType(newType);
                        ServerUtils.Packets.SyncToClient.Data.Chapters.syncChapter(chapterId);
                    }
                }

                public static void updateText(int chapterId, String newText) {
                    if (ChapterHelper.doesChapterExist(chapterId)) {
                        ChapterHelper.getChapterFromId(chapterId).getText().setText(newText);
                        ServerUtils.Packets.SyncToClient.Data.Chapters.syncChapter(chapterId);
                    }
                }
            }

            public static class QuestList {
                public static void addQuestId(int chapterId, int questIdToAdd) {
                    if (ChapterHelper.doesChapterExist(chapterId)) {
                        ChapterHelper.getChapterFromId(chapterId).getQuests().add(questIdToAdd);
                        ServerUtils.Packets.SyncToClient.Data.Chapters.syncChapter(chapterId);
                    }
                }

                public static void removeQuestId(int chapterId, int questIdToRemove) {
                    if (ChapterHelper.doesChapterExist(chapterId)) {
                        ChapterHelper.getChapterFromId(chapterId).getQuests().remove(questIdToRemove);
                        ServerUtils.Packets.SyncToClient.Data.Chapters.syncChapter(chapterId);
                    }
                }
            }
        }

        public static class Quest {
            public static class Button {//fixme these methods override other editor entries, find a less "resetting" way of updating these; probably save button per page instead of per line

                public static void updateShape(int questId, ResourceLocation newShape) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getButton().setShape(newShape);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }

                public static void updateIcon(int questId, ResourceLocation newIcon) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getButton().setIcon(newIcon);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }

                public static void updateScale(int questId, double newScale) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getButton().setScale(newScale);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }
            }

            public static class Title {
                public static void updateType(int questId, ResourceLocation newType) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getTitle().setType(newType);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }

                public static void updateText(int questId, String newText) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getTitle().setText(newText);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }
            }

            public static class Subtitle {
                public static void updateType(int questId, ResourceLocation newType) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getSubtitle().setType(newType);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }

                public static void updateText(int questId, String newText) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getSubtitle().setText(newText);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }
            }

            public static class Text {
                public static void updateType(int questId, ResourceLocation newType) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getText().setType(newType);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }

                public static void updateText(int questId, String newText) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getText().setText(newText);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }
            }

            public static class Dependencies {
                public static void updateLogic(int questId, LogicType newLogicType) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getDependencyList().setLogicType(newLogicType);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }

                public static void addQuestId(int questId, int questIdToAdd) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getDependencyList().add(questIdToAdd);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }

                public static void removeQuestId(int questId, int questIdToRemove) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getDependencyList().remove(questIdToRemove);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }
            }

            public static class Tasks {
                public static void updateLogic(int questId, LogicType newLogicType) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getTasks().setLogicType(newLogicType);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }

                public static class Task {
                    public static void updateTaskType(int questId, int taskId, ResourceLocation newTaskType) {
                        if (QuestHelper.doesTaskExist(questId, taskId)) {
                            QuestHelper.getTaskFromId(questId, taskId).setTaskType(newTaskType);
                            ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                        }
                    }

                    public static class Subtasks {
                        public static void updateLogic(int questId, int taskId, LogicType newLogicType) {
                            if (QuestHelper.doesTaskExist(questId, taskId)) {
                                QuestHelper.getTaskFromId(questId, taskId).getSubtasks().setLogicType(newLogicType);
                                ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                            }
                        }

                        public static class Subtask {
                            public static void updateCustomContent(int questId, int taskId, int subtaskId, JsonObject newContent) {
                                if (QuestHelper.doesTaskExist(questId, taskId)) {
                                    //QuestHelper.getSubtaskFromId(questId, taskId, subtaskId).setSubtaskValues(newContent);//todo
                                    ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                                }
                            }
                        }
                    }
                }
            }

            public static class Rewards {
                public static void updateLogic(int questId, LogicType newLogicType) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getRewards().setLogicType(newLogicType);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }

                public static class Reward {
                    public static class Subrewards {
                        public static class Subreward {
                            public static void updateSubrewardType(int questId, int rewardId, int subrewardId, ResourceLocation newSubrewardType) {
                                if (QuestHelper.doesSubrewardExist(questId, rewardId, subrewardId)) {
                                    QuestHelper.getSubrewardFromId(questId, rewardId, subrewardId).setSubrewardType(newSubrewardType);
                                    ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                                }
                            }

                            public static void updateSubrewardContent(int questId, int rewardId, int subrewardId, JsonObject newContent){
                                if (QuestHelper.doesSubrewardExist(questId, rewardId, subrewardId)) {
                                    //QuestHelper.getSubrewardFromId(questId, rewardId, subrewardId).setSubrewardValues(newContent);//todo
                                    ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                                }
                            }
                        }
                    }
                }
            }

            public static class Position {
                public static void updateX(int questId, int newX) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getPosition().setX(newX);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }

                public static void updateY(int questId, int newY) {
                    if (QuestHelper.doesQuestExist(questId)) {
                        QuestHelper.getQuestFromId(questId).getPosition().setY(newY);
                        ServerUtils.Packets.SyncToClient.Data.Quests.syncQuest(questId);
                    }
                }
            }
        }
    }
}