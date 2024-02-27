package com.vincentmet.customquests.api;

import com.google.gson.JsonObject;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.gui.editor.IEditorEntry;
import com.vincentmet.customquests.hierarchy.quest.*;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class QuestHelper{
	public static Quest getQuestFromId(int questId){
		if(doesQuestExist(questId)){
			return QuestingStorage.getSidedQuestsMap().get(questId);
		}
		return null;
	}
	
	public static Task getTaskFromId(int questId, int taskId){
		if(doesTaskExist(questId, taskId)){
			return QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId);
		}
		return null;
	}
	
	public static SubTask getSubtaskFromId(int questId, int taskId, int subtaskId){
		if(doesSubtaskExist(questId, taskId, subtaskId)){
			return QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().get(subtaskId);
		}
		return null;
	}

	public static Reward getRewardFromId(int questId, int rewardId){
		if(doesRewardExist(questId, rewardId)){
			return QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId);
		}
		return null;
	}

	public static SubReward getSubrewardFromId(int questId, int rewardId, int subrewardId){
		if(doesSubrewardExist(questId, rewardId, subrewardId)){
			return QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId).getSubRewards().get(subrewardId);
		}
		return null;
	}
	
	public static boolean doesQuestExist(int questId){
		return QuestingStorage.getSidedQuestsMap().get(questId) != null;
	}
	
	public static boolean doesTaskExist(int questId, int taskId){
		if(doesQuestExist(questId)){
			return QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId) != null;
		}
		return false;
	}
	
	public static boolean doesRewardExist(int questId, int rewardId){
		if(doesQuestExist(questId)){
			return QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId) != null;
		}
		return false;
	}
	
	public static boolean doesSubtaskExist(int questId, int taskId, int subtaskId){
		if(doesTaskExist(questId, taskId)){
			return QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().get(subtaskId) != null;
		}
		return false;
	}
	
	public static boolean doesSubrewardExist(int questId, int rewardId, int subrewardId){
		if(doesRewardExist(questId, subrewardId)){
			return QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId).getSubRewards().get(subrewardId) != null;
		}
		return false;
	}
	
	public static boolean doesQuestHaveRewards(int questId){
		if(doesQuestExist(questId)){
			return QuestingStorage.getSidedQuestsMap().get(questId).getRewards().size() >= 1;
		}
		return false;
	}
	
	public static boolean doesRewardHaveSubrewards(int questId, int rewardId){
		if(doesRewardExist(questId, rewardId)){
			return QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId).getSubRewards().size() >= 1;
		}
		return false;
	}
	
	public static Optional<Integer> getNextExistingRewardId(int questId){
		if(doesQuestHaveRewards(questId)){
			return QuestingStorage.getSidedQuestsMap().get(questId).getRewards().keySet().stream().findFirst();
		}
		return Optional.empty();
	}
	
	public static Optional<Integer> getNextExistingSubrewardId(int questId, int rewardId){
		if(doesRewardHaveSubrewards(questId, rewardId)){
			return QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId).getSubRewards().keySet().stream().findFirst();
		}
		return Optional.empty();
	}
	
	public static void executeAllSubrewards(UUID claimer, int questId, int rewardId){
		QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId).getSubRewards().executeAllSubrewards(Ref.currentServerInstance.getPlayerList().getPlayer(claimer));
	}

	public static List<IEditorEntry> getEditorQuestEntries(int questId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesQuestExist(questId)){
			QuestingStorage.getSidedQuestsMap().get(questId).addPageEntries(entries);
		}
		return entries;
	}

	public static List<IEditorEntry> getEditorQuestButtonEntries(int questId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesQuestExist(questId)){
			QuestingStorage.getSidedQuestsMap().get(questId).getButton().addPageEntries(entries);
		}
		return entries;
	}

	public static List<IEditorEntry> getEditorQuestTitleEntries(int questId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesQuestExist(questId)){
			QuestingStorage.getSidedQuestsMap().get(questId).getTitle().addPageEntries(entries);
		}
		return entries;
	}

	public static List<IEditorEntry> getEditorQuestSubtitleEntries(int questId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesQuestExist(questId)){
			QuestingStorage.getSidedQuestsMap().get(questId).getSubtitle().addPageEntries(entries);
		}
		return entries;
	}

	public static List<IEditorEntry> getEditorQuestTextEntries(int questId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesQuestExist(questId)){
			QuestingStorage.getSidedQuestsMap().get(questId).getText().addPageEntries(entries);
		}
		return entries;
	}

	public static List<IEditorEntry> getEditorQuestDependenciesEntries(int questId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesQuestExist(questId)){
			QuestingStorage.getSidedQuestsMap().get(questId).getDependencyList().addPageEntries(entries);
		}
		return entries;
	}

	public static List<IEditorEntry> getEditorQuestTasksEntries(int questId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesQuestExist(questId)){
			QuestingStorage.getSidedQuestsMap().get(questId).getTasks().addPageEntries(entries);
		}
		return entries;
	}

	public static List<IEditorEntry> getEditorQuestTaskEntries(int questId, int taskId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesTaskExist(questId, taskId)){
			QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).addPageEntries(entries);
		}
		return entries;
	}

	public static List<IEditorEntry> getEditorQuestSubtasksEntries(int questId, int taskId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesTaskExist(questId, taskId)){
			QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().addPageEntries(entries);
		}
		return entries;
	}

	public static List<IEditorEntry> getEditorQuestSubtaskEntries(int questId, int taskId, int subtaskId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesSubtaskExist(questId, taskId, subtaskId)){
			QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().get(subtaskId).addPageEntries(entries);
		}
		return entries;
	}

	//todo reward entries here

	public static List<IEditorEntry> getEditorQuestPositionEntries(int questId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesQuestExist(questId)){
			QuestingStorage.getSidedQuestsMap().get(questId).getPosition().addPageEntries(entries);
		}
		return entries;
	}

	//The next few functions are used by both the server and the client to properly delete a quest and its dependencies from the DB
	public static void deleteQuest(int questId){
		deleteQuestReferencesFromChapters(questId);
		deleteQuestPlayerProgress(questId);
		deleteQuestPartyProgress(questId);
		deleteQuestFromDatabase(questId);
	}

	private static void deleteQuestPlayerProgress(int questId){
		QuestingStorage.getSidedPlayersMap().values().forEach(questingPlayer -> questingPlayer.getIndividualProgress().getIndividuallyCompletedQuests().remove(questId));
		QuestingStorage.getSidedPlayersMap().values().forEach(questingPlayer -> questingPlayer.getIndividualProgress().remove(questId));
	}

	private static void deleteQuestPartyProgress(int questId){
		QuestingStorage.getSidedPartiesMap().values().forEach(party -> party.getCollectivelyCompletedQuestList().remove(questId));
		QuestingStorage.getSidedPartiesMap().values().forEach(party -> party.getCollectiveProgress().remove(questId));
	}

	private static void deleteQuestReferencesFromChapters(int questId){
		QuestingStorage.getSidedChaptersMap().values().forEach(chapter -> chapter.getQuests().remove(questId));
	}

	private static void deleteQuestFromDatabase(int questId){
		QuestingStorage.getSidedQuestsMap().remove(questId);
	}

	public static int getNextAvailableQuestId(){
		if(!QuestingStorage.getSidedQuestsMap().isEmpty()){
			return Collections.max(QuestingStorage.getSidedQuestsMap().keySet())+1;
		}
		return 0;
	}

	public static int getNextAvailableTaskId(int questId){
		if(!QuestingStorage.getSidedQuestsMap().isEmpty()){
			if(doesQuestExist(questId)){
				if(!QuestingStorage.getSidedQuestsMap().get(questId).getTasks().isEmpty()){
					return Collections.max(QuestingStorage.getSidedQuestsMap().get(questId).getTasks().keySet())+1;
				}
			}
		}
		return 0;
	}

	public static int getNextAvailableSubtaskId(int questId, int taskId){
		if(!QuestingStorage.getSidedQuestsMap().isEmpty()){
			if(doesQuestExist(questId)){
				if(!QuestingStorage.getSidedQuestsMap().get(questId).getTasks().isEmpty()){
					if(doesTaskExist(questId, taskId)){
						if(!QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().isEmpty()){
							return Collections.max(QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().keySet())+1;
						}
					}
				}
			}
		}
		return 0;
	}

	public static int getNextAvailableRewardId(int questId){
		if(!QuestingStorage.getSidedQuestsMap().isEmpty()){
			if(doesQuestExist(questId)){
				if(!QuestingStorage.getSidedQuestsMap().get(questId).getRewards().isEmpty()){
					return Collections.max(QuestingStorage.getSidedQuestsMap().get(questId).getRewards().keySet())+1;
				}
			}
		}
		return 0;
	}

	public static int getNextAvailableSubrewardId(int questId, int rewardId){
		if(!QuestingStorage.getSidedQuestsMap().isEmpty()){
			if(doesQuestExist(questId)){
				if(!QuestingStorage.getSidedQuestsMap().get(questId).getRewards().isEmpty()){
					if(doesRewardExist(questId, rewardId)){
						if(!QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId).getSubRewards().isEmpty()){
							return Collections.max(QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId).getSubRewards().keySet())+1;
						}
					}
				}
			}
		}
		return 0;
	}
}