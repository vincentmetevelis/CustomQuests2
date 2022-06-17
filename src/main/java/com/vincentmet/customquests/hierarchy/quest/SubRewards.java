package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.IJsonObjectProcessor;
import com.vincentmet.customquests.api.IJsonObjectProvider;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

public class SubRewards extends HashMap<Integer, SubReward> implements IJsonObjectProvider, IJsonObjectProcessor{
	private final int parentQuestId;
	private final int parentRewardId;
	
	public SubRewards(int parentQuestId, int parentRewardId){
		this.parentQuestId = parentQuestId;
		this.parentRewardId = parentRewardId;
	}
	
	public SubReward put(Integer id, SubReward subReward){
		if(id>=0){
			super.put(id, subReward);
		}
		return subReward;
	}
	
	@Override
	public void processJson(JsonObject json){
		clear();
		
		if(json.has("entries")){
			JsonElement jsonElement = json.get("entries");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				for(Entry<String, JsonElement> jsonEntryElement : jsonObject.entrySet()){
					String key = jsonEntryElement.getKey();
					int keyInt = Integer.parseInt(key);
					JsonElement value = jsonEntryElement.getValue();
					if(value.isJsonObject()){
						JsonObject jsonObjectValue = value.getAsJsonObject();
						SubReward subTask = new SubReward(parentQuestId, parentRewardId, keyInt);
						subTask.processJson(jsonObjectValue);
						put(keyInt, subTask);
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + keyInt + "': Value is not a JsonObject, discarding it for now!");
					}
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries': Value is not a JsonObject, generating a new one!");
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries': Not detected, generating a new JsonObject!");
		}
	}
	
	public void executeAllSubrewards(ServerPlayer player){
		forEach((subtaskId, subtask)->{
			subtask.getSubreward().executeReward(player);
		});
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		JsonObject jsonEntries = new JsonObject();
		for(Entry<Integer, SubReward> entry : entrySet()){
			jsonEntries.add(entry.getKey().toString(), entry.getValue().getJson());
		}
		json.add("entries", jsonEntries);
		return json;
	}
}
