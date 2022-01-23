package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.gui.editor.EditorEntryWrapper;
import com.vincentmet.customquests.gui.editor.IEditorEntry;
import com.vincentmet.customquests.gui.editor.IEditorPage;
import com.vincentmet.customquests.helpers.IntCounter;
import java.util.*;
import java.util.stream.Collectors;

public class DependencyList extends HashSet<Integer> implements IJsonObjectProcessor, IJsonObjectProvider, IEditorPage {
	private LogicType logicType = LogicType.AND;
	private final int parentQuestId;
	
	public DependencyList(int parentQuestId){
		this.parentQuestId = parentQuestId;
	}
	
	public List<Quest> asQuestList(){
		return stream().map(QuestHelper::getQuestFromId).filter(Objects::nonNull).collect(Collectors.toList());
	}
	
	public boolean add(Integer id){
		if(id >= 0){
			return super.add(id);
		}
		return false;
	}
	
	@Override
	public void processJson(JsonObject json){
		clear();
		
		if(json.has("logic")){
			JsonElement jsonElement = json.get("logic");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String operator = jsonPrimitive.getAsString();
					if(operator.equalsIgnoreCase("AND") || operator.equalsIgnoreCase("OR")){
						setLogicType(LogicType.valueOf(operator.toUpperCase()));
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > dependencies > logic': Value is not a valid operator, please use 'AND' or 'OR', defaulting to 'AND'!");
						setLogicType(LogicType.AND);
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > dependencies > logic': Value is not a String, defaulting to 'AND'!");
					setLogicType(LogicType.AND);
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > dependencies > logic': Value is not a JsonPrimitive, please use a String, defaulting to 'AND'!");
				setLogicType(LogicType.AND);
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > dependencies > logic': Not detected, defaulting to 'AND'!");
			setLogicType(LogicType.AND);
		}
		
		if(json.has("entries")){
			JsonElement jsonElement = json.get("entries");
			if(jsonElement.isJsonArray()){
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				IntCounter counter = new IntCounter();
				for(JsonElement jsonEntriesElement : jsonArray){
					if(jsonEntriesElement.isJsonPrimitive()){
						JsonPrimitive jsonEntriesPrimitive = jsonEntriesElement.getAsJsonPrimitive();
						if(jsonEntriesPrimitive.isNumber()){
							int jsonEntriesPrimitiveIntValue = jsonEntriesPrimitive.getAsInt();
							add(jsonEntriesPrimitiveIntValue);
						}else{
							Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > dependencies > entries > " + counter.getValue() + "': Value is not an Integer, discarding it for now!");
						}
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > dependencies > entries > " + counter.getValue() + "': Value is not a JsonPrimitive, please use an Integer, discarding it for now!");
					}
					counter.count();
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > dependencies > entries': Value is not a JsonArray, generating a new one!");
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > dependencies > entries': Not detected, generating a new JsonArray!");
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("logic", logicType.toString());
		JsonArray jsonEntries = new JsonArray();
		forEach(jsonEntries::add);
		json.add("entries", jsonEntries);
		return json;
	}
	
	public void setLogicType(LogicType logicType){
		this.logicType = logicType;
	}
	
	public LogicType getLogicType(){
		return logicType;
	}

	@Override
	public void addPageEntries(List<IEditorEntry> list) {
		//todo list.add(new EditorEntryWrapper());
	}
}
