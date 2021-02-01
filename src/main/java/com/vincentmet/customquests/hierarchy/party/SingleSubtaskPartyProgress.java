package com.vincentmet.customquests.hierarchy.party;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;

public class SingleSubtaskPartyProgress implements IJsonObjectProcessor, IJsonObjectProvider{
	private boolean completed;
	private int value;
	private final int partyId;
	private final int questId;
	private final int taskId;
	private final int subtaskId;
	
	public SingleSubtaskPartyProgress(int partyId, int questId, int taskId, int subtaskId){
		this.partyId = partyId;
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
					if(Ref.DEV_MODE)Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > task_progress > " + taskId + " > subtask_progress > " + subtaskId + " > subtask_completed': Value is not a Boolean, defaulting to 'false'!");
					setCompleted(false);
				}
			}else{
				if(Ref.DEV_MODE)Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > task_progress > " + taskId + " > subtask_progress > " + subtaskId + " > subtask_completed': Value is not a JsonPrimitive, please use a boolean, defaulting to 'false'!");
				setCompleted(false);
			}
		}else{
			if(Ref.DEV_MODE)Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > task_progress > " + taskId + " > subtask_progress > " + subtaskId + " > subtask_completed': Not detected, defaulting to 'false'!");
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
					if(Ref.DEV_MODE)Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > task_progress > " + taskId + " > subtask_progress > " + subtaskId + " > value': Value is not a Number, please use an Integer which is 0 or higher, defaulting to '0'!");
					setValue(0);
				}
			}else{
				if(Ref.DEV_MODE)Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > task_progress > " + taskId + " > subtask_progress > " + subtaskId + " > value': Value is not a Primitive, defaulting to '0'!");
				setValue(0);
			}
		}else{
			if(Ref.DEV_MODE)Ref.CustomQuests.LOGGER.warn("'Party > " + partyId + " > collective_progress > " + questId + " > task_progress > " + taskId + " > subtask_progress > " + subtaskId + " > value': Not detected, defaulting to '0'!");
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
	
	public int getPartyId(){
		return partyId;
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
		return "SingleSubtaskProgress{" + "completed=" + completed + ", value=" + value + ", partyId=" + partyId + ", questId=" + questId + ", taskId=" + taskId + ", subtaskId=" + subtaskId + '}';
	}
}
