package com.vincentmet.customquests.hierarchy.chapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.IJsonObjectProcessor;
import com.vincentmet.customquests.api.IJsonObjectProvider;
import com.vincentmet.customquests.api.IQuestingTexture;
import com.vincentmet.customquests.gui.editor.EditorEntryWrapper;
import com.vincentmet.customquests.gui.editor.IEditorEntry;
import com.vincentmet.customquests.gui.editor.IEditorPage;
import com.vincentmet.customquests.helpers.TagHelper;
import com.vincentmet.customquests.hierarchy.quest.ItemSlideshowTexture;
import com.vincentmet.customquests.hierarchy.quest.TextType;
import com.vincentmet.customquests.standardcontent.texttypes.TranslationTextType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class Chapter implements IJsonObjectProvider, IJsonObjectProcessor, IEditorPage {
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

	@Override
	public void addPageEntries(List<IEditorEntry> list) {
		list.add(new EditorEntryWrapper(new TranslatableComponent(Ref.MODID + ".editor.keys.icon"), new ResourceLocation(Ref.MODID, "resourcelocation"), () -> getIcon().getResourceLocation().toString(), newValueObject -> {
			ResourceLocation newRL = ResourceLocation.tryParse(newValueObject.toString());
			if(TagHelper.doesTagExist(newRL)){
				List<ItemStack> tagStacks = new ArrayList<>();
				TagHelper.getEntries(newRL).stream().map(ItemStack::new).forEach(tagStacks::add);
				setIcon(new ItemSlideshowTexture(newRL, tagStacks));
			}else{
				if(ForgeRegistries.ITEMS.containsKey(newRL)){
					setIcon(new ItemSlideshowTexture(newRL, new ItemStack(ForgeRegistries.ITEMS.getValue(newRL))));
				}else{
					Ref.CustomQuests.LOGGER.warn("'Chapter > " + id + " > icon': No valid item/tag for '" + newRL + "' found, defaulting to 'minecraft:grass_block'");
					setIcon(new ItemSlideshowTexture(Blocks.GRASS_BLOCK.getRegistryName(), new ItemStack(Blocks.GRASS_BLOCK)));
				}
			}
		}));//todo add a custom dropdown selector for available text-types here && text components
	}
}