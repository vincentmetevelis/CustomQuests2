package com.vincentmet.customquests.api;

import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.gui.editor.IEditorEntry;
import com.vincentmet.customquests.hierarchy.quest.Quest;
import com.vincentmet.customquests.hierarchy.quest.SubTask;
import com.vincentmet.customquests.hierarchy.quest.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
}