package com.vincentmet.customquests.api;

import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.hierarchy.quest.*;
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
}
