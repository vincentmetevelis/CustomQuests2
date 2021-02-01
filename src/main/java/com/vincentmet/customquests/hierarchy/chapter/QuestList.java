package com.vincentmet.customquests.hierarchy.chapter;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.IntCounter;
import java.util.ArrayList;

public class QuestList extends ArrayList<Integer> implements IJsonArrayProcessor, IJsonArrayProvider{
	private final int parentChapterId;
	
	public QuestList(int parentChapterId){
		this.parentChapterId = parentChapterId;
	}
	
	public QuestList add(int id){
		if(id >= 0 && stream().noneMatch(integer -> integer == id)){
			super.add(id);
		}
		return this;
	}
	
	@Override
	public void processJson(JsonArray json){
		clear();
		IntCounter counter = new IntCounter();
		for(JsonElement jsonEntriesElement : json){
			if(jsonEntriesElement.isJsonPrimitive()){
				JsonPrimitive jsonEntriesPrimitive = jsonEntriesElement.getAsJsonPrimitive();
				if(jsonEntriesPrimitive.isNumber()){
					int jsonEntriesPrimitiveIntValue = jsonEntriesPrimitive.getAsInt();
					add(jsonEntriesPrimitiveIntValue);
				}else{
					Ref.CustomQuests.LOGGER.warn("'Chapter > " + parentChapterId + " > quests > " + counter.getValue() + "': Value is not an Integer, discarding it for now!");
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Chapter > " + parentChapterId + " > quests > " + counter.getValue() + "': Value is not a JsonPrimitive, please use an Integer, discarding it for now!");
			}
			counter.count();
		}
	}
	
	@Override
	public JsonArray getJson(){
		JsonArray json = new JsonArray();
		forEach(json::add);
		return json;
	}
}