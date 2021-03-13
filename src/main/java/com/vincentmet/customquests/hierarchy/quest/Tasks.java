package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.IntCounter;
import java.util.*;
import net.minecraft.util.ResourceLocation;

public class Tasks extends HashMap<Integer, Task> implements IJsonObjectProvider, IJsonObjectProcessor{
	private LogicType logicType = LogicType.AND;
	private final int questId;
	
	public Tasks(int parentQuestId){
		this.questId = parentQuestId;
	}
	
	public Task put(Integer id, Task task){
		if(id>=0){
			super.put(id, task);
		}
		return task;
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
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > logic': Value is not a valid operator, please use 'AND' or 'OR', defaulting to 'AND'!");
						setLogicType(LogicType.AND);
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > logic': Value is not a String, defaulting to 'AND'!");
					setLogicType(LogicType.AND);
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > logic': Value is not a JsonPrimitive, please use a String, defaulting to 'AND'!");
				setLogicType(LogicType.AND);
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > logic': Not detected, defaulting to 'AND'!");
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
						if(!jsonObjectValue.has("type")){
							Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + counter.getValue() + " > type': Not detected, defaulting to 'customquests:item_detect'!");
							jsonObjectValue.addProperty("type", new ResourceLocation(Ref.MODID, "item_detect").toString());
						}else{
							JsonElement jsonElementType = jsonObjectValue.get("type");
							if(!jsonElementType.isJsonPrimitive()){
								Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + counter.getValue() + " > type': Value is not a JsonPrimitive, please use a String, defaulting to 'customquests:item_detect'!");
								jsonObjectValue.remove("type");
								jsonObjectValue.addProperty("type", new ResourceLocation(Ref.MODID, "item_detect").toString());
							}else{
								JsonPrimitive jsonPrimitiveType = jsonElementType.getAsJsonPrimitive();
								if(!jsonPrimitiveType.isString()){
									Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + counter.getValue() + " > type': Value is not a String, defaulting to 'customquests:item_detect'!");
									jsonObjectValue.remove("type");
									jsonObjectValue.addProperty("type", new ResourceLocation(Ref.MODID, "item_detect").toString());
								}else{
									String jsonStringType = jsonPrimitiveType.getAsString();
									if(CQRegistry.getTaskTypes().keySet().stream().noneMatch(tasktypeId -> tasktypeId.toString().equals(jsonStringType))){
										Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + counter.getValue() + " > type': Value does not match a registered TaskType, please download the addon mod it belongs to, or change it to something valid. Defaulting to 'customquests:item_detect'!");
										jsonObjectValue.remove("type");
										jsonObjectValue.addProperty("type", new ResourceLocation(Ref.MODID, "item_detect").toString());
									}
								}
							}
						}
						if(jsonObjectValue.has("type")){
							JsonElement jsonObjectValueElement = jsonObjectValue.get("type");
							if(jsonObjectValueElement.isJsonPrimitive()){
								JsonPrimitive jsonObjectValuePrimitive = jsonObjectValueElement.getAsJsonPrimitive();
								if(jsonObjectValuePrimitive.isString()){
									String jsonStringType = jsonObjectValuePrimitive.getAsString();
									if(CQRegistry.getTaskTypes().keySet().stream().anyMatch(tasktypeId -> tasktypeId.toString().equals(jsonStringType))){
										Task task = new Task(questId, keyInt, new ResourceLocation(jsonStringType));
										task.processJson(jsonObjectValue);
										put(Integer.parseInt(key), task);
									}else{
										Ref.CustomQuests.LOGGER.fatal("'Quest > " + questId + " > tasks > entries > " + counter.getValue() + " > type': Value does not match a registered TaskType, please download the addon mod it belongs to, or change it to something valid, discarding it for now! THIS ERROR SHOULDN'T HAPPEN!");
									}
								}else{
									Ref.CustomQuests.LOGGER.fatal("'Quest > " + questId + " > tasks > entries > " + counter.getValue() + " > type': Value is not a String, discarding it for now! THIS ERROR SHOULDN'T HAPPEN!");
								}
							}else{
								Ref.CustomQuests.LOGGER.fatal("'Quest > " + questId + " > tasks > entries > " + counter.getValue() + " > type': Value is not a JsonPrimitive, please use a String, discarding it for now! THIS ERROR SHOULDN'T HAPPEN!");
							}
						}else{
							Ref.CustomQuests.LOGGER.fatal("'Quest > " + questId + " > tasks > entries > " + counter.getValue() + " > type': Not detected, discarding it for now! THIS ERROR SHOULDN'T HAPPEN!");
						}
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
		for(Map.Entry<Integer, Task> entry : entrySet()){
			jsonEntries.add(entry.getKey().toString(), entry.getValue().getJson());
		}
		json.add("entries", jsonEntries);
		return json;
	}
	
	public Tasks setLogicType(LogicType logicType){
		this.logicType = logicType;
		return this;
	}
	
	public LogicType getLogicType(){
		return logicType;
	}
	
	public int getQuestId(){
		return questId;
	}
}