package com.vincentmet.customquests.standardcontent.tasktypes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.MouseButton;
import com.vincentmet.customquests.helpers.PlayerBoundSubtaskReference;
import com.vincentmet.customquests.helpers.WorldHelper;
import com.vincentmet.customquests.hierarchy.quest.ItemSlideshowTexture;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BiomeDetectTaskType implements ITaskType{
	private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "biome_detect");
	private static final Component TRANSLATION = new TranslatableComponent(Ref.MODID + ".standardcontent.tasks.biome_detect");
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
    public Component getTranslation(){
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
	public void executeSubtaskCheck(Player player, Object object){
		if(!CombinedProgressHelper.isQuestCompleted(player.getUUID(), questId)){
			if(WorldHelper.isPlayerInBiome(player, biome)){
				CombinedProgressHelper.addValue(player.getUUID(), questId, taskId, subtaskId, getCompletionAmount());
				ServerUtils.sendProgressAndParties((ServerPlayer)player);
			}
			processValue(player);
		}
	}
	
	public void processValue(Player player){
		if(CombinedProgressHelper.getValue(player.getUUID(), questId, taskId, subtaskId) >= getCompletionAmount()){
			CombinedProgressHelper.completeSubtask(player.getUUID(), questId, taskId, subtaskId);
		}
	}
	
	@Override
	public void executeSubtaskButton(Player player){
		/*NOOP*/
	}
	
	@Override
	public IQuestingTexture getIcon(LocalPlayer player){
		return icon;
	}
	
	@Override
	public Runnable onSlotHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, LocalPlayer player){
		return ()->{/*NOOP*/};
	}
	
	@Override
	public String getText(LocalPlayer player){
		return biome.toString();
	}
	
	@Override
	public int getCompletionAmount(){
		return 1;
	}
    
    @Override
    public Consumer<MouseButton> onSlotClick(LocalPlayer player){
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
					biome = ResourceLocation.tryParse(jsonPrimitiveStringValue);
					if(biome == null){
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > biome': Value is not a valid dimension, please use a valid biome id, defaulting to 'minecraft:plains'!");
						biome = Biomes.PLAINS.location();
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > biome': Value is not a String, defaulting to 'minecraft:plains'!");
					biome = Biomes.PLAINS.location();
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > biome': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:plains'!");
				biome = Biomes.PLAINS.location();
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > biome': Not detected, defaulting to 'minecraft:plains'!");
			biome = Biomes.PLAINS.location();
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("biome", biome.toString());
		return json;
	}
}