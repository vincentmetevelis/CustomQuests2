package com.vincentmet.customquests.hierarchy.chapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.gui.editor.EditorEntryWrapper;
import com.vincentmet.customquests.gui.editor.IEditorEntry;
import com.vincentmet.customquests.gui.editor.IEditorPage;
import com.vincentmet.customquests.helpers.TagHelper;
import com.vincentmet.customquests.hierarchy.quest.ItemSlideshowTexture;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class Chapter implements IJsonObjectProvider, IJsonObjectProcessor, IEditorPage {
	private static final IQuestingTexture DEFAULT_ICON = new ItemSlideshowTexture(Blocks.GRASS_BLOCK.getRegistryName(), new ItemStack(Blocks.GRASS_BLOCK));
	private final int id;
	private IQuestingTexture icon;
	private final ChapterTitleTextType title;
	private final ChapterTextTextType text;
	private final QuestList quests;
	
	public Chapter(int id, IQuestingTexture icon, ChapterTitleTextType title, ChapterTextTextType text, QuestList quests){
		this.id = id;
		this.icon = icon;
		this.title = title;
		this.text = text;
		this.quests = quests;
	}
	
	public Chapter(int id){
		this(id, null, new ChapterTitleTextType(id), new ChapterTextTextType(id), new QuestList(id));
	}
	
	@Override
	public void processJson(JsonObject json){
		if(json.has("icon")){
			JsonElement jsonElement = json.get("icon");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					setIcon(new ResourceLocation(jsonPrimitive.getAsString()));
				}else{
					Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > icon': Value is not a String, defaulting to '"+Blocks.GRASS_BLOCK.getRegistryName()+"'!");
					setIcon(DEFAULT_ICON);
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > icon': Value is not a JsonPrimitive, please use a String, defaulting to '"+Blocks.GRASS_BLOCK.getRegistryName()+"'!");
				setIcon(DEFAULT_ICON);
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > icon': Not detected, defaulting to '"+Blocks.GRASS_BLOCK.getRegistryName()+"'!");
			setIcon(DEFAULT_ICON);
		}
		
		if(json.has("title")){
			JsonElement jsonElement = json.get("title");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				title.processJson(jsonObject);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > title': Value is not a JsonObject, generating a new one!");
				title.processJson(new JsonObject());
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > title': Not detected, generating a new JsonObject!");
			title.processJson(new JsonObject());
		}
		
		if(json.has("text")){
			JsonElement jsonElement = json.get("text");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				text.processJson(jsonObject);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > text': Value is not a JsonObject, generating a new one!");
				text.processJson(new JsonObject());
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > text': Not detected, generating a new JsonObject!");
			text.processJson(new JsonObject());
		}
		
		if(json.has("quests")){
			JsonElement jsonElement = json.get("quests");
			if(jsonElement.isJsonArray()){
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				quests.processJson(jsonArray);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > quests': Value is not a JsonArray, generating a new one!");
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > quests': Not detected, generating a new JsonArray!");
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("icon", icon.toString());
		json.add("title", title.getJson());
		json.add("text", text.getJson());
		json.add("quests", quests.getJson());
		return json;
	}
	
	public int getId(){
		return id;
	}
	
	public IQuestingTexture getIcon(){
		return icon;
	}
	
	public ChapterTitleTextType getTitle(){
		return title;
	}
	
	public ChapterTextTextType getText(){
		return text;
	}
	
	public QuestList getQuests(){
		return quests;
	}

	public void setIcon(IQuestingTexture icon){
		if (icon != null && icon.isValid()){
			this.icon = icon;
		}else{
			Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > icon': The given texture is either null or invalid, defaulting to '"+Blocks.GRASS_BLOCK.getRegistryName()+"'!");
			setIcon(DEFAULT_ICON);
		}
	}

	public void setIcon(ResourceLocation iconRL){
		if(TagHelper.Items.doesTagExist(iconRL)){
			List<ItemStack> tagStacks = new ArrayList<>();
			TagHelper.Items.getEntries(iconRL).stream().map(ItemStack::new).forEach(tagStacks::add);
			setIcon(new ItemSlideshowTexture(iconRL, tagStacks));
		}else{
			if(ForgeRegistries.ITEMS.containsKey(iconRL)){
				setIcon(new ItemSlideshowTexture(iconRL, new ItemStack(ForgeRegistries.ITEMS.getValue(iconRL))));
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > icon': There is no valid item/tag with ResourceLocation '" + iconRL + "' found, defaulting to '"+Blocks.GRASS_BLOCK.getRegistryName()+"'!");
				setIcon(DEFAULT_ICON);
			}
		}
	}

	@Override
	public void addPageEntries(List<IEditorEntry> list) {
		list.add(new EditorEntryWrapper(new TranslatableComponent(Ref.MODID + ".editor.keys.icon"), new ResourceLocation(Ref.MODID, "resourcelocation"), () -> getIcon().getResourceLocation().toString(), newValueObject -> {
			ResourceLocation newRL = ResourceLocation.tryParse(newValueObject.toString());
			setIcon(newRL);
			EditorGuiHelper.Update.Chapter.requestUpdateIcon(id, getIcon().getResourceLocation());
		}));//todo add a custom dropdown selector (or a scroll-through-button(i.e. vanilla world type buttons in world gen menu)) for available text-types here && text components
	}
}