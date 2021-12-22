package com.vincentmet.customquests.hierarchy.party;

import com.google.gson.*;
import com.vincentmet.customquests.*;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.*;
import java.util.*;

import java.util.Map.Entry;

public class SingleQuestPartyProgress extends HashMap<Integer, SingleTaskPartyProgress> implements IJsonObjectProvider, IJsonObjectProcessor{
	private boolean claimed = false;
	private boolean allTasksCompleted = false;
	private final int partyId;
	private final int questId;
	
	public SingleQuestPartyProgress(int partyId, int questId){
		this.partyId = partyId;
		this.questId = questId;
	}
	
	@Override
	public void processJson(JsonObject json){
		if(json.has("claimed")){
			JsonElement jsonElement = json.get("claimed");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isBoolean()){
					boolean jsonPrimitiveBooleanValue = jsonPrimitive.getAsBoolean();
					setClaimed(jsonPrimitiveBooleanValue);
				}else{
					if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > claimed': Value is not a Boolean, defaulting to 'false'!");
					setClaimed(false);
				}
			}else{
				if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > claimed': Value is not a JsonPrimitive, please use a boolean, defaulting to 'false'!");
				setClaimed(false);
			}
		}else{
			if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > claimed': Not detected, defaulting to 'false'!");
			setClaimed(false);
		}
		
		if(json.has("all_tasks_completed")){
			JsonElement jsonElement = json.get("all_tasks_completed");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isBoolean()){
					boolean jsonPrimitiveBooleanValue = jsonPrimitive.getAsBoolean();
					setAllTasksCompleted(jsonPrimitiveBooleanValue);
				}else{
					if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > all_tasks_completed': Value is not a Boolean, defaulting to 'false'!");
					setAllTasksCompleted(false);
				}
			}else{
				if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > all_tasks_completed': Value is not a JsonPrimitive, please use a boolean, defaulting to 'false'!");
				setAllTasksCompleted(false);
			}
		}else{
			if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > all_tasks_completed': Not detected, defaulting to 'false'!");
			setAllTasksCompleted(false);
		}
		
		if(json.has("task_progress")){
			JsonElement jsonElement = json.get("task_progress");
			if(jsonElement.isJsonObject()){
				IntCounter counter = new IntCounter();
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				for(Entry<String, JsonElement> jsonEntryElement : jsonObject.entrySet()){
					String key = jsonEntryElement.getKey();
					int keyInt = Integer.parseInt(key);
					JsonElement value = jsonEntryElement.getValue();
					if(value.isJsonObject()){
						JsonObject jsonObjectValue = value.getAsJsonObject();
						SingleTaskPartyProgress singleTaskProgress = new SingleTaskPartyProgress(partyId, questId, keyInt);
						singleTaskProgress.processJson(jsonObjectValue);
						put(keyInt, singleTaskProgress);
					}else{
						if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > task_progress > " + counter.getValue() + "': Value is not a JsonObject, discarding it for now!");
					}
					counter.count();
				}
			}else{
				if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > task_progress': Value is not a JsonObject, generating a new one!");
			}
		}else{
			if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > task_progress': Not detected, generating a new JsonObject!");
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("claimed", claimed);
		json.addProperty("all_tasks_completed", allTasksCompleted);
		JsonObject jsonEntries = new JsonObject();
		for(Entry<Integer, SingleTaskPartyProgress> entry : entrySet()) jsonEntries.add(entry.getKey().toString(), entry.getValue().getJson());
		json.add("task_progress", jsonEntries);
		return json;
	}
	
	public void deleteExcessValues(){
		List<Integer> listOfQuestTaskIds = new ArrayList<>(QuestingStorage.getSidedQuestsMap().get(questId).getTasks().keySet());
		List<Integer> listOfProgressTaskIds = new ArrayList<>(keySet());
		listOfProgressTaskIds.removeAll(listOfQuestTaskIds);//remainder of the 'listOfProgressSubtaskIds' contains the progress objects that don't exit anymore in the quests map and therefore should be removed from the player progress
		
		entrySet().removeIf(entry -> listOfProgressTaskIds.contains(entry.getKey()));
		forEach((taskID, taskProgress) -> taskProgress.deleteExcessValues());
	}
	
	public void generateMissingValues(){
		QuestingStorage.getSidedQuestsMap().get(questId).getTasks().forEach((taskId, task) -> {
			if(!containsKey(taskId)){
				put(taskId, new SingleTaskPartyProgress(partyId, questId, taskId));
			}
		});
		forEach((taskID, singleTaskPartyProgress) -> singleTaskPartyProgress.generateMissingValues());
	}
	
	@Override
	public SingleTaskPartyProgress put(Integer key, SingleTaskPartyProgress value){
		if(key >= 0)return super.put(key, value);
		return null;
	}
	
	public void setAllTasksCompleted(boolean allTasksCompleted){
		this.allTasksCompleted = allTasksCompleted;
		forEach((taskId, singleTaskProgress) -> singleTaskProgress.setAllSubtasksCompleted(allTasksCompleted));
	}
	
	public void setClaimed(boolean claimed){
		this.claimed = claimed;
	}
	
	public boolean isClaimed(){
		return claimed;
	}
	
	public boolean areAllTasksCompleted(){
		if(!allTasksCompleted){
			LogicType type = QuestingStorage.getSidedQuestsMap().get(questId).getTasks().getLogicType();
			BooleanContainer completed = new BooleanContainer();
			switch(type){
				case OR:
					forEach((taskId, singleTaskProgress) -> {
						if(!completed.get()){
							completed.set(singleTaskProgress.areAllSubtasksCompleted());
						}
					});
					break;
				case AND:
					List<Boolean> booleanList = new ArrayList<>();
					forEach((taskId, singleTaskProgress) ->{
						booleanList.add(singleTaskProgress.areAllSubtasksCompleted());
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
	
	public int getPartyId(){
		return partyId;
	}
	
	public int getQuestId(){
		return questId;
	}
}
