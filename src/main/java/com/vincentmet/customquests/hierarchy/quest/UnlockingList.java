package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.api.exception.JsonValueTypeMismatch;
import java.util.ArrayList;

public class UnlockingList extends ArrayList<Integer> implements IJsonArrayProcessor, IJsonArrayProvider{
	private int parentQuestId;
	
	public UnlockingList(){
	
	}
	
	public UnlockingList(int parentQuestId){
		this.parentQuestId = parentQuestId;
	}
	
	public UnlockingList add(int id){
		if(id >= 0 && stream().noneMatch(integer -> integer == id)){
			super.add(id);
		}
		return this;
	}
	
	public Integer get(int index){
		if(index<size()){
			return super.get(index);
		}
		throw new IllegalArgumentException("An index was given that was '< 0' or '>= size()'");
	}
	
	@Override
	public void processJson(JsonArray json) throws JsonValueTypeMismatch{
		clear();
		
		for(JsonElement jsonEntriesElement : json){
			if(jsonEntriesElement.isJsonPrimitive()){
				JsonPrimitive jsonEntriesPrimitive = jsonEntriesElement.getAsJsonPrimitive();
				if(jsonEntriesPrimitive.isNumber()){
					int jsonEntriesPrimitiveIntValue = jsonEntriesPrimitive.getAsInt();
					add(jsonEntriesPrimitiveIntValue);
				}else{
					Ref.CustomQuests.LOGGER.warn("Value for one of the entries for Json key 'unlocks' for quest " + parentQuestId + " is not an int, please use an Integer, discarding it for now!");
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("Value for one of the entries for Json key 'unlocks' for quest " + parentQuestId + " is not a Primitive, please use an Integer, discarding it for now!");
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