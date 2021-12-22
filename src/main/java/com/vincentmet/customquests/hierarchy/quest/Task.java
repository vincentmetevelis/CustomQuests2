package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import net.minecraft.resources.ResourceLocation;

public class Task implements IJsonObjectProcessor, IJsonObjectProvider{
	private final int questId;
	private final int taskId;
	private ResourceLocation taskType;
	private final SubTasks subtasks;
	
	public Task(int questId, int taskId, ResourceLocation taskType){
		this.questId = questId;
		this.taskId = taskId;
		this.taskType = taskType;
		this.subtasks = new SubTasks(questId, taskId, taskType);
	}
	
	@Override
	public void processJson(JsonObject json){
		if(json.has("type")){
			JsonElement jsonElement = json.get("type");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonString = jsonPrimitive.getAsString();
					if(CQRegistry.getTaskTypes().keySet().stream().anyMatch(tasktypeId -> tasktypeId.toString().equals(jsonString))){
						setTaskType(new ResourceLocation(jsonString));
					}else{
						Ref.CustomQuests.LOGGER.fatal("'Quest > " + questId + " > tasks > entries > " + taskId + " > type': Value does not match a registered TaskType, please download the addon mod it belongs to, or change it to something valid, discarding it for now! THIS ERROR SHOULDN'T HAPPEN!");
					}
				}else{
					Ref.CustomQuests.LOGGER.fatal("'Quest > " + questId + " > tasks > entries > " + taskId + " > type': Value is not a String, discarding it for now! THIS ERROR SHOULDN'T HAPPEN!");
				}
			}else{
				Ref.CustomQuests.LOGGER.fatal("'Quest > " + questId + " > tasks > entries > " + taskId + " > type': Value is not a JsonPrimitive, please use a String, discarding it for now! THIS ERROR SHOULDN'T HAPPEN!");
			}
		}else{
			Ref.CustomQuests.LOGGER.fatal("'Quest > " + questId + " > tasks > entries > " + taskId + " > type': Not detected, discarding it for now! THIS ERROR SHOULDN'T HAPPEN!");
		}
		
		if(json.has("sub_tasks")){
			JsonElement jsonElement = json.get("sub_tasks");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				subtasks.processJson(jsonObject);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks': Value is not a JsonObject, generating a new one!");
				subtasks.processJson(new JsonObject());
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks': Not detected, generating a new JsonObject!");
			subtasks.processJson(new JsonObject());
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("type", taskType.toString());
		json.add("sub_tasks", subtasks.getJson());
		return json;
	}
	
	public ResourceLocation getTaskType(){
		return taskType;
	}
	
	public void setTaskType(ResourceLocation taskType){
		this.taskType = taskType;
		this.subtasks.clear();
	}
	
	public SubTasks getSubtasks(){
		return subtasks;
	}
	
	public int getId(){
		return taskId;
	}
}
