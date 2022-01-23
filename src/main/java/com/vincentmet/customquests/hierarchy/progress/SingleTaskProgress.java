package com.vincentmet.customquests.hierarchy.progress;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.BooleanContainer;
import com.vincentmet.customquests.helpers.IntCounter;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.util.thread.EffectiveSide;

import java.util.*;

public class SingleTaskProgress extends HashMap<Integer, SingleSubtaskProgress> implements IJsonObjectProcessor, IJsonObjectProvider{
	private boolean allSubtasksCompleted = false;
	private final UUID uuid;
	private final int questId;
	private final int taskId;
	
	public SingleTaskProgress(UUID uuid, int questId, int taskId){
		this.uuid = uuid;
		this.questId = questId;
		this.taskId = taskId;
	}
	
	@Override
	public void processJson(JsonObject json){
		if(json.has("all_subtasks_completed")){
			JsonElement jsonElement = json.get("all_subtasks_completed");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isBoolean()){
					boolean jsonPrimitiveBooleanValue = jsonPrimitive.getAsBoolean();
					setAllSubtasksCompleted(jsonPrimitiveBooleanValue);
				}else{
					Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries > " + questId + " > task_progress > " + taskId + " > all_subtasks_completed': Value is not a Boolean, defaulting to 'false'!");
					setAllSubtasksCompleted(false);
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries > " + questId + " > task_progress > " + taskId + " > all_subtasks_completed': Value is not a JsonPrimitive, please use a boolean, defaulting to 'false'!");
				setAllSubtasksCompleted(false);
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries > " + questId + " > task_progress > " + taskId + " > all_subtasks_completed': Not detected, defaulting to 'false'!");
			setAllSubtasksCompleted(false);
		}
		
		if(json.has("subtask_progress")){
			JsonElement jsonElement = json.get("subtask_progress");
			if(jsonElement.isJsonObject()){
				IntCounter counter = new IntCounter();
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				for(Map.Entry<String, JsonElement> jsonEntryElement : jsonObject.entrySet()){
					String key = jsonEntryElement.getKey();
					int keyInt = Integer.parseInt(key);
					JsonElement value = jsonEntryElement.getValue();
					if(value.isJsonObject()){
						JsonObject jsonObjectValue = value.getAsJsonObject();
						SingleSubtaskProgress singleSubtaskProgress = new SingleSubtaskProgress(uuid, questId, taskId, keyInt);
						singleSubtaskProgress.processJson(jsonObjectValue);
						put(keyInt, singleSubtaskProgress);
					}else{
						Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries > " + questId + " > task_progress > " + taskId + " > subtask_progress > " + counter.getValue() + "': Value is not a JsonObject, discarding it for now!");
					}
					counter.count();
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries > " + questId + " > task_progress > " + taskId + " > subtask_progress': Value is not a JsonObject, generating a new one!");
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries > " + questId + " > task_progress > " + taskId + " > subtask_progress': Not detected, generating a new JsonObject!");
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("all_subtasks_completed", allSubtasksCompleted);
		JsonObject jsonEntries = new JsonObject();
		for(Entry<Integer, SingleSubtaskProgress> entry : entrySet()) jsonEntries.add(entry.getKey().toString(), entry.getValue().getJson());
		json.add("subtask_progress", jsonEntries);
		return json;
	}
	
	public void deleteExcessValues(){
		List<Integer> listOfQuestSubtaskIds = new ArrayList<>(QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().keySet());
		List<Integer> listOfProgressSubtaskIds = new ArrayList<>(keySet());
		listOfProgressSubtaskIds.removeAll(listOfQuestSubtaskIds);//remainder of the 'listOfProgressSubtaskIds' contains the progress objects that don't exit anymore in the quests map and therefore should be removed from the player progress
		
		entrySet().removeIf(entry -> listOfProgressSubtaskIds.contains(entry.getKey()));
		forEach((subtaskID, subtaskProgress) -> subtaskProgress.deleteExcessValues());
	}
	
	public void generateMissingValues(){
		QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().forEach((subtaskId, subtask) -> {
			if(!containsKey(subtaskId)){
				put(subtaskId, new SingleSubtaskProgress(uuid, questId, taskId, subtaskId));
			}
		});
		QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().get(questId).get(taskId).forEach((integer, singleSubtaskProgress) -> singleSubtaskProgress.generateMissingValues());
	}
	
	public void setAllSubtasksCompleted(boolean allSubtasksCompleted){
		this.allSubtasksCompleted = allSubtasksCompleted;
		forEach((subTaskId, singleSubtaskProgress) -> singleSubtaskProgress.setCompleted(allSubtasksCompleted));
	}
	
	public boolean areAllSubtasksCompleted(){
		if(!allSubtasksCompleted){
			LogicType type = QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().getLogicType();
			BooleanContainer completed = new BooleanContainer();
			switch(type){
				case OR:
					forEach((taskId, singleSubtaskProgress) -> {
						if(singleSubtaskProgress.isCompleted()){
							completed.set(true);
						}
					});
					break;
				case AND:
					List<Boolean> booleanList = new ArrayList<>();
					forEach((taskId, singleSubtaskProgress) ->{
						booleanList.add(singleSubtaskProgress.isCompleted());
					});
					if(booleanList.size()>=2){
						Optional<Boolean> optionalBoolean = booleanList.stream().reduce((b1, b2) -> b1 && b2);
						completed.set(optionalBoolean.orElse(true));
					}else if(booleanList.size()==1){
						completed.set(booleanList.get(0));
					}
					break;
			}
			return completed.get();
		}
		return true; // would put the variable here, but since it is always true...
	}
	
	public UUID getUuid(){
		return uuid;
	}
	
	public int getQuestId(){
		return questId;
	}
	
	public int getTaskId(){
		return taskId;
	}
	
	public void executeTaskButton(Player player){
		if(EffectiveSide.get().isServer()){
			QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().entrySet().stream().filter(entry->!CombinedProgressHelper.isSubtaskCompleted(player.getUUID(), questId, taskId, entry.getKey())).forEach(entry -> entry.getValue().getSubtask().executeSubtaskButton(player));
		}
	}
}
