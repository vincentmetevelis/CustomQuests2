package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.JsonObject;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.api.logic.ITaskType;
import com.vincentmet.customquests.helpers.JsonContainer;
import net.minecraft.util.ResourceLocation;

public class SubTask implements IJsonObjectProvider, IJsonObjectProcessor{
	private final ResourceLocation type;
	private final int parentQuestId;
	private final int parentTaskId;
	private final int subtaskId;
	private ITaskType subTask;
	
	public SubTask(int parentQuestId, int parentTaskId, int subtaskId, ResourceLocation type){
		this.type = type;
		this.parentQuestId = parentQuestId;
		this.parentTaskId = parentTaskId;
		this.subtaskId = subtaskId;
	}
	
	@Override
	public void processJson(JsonObject json){
		json.addProperty("parent_quest_id", parentQuestId);
		json.addProperty("parent_task_id", parentTaskId);
		json.addProperty("parent_subtask_id", subtaskId);
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
	
	@Override
	public String toString(){
		return "SubTask{" + "type=" + type + ", subTask=" + subTask + '}';
	}
	
	public int getSubtaskId(){
		return subtaskId;
	}
}
