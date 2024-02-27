package com.vincentmet.customquests.hierarchy.chapter;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.gui.editor.EditorEntryWrapper;
import com.vincentmet.customquests.gui.editor.IEditorEntry;
import com.vincentmet.customquests.gui.editor.IEditorPage;
import com.vincentmet.customquests.helpers.IntCounter;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.List;

public class QuestList extends HashSet<Integer> implements IJsonArrayProcessor, IJsonArrayProvider, IEditorPage {
	private final int parentChapterId;
	
	public QuestList(int parentChapterId){
		this.parentChapterId = parentChapterId;
	}
	
	public QuestList add(int id){
		if(id >= 0){
			super.add(id);
		}
		return this;
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
					Ref.CustomQuests.LOGGER.warn("'Chapter > " + parentChapterId + " > quests > " + counter.getValue() + "': Value is not an Integer, discarding it for now!");
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Chapter > " + parentChapterId + " > quests > " + counter.getValue() + "': Value is not a JsonPrimitive, please use an Integer, discarding it for now!");
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

	@Override
	public void addPageEntries(List<IEditorEntry> list) {
		forEach(questId -> {
			list.add(new EditorEntryWrapper(new TextComponent(""), new ResourceLocation(Ref.MODID, "integer"), () -> questId, newValueObject -> {
				//todo maybe create a new screen for it, passing important data to it, including the instance of the editor screen, then go back to that instance on close or on save, instead of opening a new screen
			}));
		});
	}
}