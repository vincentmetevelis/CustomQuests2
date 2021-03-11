package com.vincentmet.customquests.hierarchy.party;

import com.google.gson.*;
import com.vincentmet.customquests.*;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.IntCounter;
import java.util.*;

public class CollectiveProgress extends HashMap<Integer, SingleQuestPartyProgress> implements IJsonObjectProvider, IJsonObjectProcessor{
    private final int parentPartyId;
    
    public CollectiveProgress(int partyId){
        this.parentPartyId = partyId;
    }
    
    @Override
    public void processJson(JsonObject json){
        IntCounter counter = new IntCounter();
        for(Map.Entry<String, JsonElement> jsonEntryElement : json.entrySet()){
            String key = jsonEntryElement.getKey();
            int keyInt = Integer.parseInt(key);
            JsonElement value = jsonEntryElement.getValue();
            if(value.isJsonObject()){
                JsonObject jsonObjectValue = value.getAsJsonObject();
                SingleQuestPartyProgress singlePartyTaskProgress = new SingleQuestPartyProgress(parentPartyId, keyInt);
                singlePartyTaskProgress.processJson(jsonObjectValue);
                put(keyInt, singlePartyTaskProgress);
            }else{
                if(Config.SidedConfig.isDebugModeOn())Ref.CustomQuests.LOGGER.warn("'Party > " + parentPartyId + " > collective_progress > " + keyInt + "': Value is not a JsonObject, discarding it for now!");
            }
            counter.count();
        }
    }
    
    @Override
    public JsonObject getJson(){
        JsonObject json = new JsonObject();
        forEach((id, element)-> json.add(id.toString(), element.getJson()));
        return json;
    }
    
    public void deleteExcessValues(){
        List<Integer> listOfQuestIds = new ArrayList<>(QuestingStorage.getSidedQuestsMap().keySet());
        List<Integer> listOfProgressQuestIds = new ArrayList<>(keySet());
        listOfProgressQuestIds.removeAll(listOfQuestIds);//remainder of the 'listOfProgressSubtaskIds' contains the progress objects that don't exit anymore in the quests map and therefore should be removed from the player progress
        
        entrySet().removeIf(entry -> listOfProgressQuestIds.contains(entry.getKey()));
        forEach((questId, questProgress) -> questProgress.deleteExcessValues());
    }
    
    public void generateMissingValues(){
        QuestingStorage.getSidedQuestsMap().forEach((questId, quest) -> {
            if(!containsKey(questId)){
                put(questId, new SingleQuestPartyProgress(parentPartyId, questId));
            }
        });
        forEach((questId, singleQuestPartyProgress) -> singleQuestPartyProgress.generateMissingValues());
    }
}
