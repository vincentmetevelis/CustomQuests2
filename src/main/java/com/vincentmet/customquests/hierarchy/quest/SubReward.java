package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import net.minecraft.resources.ResourceLocation;

public class SubReward implements IJsonObjectProvider, IJsonObjectProcessor{
	private final int parentQuestId;
	private final int parentRewardId;
	private final int subRewardId;
	private ResourceLocation rewardType;
	private IRewardType subreward;
	
	public SubReward(int parentQuestId, int parentRewardId, int subRewardId){
		this.parentQuestId = parentQuestId;
		this.parentRewardId = parentRewardId;
		this.subRewardId = subRewardId;
	}
	
	@Override
	public void processJson(JsonObject json){
		json.addProperty("parent_quest_id", parentQuestId);
		json.addProperty("parent_reward_id", parentRewardId);
		json.addProperty("parent_subreward_id", subRewardId);
		
		//Four lines below are for the debugging messages to work correctly when generating new JsonObjects
		JsonObject errJsonObject = new JsonObject();
		errJsonObject.addProperty("parent_quest_id", parentQuestId);
		errJsonObject.addProperty("parent_reward_id", parentRewardId);
		errJsonObject.addProperty("parent_subreward_id", subRewardId);
		
		if(json.has("type")){
			JsonElement jsonElement = json.get("type");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonString = jsonPrimitive.getAsString();
					if(CQRegistry.getRewardTypes().keySet().stream().anyMatch(rewardTypeId -> rewardTypeId.toString().equals(jsonString))){
						rewardType = new ResourceLocation(jsonString);
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > entries > " + subRewardId + " > type': Value does not match a registered RewardType, please download the addon mod it belongs to, or change it to something valid, discarding it for now!");
						rewardType = new ResourceLocation(Ref.MODID, "items");
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > entries > " + subRewardId + " > type': Value is not a String, discarding it for now!");
					rewardType = new ResourceLocation(Ref.MODID, "items");
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > entries > " + subRewardId + " > type': Value is not a JsonPrimitive, please use a String, discarding it for now!");
				rewardType = new ResourceLocation(Ref.MODID, "items");
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > entries > " + subRewardId + " > type': Not detected, discarding it for now!");
			rewardType = new ResourceLocation(Ref.MODID, "items");
		}
		
		if(json.has("content")){
			JsonElement jsonElement = json.get("content");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				CQRegistry.getRewardTypes().entrySet()
						  .stream()
						  .filter(entry -> entry.getKey().toString().equals(rewardType.toString()))
						  .forEach(entry->{
							  subreward = entry.getValue().get();
							  subreward.processJson(jsonObject);
						  })
				;
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > entries > " + subRewardId + " > content': Value is not a JsonObject, generating a new one!");
				CQRegistry.getRewardTypes().entrySet()
						  .stream()
						  .filter(entry -> entry.getKey().toString().equals(rewardType.toString()))
						  .forEach(entry->{
							  subreward = entry.getValue().get();
							  subreward.processJson(errJsonObject);
						  })
				;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > entries > " + subRewardId + " > content': Not detected, generating a new JsonObject!");
			CQRegistry.getRewardTypes().entrySet()
					  .stream()
					  .filter(entry -> entry.getKey().toString().equals(rewardType.toString()))
					  .forEach(entry->{
						  subreward = entry.getValue().get();
						  subreward.processJson(errJsonObject);
					  })
			;
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("type", rewardType.toString());
		CQRegistry.getRewardTypes().entrySet()
				  .stream()
				  .filter(entry -> entry.getKey().toString().equals(rewardType.toString()))
				  .forEach(entry->json.add("content", subreward.getJson()));
		return json;
	}
	
	public IRewardType getSubreward(){
		return subreward;
	}
	
	public ResourceLocation getRewardType(){
		return rewardType;
	}
	
	public int getSubRewardId(){
		return subRewardId;
	}
	
	@Override
	public String toString(){
		return "SubReward{" + "parentQuestId=" + parentQuestId + ", parentRewardId=" + parentRewardId + ", subRewardId=" + subRewardId + ", rewardType=" + rewardType + ", subreward=" + subreward + '}';
	}
}
