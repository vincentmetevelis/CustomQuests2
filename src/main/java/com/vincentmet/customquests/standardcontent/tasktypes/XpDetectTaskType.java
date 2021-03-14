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
import net.minecraftforge.registries.ForgeRegistries;

public class XpDetectTaskType implements ITaskType{
	private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "xp_detect");
	private static final ITextComponent TRANSLATION = new TranslationTextComponent(Ref.MODID + ".standardcontent.tasks.xp_detect");
	public static final List<PlayerBoundSubtaskReference> TRACKING_LIST = new ArrayList<>();
	private int questId;
	private int taskId;
	private int subtaskId;
	
	private int amount = 1;
	private boolean inLevels;
	
	ItemSlideshowTexture icon = new ItemSlideshowTexture(Items.EXPERIENCE_BOTTLE.getRegistryName(), new ItemStack(Items.EXPERIENCE_BOTTLE));
	
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
			processValue(player.experienceTotal, player);
		}
	}
	
	public void processValue(int detectedXp, PlayerEntity player){
		if(!CombinedProgressHelper.isQuestCompleted(player.getUniqueID(), questId)){
			int oldValue = CombinedProgressHelper.getValue(player.getUniqueID(), questId, taskId, subtaskId);
			if(detectedXp != oldValue){
				CombinedProgressHelper.setValue(player.getUniqueID(), questId, taskId, subtaskId, detectedXp);
				ServerUtils.sendProgressAndParties((ServerPlayerEntity)player);
			}
		}
		if(detectedXp >= amount){
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
		return ()->{};
	}
	
	@Override
	public String getText(ClientPlayerEntity player){
		return amount + " " + new TranslationTextComponent(Ref.MODID + ".general.experience").getString() + (inLevels?" "+new TranslationTextComponent(Ref.MODID + ".general.levels").getString():" " + new TranslationTextComponent(Ref.MODID + ".general.points").getString());
	}
	
	@Override
	public int getCompletionAmount(){
		return amount;
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
		
		if(json.has("in_levels")){
			JsonElement jsonElement = json.get("in_levels");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isBoolean()){
					inLevels = jsonPrimitive.getAsBoolean();
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > in_levels': Value is not a Boolean, defaulting to 'false'!");
					inLevels = false;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > in_levels': Value is not a JsonPrimitive, please use a boolean, defaulting to 'false'!");
				inLevels = false;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > in_levels': Not detected, defaulting to 'false'");
			inLevels = false;
		}
		
		if(json.has("amount")){
			JsonElement jsonElement = json.get("amount");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					int jsonPrimitiveIntValue = jsonPrimitive.getAsInt();
					if(jsonPrimitiveIntValue >= 1){
						amount = jsonPrimitiveIntValue;
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > amount': Value is not >= 1, defaulting to '1'!");
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > amount': Value is not an Integer, defaulting to '1'!");
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > amount': Value is not a JsonPrimitive, please use a Double, defaulting to '1'!");
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > amount': Not detected, defaulting to '1'!");
		}
		
		if(json.has("icon")){
			JsonElement jsonElement = json.get("icon");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
					ResourceLocation rl = ResourceLocation.tryCreate(jsonPrimitiveStringValue);
					if(rl != null){
						icon = new ItemSlideshowTexture(rl, new ItemStack(ForgeRegistries.ITEMS.getValue(rl)));
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > icon': Value is not a valid item, please use a valid item id, defaulting to 'minecraft:diamond_sword'!");
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > icon': Value is not a String, defaulting to 'minecraft:diamond_sword'!");
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > icon': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:diamond_sword'!");
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > icon': Not detected, defaulting to 'minecraft:diamond_sword'!");
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("in_levels", inLevels);
		json.addProperty("amount", amount);
		json.addProperty("icon", icon.getResourceLocation().toString());
		return json;
	}
}