package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import java.util.HashSet;

public class LockingList extends HashSet<Integer> implements IJsonArrayProcessor, IJsonArrayProvider{
	private final int parentQuestId;
	
	public LockingList(int parentQuestId){
		this.parentQuestId = parentQuestId;
	}
	
	public LockingList add(int id){
		if(id >= 0){
			super.add(id);
		}
		return this;
	}
	
	@Override
	public void processJson(JsonArray json){
		clear();
		
		for(JsonElement jsonEntriesElement : json){
			if(jsonEntriesElement.isJsonPrimitive()){
				JsonPrimitive jsonEntriesPrimitive = jsonEntriesElement.getAsJsonPrimitive();
				if(jsonEntriesPrimitive.isNumber()){
					int jsonEntriesPrimitiveIntValue = jsonEntriesPrimitive.getAsInt();
					add(jsonEntriesPrimitiveIntValue);
				}else{
					Ref.CustomQuests.LOGGER.warn("Value for one of the entries for Json key 'locks' for quest " + parentQuestId + " is not an int, please use an Integer, discarding it for now!");
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("Value for one of the entries for Json key 'locks' for quest " + parentQuestId + " is not a Primitive, please use an Integer, discarding it for now!");
			}
		}
	}
	
	@Override
	public JsonArray getJson(){
		JsonArray json = new JsonArray();
		forEach(json::add);
		return json;
	}
}