package com.vincentmet.customquests.hierarchy.party;

import com.google.gson.*;
import com.vincentmet.customquests.*;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.IntCounter;
import java.util.*;

public class CollectivelyCompletedQuestList extends HashSet<Integer> implements IJsonArrayProcessor, IJsonArrayProvider{
	private final int parentPartyId;
	
	public CollectivelyCompletedQuestList(int parentPartyId){
		this.parentPartyId = parentPartyId;
	}
	
	public boolean add(Integer id){
		if(id >= 0){
			super.add(id);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean addAll(Collection<? extends Integer> c){
		c.forEach(this::add);
		return c.size()>=1;
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
					if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + parentPartyId + " > quests_collectively_completed > " + counter.getValue() + "': Value is not an Integer, discarding it for now!");
				}
			}else{
				if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'User > " + parentPartyId + " > quests_collectively_completed > " + counter.getValue() + "': Value is not a JsonPrimitive, please use an Integer, discarding it for now!");
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
