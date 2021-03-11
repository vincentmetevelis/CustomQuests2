package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.JsonObject;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.JsonContainer;
import net.minecraft.util.ResourceLocation;

public class SubTask implements IJsonObjectProvider, IJsonObjectProcessor{
	private final ResourceLocation type;
	private final int questId;
	private final int taskId;
	private final int subtaskId;
	private ITaskType subTask;
	
	public SubTask(int questId, int taskId, int subtaskId, ResourceLocation type){
		this.type = type;
		this.questId = questId;
		this.taskId = taskId;
		this.subtaskId = subtaskId;
	}
	
	@Override
	public void processJson(JsonObject json){
		json.addProperty("quest_id", questId);
		json.addProperty("task_id", taskId);
		json.addProperty("subtask_id", subtaskId);
		CQRegistry.getTaskTypes().entrySet()
				  .stream()
				  .filter(entry -> entry.getKey().toString().equals(type.toString()))
				  .forEach(entry ->{
						subTask = entry.getValue().getSecond().get();
						subTask.processJson(json);
				  })
		;
	}
	
	@Override
	public JsonObject getJson(){
		JsonContainer json = new JsonContainer();
		CQRegistry.getTaskTypes().entrySet().stream().filter(entry -> entry.getKey().toString().equals(type.toString())).forEach(entry -> json.set(subTask.getJson()));
		return json.get();
	}
	
	public ITaskType getSubtask(){
		return subTask;
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
}