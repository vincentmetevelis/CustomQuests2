package com.vincentmet.customquests.hierarchy.chapter;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.TagHelper;
import com.vincentmet.customquests.hierarchy.quest.*;
import java.util.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class Chapter implements IJsonObjectProvider, IJsonObjectProcessor{
	private final int id;
	private IQuestingTexture icon;
	private TextType title;
	private TextType text;
	private final QuestList quests;
	
	
	public Chapter(int id, IQuestingTexture icon, TextType title, TextType text, QuestList quests){
		this.id = id;
		this.icon = icon;
		this.title = title;
		this.text = text;
		this.quests = quests;
	}
	
	public Chapter(int id){
		this(id, null, new TextType(id, "Chapter", "title"), new TextType(id, "Chapter", "text"), new QuestList(id));
	}
	
	@Override
	public void processJson(JsonObject json){
		if(json.has("icon")){
			JsonElement jsonElement = json.get("icon");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
					ResourceLocation jsonResourceLocationValue = new ResourceLocation(jsonPrimitiveStringValue);
					if(TagHelper.doesTagExist(jsonResourceLocationValue)){
						List<ItemStack> tagStacks = new ArrayList<>();
						TagHelper.getEntries(jsonResourceLocationValue).stream().map(ItemStack::new).forEach(tagStacks::add);
						setIcon(new ItemSlideshowTexture(jsonResourceLocationValue, tagStacks));
					}else{
						if(ForgeRegistries.ITEMS.containsKey(jsonResourceLocationValue)){
							setIcon(new ItemSlideshowTexture(jsonResourceLocationValue, new ItemStack(ForgeRegistries.ITEMS.getValue(jsonResourceLocationValue))));
						}else{
							Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > icon': No valid item/tag for '" + jsonPrimitiveStringValue + "' found, defaulting to 'minecraft:grass_block'");
							setIcon(new ItemSlideshowTexture(Blocks.GRASS_BLOCK.getRegistryName(), new ItemStack(Blocks.GRASS_BLOCK)));
						}
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > icon': Value is not a String, defaulting to 'minecraft:grass_block'");
					setIcon(new ItemSlideshowTexture(Blocks.GRASS_BLOCK.getRegistryName(), new ItemStack(Blocks.GRASS_BLOCK)));
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > icon': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:grass_block'");
				setIcon(new ItemSlideshowTexture(Blocks.GRASS_BLOCK.getRegistryName(), new ItemStack(Blocks.GRASS_BLOCK)));
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > icon': Not detected, defaulting to 'minecraft:grass_block'");
			setIcon(new ItemSlideshowTexture(Blocks.GRASS_BLOCK.getRegistryName(), new ItemStack(Blocks.GRASS_BLOCK)));
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
	
	public TextType getTitle(){
		return title;
	}
	
	public TextType getText(){
		return text;
	}
	
	public QuestList getQuests(){
		return quests;
	}
	
	public void setIcon(IQuestingTexture icon){
		this.icon = icon;
	}
	
	public void setTitle(TextType title){
		this.title = title;
	}
	
	public void setText(TextType text){
		this.text = text;
	}
}