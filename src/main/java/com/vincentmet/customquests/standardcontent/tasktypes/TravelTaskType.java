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
import net.minecraft.world.dimension.DimensionType;

public class TravelTaskType implements ITaskType{
	private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "travel");
	private static final ITextComponent TRANSLATION_TASK_TITLE = new TranslationTextComponent(Ref.MODID + ".standardcontent.tasks.travel");
	private static final ITextComponent TRANSLATION_DIMENSION = new TranslationTextComponent(Ref.MODID + ".general.dimension");
	private static final ITextComponent TRANSLATION_POSITION = new TranslationTextComponent(Ref.MODID + ".general.position");
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
    public ITextComponent getTranslation(){
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
	public void executeSubtaskCheck(PlayerEntity player, Object object){
		if(!CombinedProgressHelper.isQuestCompleted(player.getUniqueID(), questId)){
			if(range <= -1){
				if(WorldHelper.isPlayerInDimension(player, dimension)){
					CombinedProgressHelper.addValue(player.getUniqueID(), questId, taskId, subtaskId, getCompletionAmount());
					ServerUtils.sendProgressAndParties((ServerPlayerEntity)player);
				}
			}else{
				if(WorldHelper.isPlayerInDimension(player, dimension) && WorldHelper.isPlayerInRange(player, x, y, z, range)){
					CombinedProgressHelper.addValue(player.getUniqueID(), questId, taskId, subtaskId, getCompletionAmount());
					ServerUtils.sendProgressAndParties((ServerPlayerEntity)player);
				}
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
					dimension = ResourceLocation.tryCreate(jsonPrimitiveStringValue);
					if(dimension == null){
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > dimension': Value is not a valid dimension, please use a valid dimension id, defaulting to 'minecraft:overworld'!");
						dimension = DimensionType.OVERWORLD.getRegistryName();
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > dimension': Value is not a String, defaulting to 'minecraft:overworld'!");
					dimension = DimensionType.OVERWORLD.getRegistryName();
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > dimension': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:overworld'!");
				dimension = DimensionType.OVERWORLD.getRegistryName();
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > dimension': Not detected, defaulting to 'minecraft:overworld'!");
			dimension = DimensionType.OVERWORLD.getRegistryName();
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