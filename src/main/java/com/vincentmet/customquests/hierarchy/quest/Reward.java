package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;

public class Reward implements IJsonObjectProcessor, IJsonObjectProvider{
	private final int parentQuestId;
	private final int rewardId;
	private final SubRewards subRewards;
	
	public Reward(int parentQuestId, int rewardId){
		this.parentQuestId = parentQuestId;
		this.rewardId = rewardId;
		this.subRewards = new SubRewards(parentQuestId, rewardId);
	}
	
	@Override
	public void processJson(JsonObject json){
		if(json.has("sub_rewards")){
			JsonElement jsonElement = json.get("sub_rewards");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				subRewards.processJson(jsonObject);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + rewardId + " > sub_rewards': Value is not a JsonObject, generating a new one!");
				subRewards.processJson(new JsonObject());
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + rewardId + " > sub_rewards': Not detected, generating a new JsonObject!");
			subRewards.processJson(new JsonObject());
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.add("sub_rewards", subRewards.getJson());
		return json;
	}
	
	public int getParentQuestId(){
		return parentQuestId;
	}
	
	public int getRewardId(){
		return rewardId;
	}
	
	public SubRewards getSubRewards(){
		return subRewards;
	}
}