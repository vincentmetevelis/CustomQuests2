package com.vincentmet.customquests.api;

import com.google.gson.JsonObject;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.helpers.BooleanContainer;
import com.vincentmet.customquests.hierarchy.party.Party;
import com.vincentmet.customquests.hierarchy.quest.Quest;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.entity.player.*;

public class PartyHelper {
	private static final List<Integer> partiesToDelete = new ArrayList<>();
	
	public static int getNextAvailablePartyId(){
		return QuestingStorage.getSidedPartiesMap().keySet().stream().reduce(Math::max).map(partyId->partyId+1).orElse(0);
	}
	
	public static boolean doesPartyExist(int partyId){
		return QuestingStorage.getSidedPartiesMap().get(partyId) != null;
	}
	
	public static UUID getPartyOwner(int partyId){
		if(doesPartyExist(partyId)){
			return QuestingStorage.getSidedPartiesMap().get(partyId).getOwner();
		}
		return null;//this will never happen since its checked at data processing
	}
	
	public static void setPartyOwner(UUID player, int partyId){
		if(doesPartyExist(partyId) && ProgressHelper.doesPlayerExist(player)){
			QuestingStorage.getSidedPartiesMap().get(partyId).setOwner(player);
		}
	}
	
	public static boolean isPlayerPartyOwner(UUID player, int partyId){
		return player.equals(getPartyOwner(partyId));
	}
	
	public static int createParty(UUID creatorUUID, String name){
		if(!ProgressHelper.isPlayerInParty(creatorUUID)){
			int newPartyId = getNextAvailablePartyId();
			Party party = new Party(newPartyId);
			JsonObject json = new JsonObject();
			json.addProperty("owner", creatorUUID.toString());
			json.addProperty("name", name);
			party.processJson(json);
			if(!executePartyDeletionQueue()){
				QuestingStorage.getSidedPartiesMap().put(party.getId(), party);
				party.getCollectiveProgress().deleteExcessValues();
				party.getCollectiveProgress().generateMissingValues();
				ProgressHelper.setPlayerParty(creatorUUID, newPartyId);
				return party.getId();
			}
		}
		return Ref.NO_PARTY;
	}
	
	public static void addPartyToDeletionQueue(int partyId){ //Only use this when deleting a party during data processing
		partiesToDelete.add(partyId);
	}
	
	private static boolean executePartyDeletionQueue(){
		BooleanContainer anyPartiesDeleted = new BooleanContainer(false);
		partiesToDelete.forEach(party -> {
			PartyHelper.deleteParty(party);
			anyPartiesDeleted.set(true);
		});
		partiesToDelete.clear();
		return anyPartiesDeleted.get();
	}
	
	public static void deleteParty(int partyId){ // Only use this when deleting a party during runtime
		QuestingStorage.getSidedPartiesMap().remove(partyId);
		QuestingStorage.getSidedPlayersMap().entrySet().stream().filter(uuidPlayerEntry->uuidPlayerEntry.getValue().getParty()==partyId).forEach(uuidPlayerEntry -> uuidPlayerEntry.getValue().setParty(-1));
	}
	
	public static List<UUID> getAllUUIDsInParty(int partyId){
		return QuestingStorage.getSidedPlayersMap().entrySet().stream().filter(playerEntry->playerEntry.getValue().getParty() == partyId && partyId >= 0).map(Map.Entry::getKey).map(UUID::fromString).collect(Collectors.toList());
	}
	
	//NEW STUFF
	public static void completeQuest(int partyId, int questId){
		if(doesPartyExist(partyId) && QuestHelper.doesQuestExist(questId)){
			QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).setAllTasksCompleted(true);
			QuestingStorage.getSidedPartiesMap().get(partyId).getCollectivelyCompletedQuestList().add(questId);
		}
	}
	
	public static void completeTask(int partyId, int questId, int taskId){
		if(doesPartyExist(partyId) && QuestHelper.doesTaskExist(questId, taskId)){
			QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).get(taskId).setAllSubtasksCompleted(true);
		}
	}
	
	public static void completeSubtask(int partyId, int questId, int taskId, int subtaskId){
		if(doesPartyExist(partyId) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
			QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).get(taskId).get(subtaskId).setCompleted(true);
		}
	}
	
	public static boolean isQuestCompleted(int partyId, int questId){
		if(PartyHelper.doesPartyExist(partyId) && QuestHelper.doesQuestExist(questId)){
			return QuestingStorage.getSidedPartiesMap().get(partyId).getCollectivelyCompletedQuestList().contains(questId);
		}
		return false;
	}
	
	public static boolean isTaskCompleted(int partyId, int questId, int taskId){
		if(PartyHelper.doesPartyExist(partyId) && QuestHelper.doesTaskExist(questId, taskId)){
			return QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).get(taskId).areAllSubtasksCompleted();
		}
		return false;
	}
	
	public static boolean isSubtaskCompleted(int partyId, int questId, int taskId, int subtaskId){
		if(doesPartyExist(partyId) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
			return QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).get(taskId).get(subtaskId).isCompleted();
		}
		return false;
	}
	
	public static boolean isQuestUnlocked(int partyId, int questId){
		if(doesPartyExist(partyId) && QuestHelper.doesQuestExist(questId)){
			Quest quest = QuestHelper.getQuestFromId(questId);
			Set<Integer> partyCompletedQuests = QuestingStorage.getSidedPartiesMap().get(partyId).getCollectivelyCompletedQuestList();
			List<Boolean> boolsToEval = new ArrayList<>();
			quest.getDependencyList().forEach(dependencyQuestId -> boolsToEval.add(partyCompletedQuests.contains(dependencyQuestId)));
			return CQHelper.evalBool(quest.getDependencyList().getLogicType(), boolsToEval, true);
		}
		return false;
	}
	
	public static boolean isQuestClaimed(int partyId, int questId){ // Only use this when only one player per party may claim the reward (config option), instead use CombinedProgressHelper#canClaimReward()
		if(doesPartyExist(partyId) && QuestHelper.doesQuestExist(questId)){
			return QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).isClaimed();
		}
		return false;
	}
	
	public static int getValue(int partyId, int questId, int taskId, int subtaskId){
		if(doesPartyExist(partyId) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
			return QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).get(taskId).get(subtaskId).getValue();
		}
		return 0;
	}
	
	public static void setValue(int partyId, int questId, int taskId, int subtaskId, int value){
		if(doesPartyExist(partyId) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
			QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).get(taskId).get(subtaskId).setValue(value);
		}
	}
	
	public static void addValue(int partyId, int questId, int taskId, int subtaskId, int value){
		if(doesPartyExist(partyId) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
			QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).get(taskId).get(subtaskId).addValue(value);
		}
	}
	
	public static void forEachPlayerInPartyCurrentlyOnline(int partyId, Consumer<ServerPlayerEntity> consumer){
		getAllUUIDsInParty(partyId).stream().map(Ref.currentServerInstance.getPlayerList()::getPlayerByUUID).filter(Objects::nonNull).forEach(consumer);
	}
	
	public static void syncAllPartyDataWithinParty(){
		QuestingStorage.getSidedPartiesMap().keySet().forEach(PartyHelper::syncDataBetweenPartyMembers);
	}
	
	public static void syncDataBetweenPartyMembers(int partyId){
		BooleanContainer shouldSync = new BooleanContainer();
		if(doesPartyExist(partyId)){
			List<UUID> allPartyUuids = PartyHelper.getAllUUIDsInParty(partyId);
			allPartyUuids.forEach(uuid -> {
				//Sync all players in the party their progress to the party
				int sizeCompletedPartyQuestIds = QuestingStorage.getSidedPartiesMap().get(partyId).getCollectivelyCompletedQuestList().size();
				int sizeCompletedPlayerQuestIds = QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().getIndividuallyCompletedQuests().size();
				if(sizeCompletedPartyQuestIds != sizeCompletedPlayerQuestIds){
					shouldSync.set(true);
				}
				QuestingStorage.getSidedPartiesMap().get(partyId).getCollectivelyCompletedQuestList().addAll(QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().getIndividuallyCompletedQuests());
				QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().forEach((questId, singleQuestPartyProgress) -> {
					boolean areAllPlayerTasksCompleted = QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().get(questId).areAllTasksCompleted();
					if(!singleQuestPartyProgress.areAllTasksCompleted() && areAllPlayerTasksCompleted){
						singleQuestPartyProgress.setAllTasksCompleted(true);
						shouldSync.set(true);
					}
					singleQuestPartyProgress.forEach((taskId, singleTaskPartyProgress) -> {
						boolean areAllPlayerSubtasksCompleted = QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().get(questId).get(taskId).areAllSubtasksCompleted();
						if(!singleTaskPartyProgress.areAllSubtasksCompleted() && areAllPlayerSubtasksCompleted){
							singleTaskPartyProgress.setAllSubtasksCompleted(true);
							shouldSync.set(true);
						}
						singleTaskPartyProgress.forEach((subtaskId, singleSubtaskPartyProgress) -> {
							boolean isSubtaskCompleted = QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().get(questId).get(taskId).get(subtaskId).isCompleted();
							int subtaskValue = QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().get(questId).get(taskId).get(subtaskId).getValue();
							if(!singleSubtaskPartyProgress.isCompleted() && isSubtaskCompleted){
								singleSubtaskPartyProgress.setCompleted(true);
								shouldSync.set(true);
							}
							if(subtaskValue > singleSubtaskPartyProgress.getValue()){
								singleSubtaskPartyProgress.setValue(subtaskValue);
								shouldSync.set(true);
							}
						});
					});
				});
			});
			allPartyUuids.forEach(uuid -> {
				//Sync the party's progress to all the party members
				QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().getIndividuallyCompletedQuests().addAll(QuestingStorage.getSidedPartiesMap().get(partyId).getCollectivelyCompletedQuestList());
				int sizeCompletedPartyQuestIds = QuestingStorage.getSidedPartiesMap().get(partyId).getCollectivelyCompletedQuestList().size();
				int sizeCompletedPlayerQuestIds = QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().getIndividuallyCompletedQuests().size();
				if(sizeCompletedPartyQuestIds != sizeCompletedPlayerQuestIds){
					shouldSync.set(true);
				}
				QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().forEach((questId, singleQuestUserProgress) -> {
					boolean areAllPartyTasksCompleted = QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).areAllTasksCompleted();
					if(!singleQuestUserProgress.areAllTasksCompleted() && areAllPartyTasksCompleted){
						singleQuestUserProgress.setAllTasksCompleted(true);
						shouldSync.set(true);
					}
					singleQuestUserProgress.forEach((taskId, singleTaskProgress) -> {
						boolean areAllPartySubtasksCompleted = QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).get(taskId).areAllSubtasksCompleted();
						if(!singleTaskProgress.areAllSubtasksCompleted() && areAllPartySubtasksCompleted){
							singleTaskProgress.setAllSubtasksCompleted(true);
							shouldSync.set(true);
						}
						singleTaskProgress.forEach((subtaskId, singleSubtaskProgress) -> {
							boolean isPartySubtaskCompleted = QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).get(taskId).get(subtaskId).isCompleted();
							int subtaskPartyValue = QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).get(taskId).get(subtaskId).getValue();
							if(!singleSubtaskProgress.isCompleted() && isPartySubtaskCompleted){
								singleSubtaskProgress.setCompleted(true);
								shouldSync.set(true);
							}
							if(subtaskPartyValue > singleSubtaskProgress.getValue()){
								singleSubtaskProgress.setValue(subtaskPartyValue);
								shouldSync.set(true);
							}
						});
					});
				});
			});
		}
		if(shouldSync.get()) PartyHelper.forEachPlayerInPartyCurrentlyOnline(partyId, ServerUtils::sendProgressAndParties);
	}
	
	public static void deleteProgress(int partyId){
		if(doesPartyExist(partyId)){
			Party party = QuestingStorage.getSidedPartiesMap().get(partyId);
			party.getCollectiveProgress().clear();
			party.getCollectivelyCompletedQuestList().clear();
			CQHelper.generateMissingPartyProgress(partyId);
		}
	}
}