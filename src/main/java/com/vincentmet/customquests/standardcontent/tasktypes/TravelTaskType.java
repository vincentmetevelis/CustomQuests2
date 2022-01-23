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
import net.minecraft.world.level.dimension.DimensionType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TravelTaskType implements ITaskType{
	private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "travel");
	private static final Component TRANSLATION_TASK_TITLE = new TranslatableComponent(Ref.MODID + ".standardcontent.tasks.travel");
	private static final Component TRANSLATION_DIMENSION = new TranslatableComponent(Ref.MODID + ".general.dimension");
	private static final Component TRANSLATION_POSITION = new TranslatableComponent(Ref.MODID + ".general.position");
	public static final List<PlayerBoundSubtaskReference> TRACKING_LIST = new ArrayList<>();
	private int questId;
	private int taskId;
	private int subtaskId;
	
	private ResourceLocation dimension;
	private int x;
	private int y;
	private int z;
	private int range;
	
	ItemSlideshowTexture icon = new ItemSlideshowTexture(Items.COMPASS.getRegistryName(), new ItemStack(Items.COMPASS));
	
	@Override
	public ResourceLocation getId(){
		return ID;
	}
    
    @Override
    public Component getTranslation(){
        return TRANSLATION_TASK_TITLE;
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
			if(range <= -1){
				if(WorldHelper.isPlayerInDimension(player, dimension)){
					CombinedProgressHelper.addValue(player.getUUID(), questId, taskId, subtaskId, getCompletionAmount());
					ServerUtils.sendProgressAndParties((ServerPlayer) 	player);
				}
			}else{
				if(WorldHelper.isPlayerInDimension(player, dimension) && WorldHelper.isPlayerInRange(player, x, y, z, range)){
					CombinedProgressHelper.addValue(player.getUUID(), questId, taskId, subtaskId, getCompletionAmount());
					ServerUtils.sendProgressAndParties((ServerPlayer) player);
				}
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
		if(range <= -1){
			return TRANSLATION_DIMENSION.getString() + ": " + dimension.toString();
		}else{
			return TRANSLATION_DIMENSION.getString() + ": " + dimension.toString() + ", " + TRANSLATION_POSITION.getString() + ": [X: " + x + ", Y: " + y + ", Z: " + z + "]";
		}
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
					dimension = ResourceLocation.tryParse(jsonPrimitiveStringValue);
					if(dimension == null){
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > dimension': Value is not a valid dimension, please use a valid dimension id, defaulting to 'minecraft:overworld'!");
						dimension = DimensionType.OVERWORLD_LOCATION.location();//todo test this RL
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > dimension': Value is not a String, defaulting to 'minecraft:overworld'!");
					dimension = DimensionType.OVERWORLD_LOCATION.location();
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > dimension': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:overworld'!");
				dimension = DimensionType.OVERWORLD_LOCATION.location();
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > dimension': Not detected, defaulting to 'minecraft:overworld'!");
			dimension = DimensionType.OVERWORLD_LOCATION.location();
		}
	
		if(json.has("x")){
			JsonElement jsonElement = json.get("x");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					x = jsonPrimitive.getAsInt();
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > x': Value is not an Integer, defaulting to '0'!");
					x = 0;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > x': Value is not a JsonPrimitive, please use a Double, defaulting to '0'!");
				x = 0;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > x': Not detected, defaulting to '0'!");
			x = 0;
		}
	
		if(json.has("y")){
			JsonElement jsonElement = json.get("y");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					y = jsonPrimitive.getAsInt();
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > y': Value is not an Integer, defaulting to '0'!");
					y = 0;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > y': Value is not a JsonPrimitive, please use a Double, defaulting to '0'!");
				y = 0;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > y': Not detected, defaulting to '0'!");
			y = 0;
		}
	
		if(json.has("z")){
			JsonElement jsonElement = json.get("z");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					z = jsonPrimitive.getAsInt();
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > z': Value is not an Integer, defaulting to '0'!");
					z = 0;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > z': Value is not a JsonPrimitive, please use a Double, defaulting to '0'!");
				z = 0;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > z': Not detected, defaulting to '0'!");
			z = 0;
		}
	
		if(json.has("range")){
			JsonElement jsonElement = json.get("range");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					if(jsonPrimitive.getAsInt() >=0){
						range = jsonPrimitive.getAsInt();
					}else{
						range = -1;
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > range': Value is not an Integer, defaulting to '-1'!");
					range = -1;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > range': Value is not a JsonPrimitive, please use a Double, defaulting to '-1'!");
				range = -1;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > range': Not detected, defaulting to '-1'!");
			range = -1;
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("dimension", dimension.toString());
		json.addProperty("x", x);
		json.addProperty("y", y);
		json.addProperty("z", z);
		json.addProperty("range", range);
		return json;
	}
}