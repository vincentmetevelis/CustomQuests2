package com.vincentmet.customquests.hierarchy.party;

import com.google.gson.*;
import com.vincentmet.customquests.*;
import com.vincentmet.customquests.api.*;
import java.util.UUID;

public class Party implements IJsonObjectProvider, IJsonObjectProcessor{
	private final int id;
	private String name = "";
	private UUID owner;
	private PartyType partyType;
	private final CollectivelyCompletedQuestList collectivelyCompletedQuestList;
	private final CollectiveProgress collectiveProgress;
	
	public Party(int id){
		this.id = id;
		this.collectivelyCompletedQuestList = new CollectivelyCompletedQuestList(id);
		this.collectiveProgress = new CollectiveProgress(id);
	}
	
	@Override
	public void processJson(JsonObject json){
		if(json.has("name")){
			JsonElement jsonElement = json.get("name");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
					setName(jsonPrimitiveStringValue);
				}else{
					if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + id + " > name': Value is not a String, defaulting to an empty String!");
				}
			}else{
				if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + id + " > name': Value is not a JsonPrimitive, please use a String, defaulting to an empty String!");
			}
		}else{
			if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + id + " > name': Not detected, defaulting to an empty String!");
		}
		
		if(json.has("type")){
			JsonElement jsonElement = json.get("type");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
					switch(jsonPrimitiveStringValue){
						case "FFA":
							setPartyType(PartyType.FFA);
							break;
						default:
						case "INVITE_ONLY":
							setPartyType(PartyType.INVITE_ONLY);
							break;
					}
				}else{
					if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + id + " > type': Value is not a String, defaulting to 'INVITE_ONLY'!");
					setPartyType(PartyType.INVITE_ONLY);
				}
			}else{
				if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + id + " > type': Value is not a JsonPrimitive, please use a String, defaulting to 'INVITE_ONLY'!");
				setPartyType(PartyType.INVITE_ONLY);
			}
		}else{
			if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + id + " > type': Not detected, defaulting to 'INVITE_ONLY'!");
			setPartyType(PartyType.INVITE_ONLY);
		}
		
		if(json.has("owner")){
			JsonElement jsonElement = json.get("owner");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
					setOwner(UUID.fromString(jsonPrimitiveStringValue));
				}else{
					if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + id + " > owner': Value is not a UUID String, deleting party!");
					PartyHelper.addPartyToDeletionQueue(id);
				}
			}else{
				if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + id + " > owner': Value is not a JsonPrimitive, deleting party!");
				PartyHelper.addPartyToDeletionQueue(id);
			}
		}else{
			if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + id + " > owner': Not detected, deleting party!");
			PartyHelper.addPartyToDeletionQueue(id);
		}
		
		if(json.has("quests_collectively_completed")){
			JsonElement jsonElement = json.get("quests_collectively_completed");
			if(jsonElement.isJsonArray()){
				JsonArray jsonObject = jsonElement.getAsJsonArray();
				collectivelyCompletedQuestList.processJson(jsonObject);
			}else{
				if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + id + " > quests_collectively_completed': Value is not a JsonArray, generating a new one!");
				collectivelyCompletedQuestList.processJson(new JsonArray());
			}
		}else{
			if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + id + " > quests_collectively_completed': Not detected, generating a new JsonArray!");
			collectivelyCompletedQuestList.processJson(new JsonArray());
		}
		
		if(json.has("collective_progress")){
			JsonElement jsonElement = json.get("collective_progress");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				collectiveProgress.processJson(jsonObject);
			}else{
				if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + id + " > collective_progress': Value is not a JsonObject, generating a new one!");
				collectiveProgress.processJson(new JsonObject());
			}
		}else{
			if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + id + " > collective_progress': Not detected, generating a new JsonObject!");
			collectiveProgress.processJson(new JsonObject());
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("name", name);
		json.addProperty("type", partyType.toString());
		json.addProperty("owner", owner.toString());
		json.add("quests_collectively_completed", collectivelyCompletedQuestList.getJson());
		json.add("collective_progress", collectiveProgress.getJson());
		return json;
	}
	
	public enum PartyType{
		FFA(),
		INVITE_ONLY()
	}
	
	public UUID getOwner(){
		return owner;
	}
	
	public CollectiveProgress getCollectiveProgress(){
		return collectiveProgress;
	}
	
	public void setOwner(UUID owner){
		this.owner = owner;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setPartyType(PartyType partyType){
		this.partyType = partyType;
	}
	
	public String getName(){
		return name;
	}
	
	public int getId(){
		return id;
	}
	
	public PartyType getPartyType(){
		return partyType;
	}
	
	public CollectivelyCompletedQuestList getCollectivelyCompletedQuestList(){
		return collectivelyCompletedQuestList;
	}
}