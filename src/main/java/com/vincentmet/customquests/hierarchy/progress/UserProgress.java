package com.vincentmet.customquests.hierarchy.progress;

import com.google.gson.*;
import com.vincentmet.customquests.*;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.IntCounter;
import java.util.*;

import java.util.Map.Entry;

public class UserProgress extends HashMap<Integer, SingleQuestUserProgress> implements IJsonObjectProcessor, IJsonObjectProvider {
	private final UUID uuid;
	private final IndividuallyCompletedQuestList individuallyCompletedQuests;
	
	public UserProgress(UUID uuid){
		this.uuid = uuid;
		this.individuallyCompletedQuests = new IndividuallyCompletedQuestList(uuid);
	}
	
	@Override
	public void processJson(JsonObject json){
		if(json.has("quests_individually_completed")){
			JsonElement jsonElement = json.get("quests_individually_completed");
			if(jsonElement.isJsonArray()){
				JsonArray jsonObject = jsonElement.getAsJsonArray();
				individuallyCompletedQuests.processJson(jsonObject);
			}else{
				if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > quests_individually_completed': Value is not a JsonArray, generating a new one!");
				individuallyCompletedQuests.processJson(new JsonArray());
			}
		}else{
			if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > quests_individually_completed': Not detected, generating a new JsonArray!");
			individuallyCompletedQuests.processJson(new JsonArray());
		}
		
		if(json.has("entries")){
			JsonElement jsonElement = json.get("entries");
			if(jsonElement.isJsonObject()){
				IntCounter counter = new IntCounter();
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				for(Map.Entry<String, JsonElement> jsonEntryElement : jsonObject.entrySet()){
					String key = jsonEntryElement.getKey();
					int keyInt = Integer.parseInt(key);
					JsonElement value = jsonEntryElement.getValue();
					if(value.isJsonObject()){
						JsonObject jsonObjectValue = value.getAsJsonObject();
						SingleQuestUserProgress singleQuestUserProgress = new SingleQuestUserProgress(uuid, keyInt);
						singleQuestUserProgress.processJson(jsonObjectValue);
						put(keyInt, singleQuestUserProgress);
					}else{
						if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries > " + counter.getValue() + "': Value is not a JsonObject, discarding it for now!");
					}
					counter.count();
				}
			}else{
				if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries': Value is not a JsonObject, generating a new one!");
			}
		}else{
			if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries': Not detected, generating a new JsonObject!");
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.add("quests_individually_completed", individuallyCompletedQuests.getJson());
		JsonObject jsonEntries = new JsonObject();
		for(Entry<Integer, SingleQuestUserProgress> entry : entrySet()) jsonEntries.add(entry.getKey().toString(), entry.getValue().getJson());
		json.add("entries", jsonEntries);
		return json;
	}
	
	@Override
	public SingleQuestUserProgress put(Integer key, SingleQuestUserProgress value){
		if(key >= 0)return super.put(key, value);
		return null;
	}
	
	public void deleteExcessValues(){
		List<Integer> listOfQuestIds = new ArrayList<>(QuestingStorage.getSidedQuestsMap().keySet());
		List<Integer> listOfProgressQuestIds = new ArrayList<>(keySet());
		listOfProgressQuestIds.removeAll(listOfQuestIds);//remainder of the 'listOfProgressSubtaskIds' contains the progress objects that don't exit anymore in the quests map and therefore should be removed from the player progress
		
		entrySet().removeIf(entry -> listOfProgressQuestIds.contains(entry.getKey()));
		forEach((questID, questProgress) -> questProgress.deleteExcessValues());
	}
	
	public void generateMissingValues(){
		QuestingStorage.getSidedQuestsMap().forEach((key, value)->{
			if(!containsKey(key)){
				put(key, new SingleQuestUserProgress(uuid, key));
			}
		});
		QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().forEach((integer, singleQuestUserProgress) -> singleQuestUserProgress.generateMissingValues());
	}
	
	public UUID getUuid(){
		return uuid;
	}
	
	public IndividuallyCompletedQuestList getIndividuallyCompletedQuests(){
		return individuallyCompletedQuests;
	}
}
