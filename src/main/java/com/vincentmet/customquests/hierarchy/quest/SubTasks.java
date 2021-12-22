package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.IntCounter;
import java.util.*;
import net.minecraft.resources.ResourceLocation;

public class SubTasks extends HashMap<Integer, SubTask> implements IJsonObjectProvider, IJsonObjectProcessor{
	private final int questId;
	private final int taskId;
	
	private LogicType logicType = LogicType.AND;
	private final ResourceLocation type;
	
	public SubTasks(int questId, int taskId, ResourceLocation type){
		this.questId = questId;
		this.taskId = taskId;
		this.type = type;
	}
	
	public SubTask put(Integer id, SubTask subtask){
		if(id>=0){
			super.put(id, subtask);
		}
		return subtask;
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
					if(operator.toUpperCase().equals("AND") || operator.toUpperCase().equals("OR")){
						setLogicType(LogicType.valueOf(operator.toUpperCase()));
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > logic': Value is not a valid operator, please use 'AND' or 'OR', defaulting to 'AND'!");
						setLogicType(LogicType.AND);
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > logic': Value is not a String, defaulting to 'AND'!");
					setLogicType(LogicType.AND);
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > logic': Value is not a JsonPrimitive, please use a String, defaulting to 'AND'!");
				setLogicType(LogicType.AND);
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > logic': Not detected, defaulting to 'AND'!");
			setLogicType(LogicType.AND);
		}
		
		if(json.has("entries")){
			JsonElement jsonElement = json.get("entries");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				IntCounter counter = new IntCounter();
				for(Map.Entry<String, JsonElement> jsonEntryElement : jsonObject.entrySet()){
					String key = jsonEntryElement.getKey();
					int keyInt = Integer.parseInt(key);
					JsonElement value = jsonEntryElement.getValue();
					if(value.isJsonObject()){
						JsonObject jsonObjectValue = value.getAsJsonObject();
						SubTask subTask = new SubTask(questId, taskId, keyInt, type);
						subTask.processJson(jsonObjectValue);
						put(keyInt, subTask);
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + counter.getValue() + "': Value is not a JsonObject, discarding it for now!");
					}
					counter.count();
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries': Value is not a JsonObject, generating a new one!");
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries': Not detected, generating a new JsonObject!");
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("logic", logicType.toString());
		JsonObject jsonEntries = new JsonObject();
		for(Map.Entry<Integer, SubTask> entry : entrySet()){
			jsonEntries.add(entry.getKey().toString(), entry.getValue().getJson());
		}
		json.add("entries", jsonEntries);
		return json;
	}
	
	public SubTasks setLogicType(LogicType logicType){
		this.logicType = logicType;
		return this;
	}
	
	public LogicType getLogicType(){
		return logicType;
	}
	
	public int getQuestId(){
		return questId;
	}
	
	public int getTaskId(){
		return taskId;
	}
}
