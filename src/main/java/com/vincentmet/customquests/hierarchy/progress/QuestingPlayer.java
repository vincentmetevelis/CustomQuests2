package com.vincentmet.customquests.hierarchy.progress;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import java.util.UUID;

public class QuestingPlayer implements IJsonObjectProvider, IJsonObjectProcessor{
	private final UUID uuid;
	private int party = -1;
	private final UserProgress individualProgress;
	
	public QuestingPlayer(UUID uuid){
		this.uuid = uuid;
		this.individualProgress = new UserProgress(uuid);
	}
	
	@Override
	public void processJson(JsonObject json){
		if(json.has("party")){
			JsonElement jsonElement = json.get("party");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					int jsonPrimitiveIntValue = jsonPrimitive.getAsInt();
					if(jsonPrimitiveIntValue>=0){ // Either get a positive party id and set all negative values to -1
						setParty(jsonPrimitiveIntValue);
					}else{
						setParty(-1);
					}
				}else{
					if(Ref.DEV_MODE)Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > party': Value is not an Integer, defaulting to '-1'!");
					setParty(-1);
				}
			}else{
				if(Ref.DEV_MODE)Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > party': Value is not a JsonPrimitive, please use an Integer, defaulting to '-1'!");
				setParty(-1);
			}
		}else{
			if(Ref.DEV_MODE)Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > party': Not detected, defaulting to '-1'!");
			setParty(-1);
		}
		
		if(json.has("individual_progress")){
			JsonElement jsonElement = json.get("individual_progress");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				individualProgress.processJson(jsonObject);
			}else{
				if(Ref.DEV_MODE)Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress': Value is not a JsonObject, generating a new one!");
				individualProgress.processJson(new JsonObject());
			}
		}else{
			if(Ref.DEV_MODE)Ref.CustomQuests.LOGGER.warn("'User > " + uuid.toString() + " > individual_progress': Not detected, generating a new JsonObject!");
			individualProgress.processJson(new JsonObject());
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("party", party);
		json.add("individual_progress", individualProgress.getJson());
		return json;
	}
	
	public void deleteExcessValues(){
		individualProgress.deleteExcessValues();
	}
	
	public void generateMissingValues(){
		individualProgress.generateMissingValues();
	}
	
	public int getParty(){
		return party;
	}
	
	public void setParty(int party){
		if(party>=0 && QuestingStorage.getSidedPartiesMap().containsKey(party)){
			this.party = party;
		}else{
			this.party = -1;
		}
	}
	
	public UserProgress getIndividualProgress(){
		return individualProgress;
	}
	
	public UUID getUuid(){
		return uuid;
	}
	
	@Override
	public String toString(){
		return "QuestingPlayer{" + "uuid=" + uuid + ", party=" + party + ", individualProgress=" + individualProgress + '}';
	}
}
