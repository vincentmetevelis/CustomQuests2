package com.vincentmet.customquests.standardcontent.tasktypes;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.*;
import com.vincentmet.customquests.hierarchy.quest.ItemSlideshowTexture;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraft.world.biome.Biomes;

public class BiomeDetectTaskType implements ITaskType{
	private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "biome_detect");
	private static final ITextComponent TRANSLATION = new TranslationTextComponent(Ref.MODID + ".standardcontent.tasks.biome_detect");
	public static final List<PlayerBoundSubtaskReference> TRACKING_LIST = new ArrayList<>();
	private int questId;
	private int taskId;
	private int subtaskId;
	
	private ResourceLocation biome;
	
	ItemSlideshowTexture icon = new ItemSlideshowTexture(Items.COMPASS.getRegistryName(), new ItemStack(Items.COMPASS));
	
	@Override
	public ResourceLocation getId(){
		return ID;
	}
    
    @Override
    public ITextComponent getTranslation(){
        return TRANSLATION;
    }
	
	@Override
	public List<PlayerBoundSubtaskReference> getCurrentlyTrackingList(){
		return TRACKING_LIST;
	}
	
	@Override
	public boolean hasButton(ButtonContext context){
		return false;
	}
	
	@Override
	public void executeSubtaskCheck(PlayerEntity player, Object object){
		if(!CombinedProgressHelper.isQuestCompleted(player.getUniqueID(), questId)){
			if(WorldHelper.isPlayerInBiome(player, biome)){
				CombinedProgressHelper.addValue(player.getUniqueID(), questId, taskId, subtaskId, getCompletionAmount());
				ServerUtils.sendProgressAndParties((ServerPlayerEntity)player);
			}
			processValue(player);
		}
	}
	
	public void processValue(PlayerEntity player){
		if(CombinedProgressHelper.getValue(player.getUniqueID(), questId, taskId, subtaskId) >= getCompletionAmount()){
			CombinedProgressHelper.completeSubtask(player.getUniqueID(), questId, taskId, subtaskId);
		}
	}
	
	@Override
	public void executeSubtaskButton(PlayerEntity player){
		/*NOOP*/
	}
	
	@Override
	public IQuestingTexture getIcon(ClientPlayerEntity player){
		return icon;
	}
	
	@Override
	public Runnable onSlotHover(int mouseX, int mouseY, float partialTicks, ClientPlayerEntity player){
		return ()->{/*NOOP*/};
	}
	
	@Override
	public String getText(ClientPlayerEntity player){
		return biome.toString();
	}
	
	@Override
	public int getCompletionAmount(){
		return 1;
	}
    
    @Override
    public Consumer<MouseButton> onSlotClick(ClientPlayerEntity player){
		return (mouseButton)->{/*NOOP*/};
    }
    
    @Override
	public void processJson(JsonObject json){
		if(json.has("quest_id")){
			JsonElement jsonElement = json.get("quest_id");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					questId = jsonPrimitive.getAsInt();
				}
			}
		}
		if(json.has("task_id")){
			JsonElement jsonElement = json.get("task_id");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					taskId = jsonPrimitive.getAsInt();
				}
			}
		}
		if(json.has("subtask_id")){
			JsonElement jsonElement = json.get("subtask_id");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					subtaskId = jsonPrimitive.getAsInt();
				}
			}
		}
		
		if(json.has("dimension")){
			JsonElement jsonElement = json.get("dimension");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
					biome = ResourceLocation.tryCreate(jsonPrimitiveStringValue);
					if(biome == null){
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > biome': Value is not a valid dimension, please use a valid biome id, defaulting to 'minecraft:plains'!");
						biome = Biomes.PLAINS.getRegistryName();
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > biome': Value is not a String, defaulting to 'minecraft:plains'!");
					biome = Biomes.PLAINS.getRegistryName();
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > biome': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:plains'!");
				biome = Biomes.PLAINS.getRegistryName();
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > biome': Not detected, defaulting to 'minecraft:plains'!");
			biome = Biomes.PLAINS.getRegistryName();
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("biome", biome.toString());
		return json;
	}
}