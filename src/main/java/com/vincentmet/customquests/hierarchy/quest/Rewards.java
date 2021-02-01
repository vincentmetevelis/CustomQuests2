package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.IntCounter;
import java.util.*;

public class Rewards extends HashMap<Integer, Reward> implements IJsonObjectProcessor, IJsonObjectProvider{
	private LogicType logicType = LogicType.AND;
	private final int parentQuestId;
	
	public Rewards(int parentQuestId){
		this.parentQuestId = parentQuestId;
	}
	
	public Reward put(Integer id, Reward reward){
		if(keySet().stream().noneMatch(integer -> integer.equals(id))){
			super.put(id, reward);
		}
		return reward;
	}
	
	@Override
	public void processJson(JsonObject json){
		clear();
		
		if(json.has("logic")){
			JsonElement jsonElement = json.get("logic");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String operator = jsonPrimitive.getAsString();
					if(operator.equalsIgnoreCase("AND") || operator.equalsIgnoreCase("OR")){
						setLogicType(LogicType.valueOf(operator.toUpperCase()));
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > logic': Value is not a valid operator, please use 'AND' or 'OR', defaulting to 'AND'!");
						setLogicType(LogicType.AND);
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > logic': Value is not a String, defaulting to 'AND'!");
					setLogicType(LogicType.AND);
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > logic': Value is not a JsonPrimitive, please use a String, defaulting to 'AND'!");
				setLogicType(LogicType.AND);
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > logic': Not detected, defaulting to 'AND'!");
			setLogicType(LogicType.AND);
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
						Reward reward = new Reward(parentQuestId, keyInt);
						reward.processJson(jsonObjectValue);
						put(keyInt, reward);
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > tasks > entries > " + counter.getValue() + "': Value is not a JsonObject, discarding it for now!");
					}
					counter.count();
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries': Value is not a JsonObject, generating a new one!");
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries': Not detected, generating a new JsonObject!");
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("logic", logicType.toString());
		JsonObject jsonEntries = new JsonObject();
		for(Map.Entry<Integer, Reward> entry : entrySet()){
			jsonEntries.add(entry.getKey().toString(), entry.getValue().getJson());
		}
		json.add("entries", jsonEntries);
		return json;
	}
	
	public Rewards setLogicType(LogicType logicType){
		this.logicType = logicType;
		return this;
	}
	
	public LogicType getLogicType(){
		return logicType;
	}
}
