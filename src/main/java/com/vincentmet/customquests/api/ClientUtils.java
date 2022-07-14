package com.vincentmet.customquests.api;

import com.vincentmet.customquests.gui.EditorScreen;
import com.vincentmet.customquests.gui.QuestingScreen;
import com.vincentmet.customquests.network.messages.PacketHandler;
import com.vincentmet.customquests.network.messages.editor.cts.requests.create.*;
import com.vincentmet.customquests.network.messages.editor.cts.requests.delete.*;
import com.vincentmet.customquests.network.messages.editor.cts.requests.update.chapter.*;
import com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class ClientUtils{
	public static void openQuestingScreen(){
		Minecraft.getInstance().setScreen(new QuestingScreen());
	}
	
	public static void openEditorScreen(){
		Minecraft.getInstance().setScreen(new EditorScreen());
	}
	
	public static void reloadEditorIfOpen(){
		if(Minecraft.getInstance().screen instanceof EditorScreen screen){
			screen.actionQueue.push(screen::reInit);
		}
	}

	public static void reloadMainGuiIfOpen(){
		if(Minecraft.getInstance().screen instanceof QuestingScreen screen){
			screen.actionQueue.push(screen::reInit);
		}
	}

	public static class EditorMessages{
		public static class Create{
			public static void requestCreateChapter(){
				PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestCreateChapter());
			}

			public static void requestCreateQuest(){
				PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestCreateQuest());
			}

			public static void requestCreateTask(int questId){
				PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestCreateTask(questId));
			}

			public static void requestCreateSubtask(int questId, int taskId){
				PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestCreateSubtask(questId, taskId));
			}

			public static void requestCreateReward(int questId){
				PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestCreateReward(questId));
			}

			public static void requestCreateSubreward(int questId, int rewardId){
				PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestCreateSubreward(questId, rewardId));
			}
		}

		public static class Delete{
			public static void requestDeleteChapter(int chapterId){
				PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestDeleteChapter(chapterId));
			}

			public static void requestDeleteQuest(int questId){
				PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestDeleteQuest(questId));
			}

			public static void requestDeleteTask(int questId, int taskId){
				PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestDeleteTask(questId, taskId));
			}

			public static void requestDeleteSubtask(int questId, int taskId, int subtaskId){
				PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestDeleteSubtask(questId, taskId, subtaskId));
			}

			public static void requestDeleteReward(int questId, int rewardId){
				PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestDeleteReward(questId, rewardId));
			}

			public static void requestDeleteSubreward(int questId, int rewardId, int subrewardId){
				PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestDeleteSubreward(questId, rewardId, subrewardId));
			}
		}

		public static class Update{
			public static class Chapter{
				public static void requestUpdateChapterIcon(int chapterId, ResourceLocation icon){
					PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateChapterIcon(chapterId, icon));
				}

				public static class Title{
					public static void requestUpdateChapterTitleType(int chapterId, ResourceLocation type){
						PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateChapterTitleType(chapterId, type));
					}

					public static void requestUpdateChapterTitleText(int chapterId, String text){
						PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateChapterTitleText(chapterId, text));
					}
				}

				public static class Text{
					public static void requestUpdateChapterTextType(int chapterId, ResourceLocation type){
						PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateChapterTextType(chapterId, type));
					}

					public static void requestUpdateChapterTextText(int chapterId, String text){
						PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateChapterTextText(chapterId, text));
					}
				}

				public static void requestAddQuestIdToChapterQuestList(int chapterId, int questIdToAdd){
					PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestChapterQuestlistAddQuestId(chapterId, questIdToAdd));
				}

				public static void requestRemoveQuestIdFromChapterQuestList(int chapterId, int questIdToRemove){
					PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestChapterQuestlistRemoveQuestId(chapterId, questIdToRemove));
				}
			}

			public static class Quest{
				public static class Button{
					public static void requestUpdateQuestButtonShape(int questId, ResourceLocation shape){
						PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateQuestButtonShape(questId, shape));
					}

					public static void requestUpdateQuestButtonIcon(int questId, ResourceLocation icon){
						PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateQuestButtonIcon(questId, icon));
					}

					public static void requestUpdateQuestButtonScale(int questId, double scale){
						PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateQuestButtonScale(questId, scale));
					}
				}

				public static class Title{
					public static void requestUpdateQuestTitleType(int questId, ResourceLocation type){
						PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateQuestTitleType(questId, type));
					}

					public static void requestUpdateQuestTitleText(int questId, String text){
						PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateQuestTitleText(questId, text));
					}
				}

				public static class Subtitle{
					public static void requestUpdateQuestTitleType(int questId, ResourceLocation type){
						PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateQuestSubtitleType(questId, type));
					}

					public static void requestUpdateQuestTitleText(int questId, String text){
						PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateQuestSubtitleText(questId, text));
					}
				}

				public static class Text{
					public static void requestUpdateQuestTextType(int questId, ResourceLocation type){
						PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateQuestTextType(questId, type));
					}

					public static void requestUpdateQuestTextText(int questId, String text){
						PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateQuestTextText(questId, text));
					}
				}

				public static class Tasks{
					public static void requestUpdateTasksLogic(int questId, LogicType logicType){
						//PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateTasksLogic(questId, logicType));//todo
					}

					public static class Task{
						public static void requestUpdateTaskType(int questId, int taskId, ResourceLocation taskType){
							//PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateTaskType(questId, taskId, taskType));//todo
						}

						public static class Subtasks{
							public static void requestUpdateSubtasksLogic(int questId, int taskId, LogicType logicType){
								//PacketHandler.CHANNEL.sendToServer(new MessageEditorRequestUpdateSubtasksLogic(questId, taskId, logicType));//todo
							}

							public static class Subtask{
								//todo, idk, something referencing the Type class or something
							}
						}
					}
				}

				public static class Rewards{
					//todo
				}

				public static class Position{
					//todo
				}
			}
		}
		
		public static void requestUpdateReward(int questId, int rewardId){
			//todo remove after new methods are done
		}

		public static void requestUpdateSubreward(int questId, int rewardId, int subrewardId){
			//todo remove after new methods are done
		}
	}
}