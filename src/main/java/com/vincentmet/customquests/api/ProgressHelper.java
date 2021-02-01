package com.vincentmet.customquests.api;

import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.hierarchy.progress.*;
import com.vincentmet.customquests.hierarchy.quest.Quest;
import java.util.*;
import net.minecraft.entity.player.*;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import static com.vincentmet.customquests.Ref.CustomQuests.LOGGER;

public class ProgressHelper{
	public static int getPlayerParty(UUID uuid){
		if(doesPlayerExist(uuid)){
			return QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getParty();
		}
		return Ref.NO_PARTY;
	}
	
	public static void setPlayerParty(UUID uuid, int partyId){
		if(doesPlayerExist(uuid)){
			QuestingStorage.getSidedPlayersMap().get(uuid.toString()).setParty(partyId);
		}
	}
	
	public static boolean isPlayerInParty(UUID uuid){
		if(uuid != null && QuestingStorage.getSidedPlayersMap().containsKey(uuid.toString())){
			int partyId = QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getParty();
			return partyId >= 0 && PartyHelper.doesPartyExist(partyId);
		}
		return false;
	}
	
	public static boolean doesPlayerExist(UUID uuid){
		if(uuid != null){
			return QuestingStorage.getSidedPlayersMap().get(uuid.toString()) != null;
		}
		return false;
	}
	
	public static void executeTaskCallback(PlayerEntity player, int questId, int taskId){
		if(EffectiveSide.get().isServer()){
			QuestingStorage.getSidedPlayersMap().get(player.getUniqueID().toString()).getIndividualProgress().get(questId).get(taskId).executeTaskButton(player);
			ServerUtils.sendProgressAndParties((ServerPlayerEntity)player);
		}else{
			LOGGER.warn("Tried executing task callback on client, this method should only be called on the server!");
		}
	}
	
	//NEW STUFF
	public static void completeQuest(UUID player, int questId){
		if(doesPlayerExist(player) && QuestHelper.doesQuestExist(questId)){
			UserProgress userProgress = QuestingStorage.getSidedPlayersMap().get(player.toString()).getIndividualProgress();
			if(!userProgress.getIndividuallyCompletedQuests().contains(questId)){
				userProgress.getIndividuallyCompletedQuests().add(questId);
			}
			userProgress.get(questId).setAllTasksCompleted(true);
		}
	}
	
	public static void completeTask(UUID player, int questId, int taskId){
		if(doesPlayerExist(player) && QuestHelper.doesTaskExist(questId, taskId)){
			QuestingStorage.getSidedPlayersMap().get(player.toString()).getIndividualProgress().get(questId).get(taskId).setAllSubtasksCompleted(true);
		}
	}
	
	public static void completeSubtask(UUID player, int questId, int taskId, int subtaskId){
		if(doesPlayerExist(player) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
			QuestingStorage.getSidedPlayersMap().get(player.toString()).getIndividualProgress().get(questId).get(taskId).get(subtaskId).setCompleted(true);
		}
	}
	
	public static boolean isQuestCompleted(UUID uuid, int questId){
		if(doesPlayerExist(uuid) && QuestHelper.doesQuestExist(questId)){
			return QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().getIndividuallyCompletedQuests().contains(questId);
		}
		return false;
	}
	
	public static boolean isTaskCompleted(UUID uuid, int questId, int taskId){
		if(doesPlayerExist(uuid) && QuestHelper.doesTaskExist(questId, taskId)){
			return QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().get(questId).get(taskId).areAllSubtasksCompleted();
		}
		return false;
	}
	
	public static boolean isSubtaskCompleted(UUID uuid, int questId, int taskId, int subtaskId){
		if(doesPlayerExist(uuid) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
			return QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().get(questId).get(taskId).get(subtaskId).isCompleted();
		}
		return false;
	}
	
	public static boolean isQuestUnlocked(UUID uuid, int questId){
		if(doesPlayerExist(uuid) && QuestHelper.doesQuestExist(questId)){
			Quest quest = QuestHelper.getQuestFromId(questId);
			List<Integer> playerCompletedQuests = QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().getIndividuallyCompletedQuests();
			List<Boolean> boolsToEval = new ArrayList<>();
			quest.getDependencyList().forEach(depId->boolsToEval.add(playerCompletedQuests.contains(depId)));
			return CQHelper.evalBool(quest.getDependencyList().getLogicType(), boolsToEval, true);
		}
		return false;
	}
	
	public static int getValue(UUID player, int questId, int taskId, int subtaskId){
		if(doesPlayerExist(player) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
			return QuestingStorage.getSidedPlayersMap().get(player.toString()).getIndividualProgress().get(questId).get(taskId).get(subtaskId).getValue();
		}
		return 0;
	}
	
	public static boolean isQuestClaimed(UUID player, int questId){ // Only use this when only one player per party may claim the reward (config option), instead use CombinedProgressHelper#canClaimReward()
		if(doesPlayerExist(player) && QuestHelper.doesQuestExist(questId)){
			return QuestingStorage.getSidedPlayersMap().get(player.toString()).getIndividualProgress().get(questId).isClaimed();
		}
		return false;
	}
	
	public static void setValue(UUID player, int questId, int taskId, int subtaskId, int value){
		if(doesPlayerExist(player) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
			QuestingStorage.getSidedPlayersMap().get(player.toString()).getIndividualProgress().get(questId).get(taskId).get(subtaskId).setValue(value);
		}
	}
	
	public static void addValue(UUID player, int questId, int taskId, int subtaskId, int value){
		if(doesPlayerExist(player) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
			QuestingStorage.getSidedPlayersMap().get(player.toString()).getIndividualProgress().get(questId).get(taskId).get(subtaskId).addValue(value);
		}
	}
	
	public static void deleteProgress(UUID uuid){
		QuestingPlayer questingPlayer = QuestingStorage.getSidedPlayersMap().get(uuid.toString());
		questingPlayer.getIndividualProgress().clear();
		questingPlayer.getIndividualProgress().getIndividuallyCompletedQuests().clear();
		CQHelper.generateMissingProgress(uuid);
	}
}