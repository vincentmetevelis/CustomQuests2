package com.vincentmet.customquests.hierarchy.progress;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import java.util.UUID;

public class SingleSubtaskProgress implements IJsonObjectProcessor, IJsonObjectProvider{
	private boolean completed;
	private int value;
	private final UUID uuid;
	private final int questId;
	private final int taskId;
	private final int subtaskId;
	
	public SingleSubtaskProgress(UUID uuid, int questId, int taskId, int subtaskId){
		this.uuid = uuid;
		this.questId = questId;
		this.taskId = taskId;
		this.subtaskId = subtaskId;
	}
	
	@Override
	public void processJson(JsonObject json){
		if(json.has("subtask_completed")){
			JsonElement jsonElement = json.get("subtask_completed");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isBoolean()){
					boolean jsonPrimitiveBooleanValue = jsonPrimitive.getAsBoolean();
					setCompleted(jsonPrimitiveBooleanValue);
				}else{
					Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries > " + questId + " > task_progress > " + taskId + " > subtask_progress > " + subtaskId + " > subtask_completed': Value is not a Boolean, defaulting to 'false'!");
					setCompleted(false);
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries > " + questId + " > task_progress > " + taskId + " > subtask_progress > " + subtaskId + " > subtask_completed': Value is not a JsonPrimitive, please use a boolean, defaulting to 'false'!");
				setCompleted(false);
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries > " + questId + " > task_progress > " + taskId + " > subtask_progress > " + subtaskId + " > subtask_completed': Not detected, defaulting to 'false'!");
			setCompleted(false);
		}
		
		if(json.has("value")){
			JsonElement jsonElement = json.get("value");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					int jsonPrimitiveIntValue = jsonPrimitive.getAsInt();
					setValue(jsonPrimitiveIntValue);
				}else{
					Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries > " + questId + " > task_progress > " + taskId + " > subtask_progress > " + subtaskId + " > value': Value is not a Number, please use an Integer which is 0 or higher, defaulting to '0'!");
					setValue(0);
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries > " + questId + " > task_progress > " + taskId + " > subtask_progress > " + subtaskId + " > value': Value is not a Primitive, defaulting to '0'!");
				setValue(0);
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress > entries > " + questId + " > task_progress > " + taskId + " > subtask_progress > " + subtaskId + " > value': Not detected, defaulting to '0'!");
			setValue(0);
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("subtask_completed", completed);
		json.addProperty("value", value);
		return json;
	}
	
	public void deleteExcessValues(){
		//NO-OP
	}
	
	public void generateMissingValues(){
		//NO-OP
	}
	
	public void setValue(int value){
		this.value = value;
	}
	
	public void addValue(int value){
		this.value += value;
	}
	
	public int getValue(){
		return value;
	}
	
	public void setCompleted(boolean completed){
		this.completed = completed;
	}
	
	public boolean isCompleted(){
		return completed;
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
	
	public int getSubtaskId(){
		return subtaskId;
	}
	
	@Override
	public String toString(){
		return "SingleSubtaskProgress{" + "completed=" + completed + ", value=" + value + ", uuid=" + uuid + ", questId=" + questId + ", taskId=" + taskId + ", subtaskId=" + subtaskId + '}';
	}
}
