package com.vincentmet.customquests.standardcontent.tasktypes;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.api.logic.*;
import com.vincentmet.customquests.helpers.*;
import com.vincentmet.customquests.hierarchy.quest.*;
import com.vincentmet.customquests.integrations.jei.JEIHelper;
import com.vincentmet.customquests.standardcontent.StandardContentProgressHelper;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

public class ItemDetectTaskType implements ITaskType, IItemStacksProvider{
	private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "item_detect");
	private static final ITextComponent TRANSLATION = new TranslationTextComponent(Ref.MODID + ".standardcontent.tasks.item_detect");
	private UUID uuid;
	private int questId;
	private int taskId;
	private int subtaskId;
	
	private ResourceLocation ogRL;
	private int count;
	private String ogNBT;
	
	private int parentQuestId;
	private int parentTaskId;
	private int parentSubtaskId;
	
	ItemSlideshowTexture icon;
	private List<ItemStack> items = new ArrayList<>();
	
	@Override
	public ResourceLocation getId(){
		return ID;
	}
    
    @Override
    public ITextComponent getTranslation(){
        return TRANSLATION;
    }
    
    @Override
	public boolean hasButton(ButtonContext context){
		return false;
	}
	
	@Override
	public void executeSubtaskCheck(PlayerEntity player, Object object){
		IntCounter correctItemInInvCount = new IntCounter();
		player.inventory.mainInventory.forEach(invStack -> {
			items.forEach(itemStack -> {
				if(invStack.getItem() == itemStack.getItem()){
					correctItemInInvCount.add(invStack.getCount());
				}
			});
			
		});
		processValue(correctItemInInvCount, player);
	}
	
	public void processValue(IntCounter correctItemInInvCount, PlayerEntity player){
		if(!CombinedProgressHelper.isQuestCompleted(player.getUniqueID(), questId)){
			int oldValue = CombinedProgressHelper.getValue(player.getUniqueID(), questId, taskId, subtaskId);
			int newValue = correctItemInInvCount.getValue();
			if(newValue != oldValue){
				CombinedProgressHelper.setValue(player.getUniqueID(), questId, taskId, subtaskId, correctItemInInvCount.getValue());
				ServerUtils.sendProgressAndParties((ServerPlayerEntity)player);
			}
		}
		if(correctItemInInvCount.getValue() >= count){
			CombinedProgressHelper.completeSubtask(player.getUniqueID(), questId, taskId, subtaskId);
		}
	}
	
	@Override
	public ITaskButtonClick executeSubtaskButton(){
		return (player, questId, taskId, subtaskId) -> {/*NOP*/};
	}
	
	@Override
	public void onLoad(UUID uuid, int questId, int taskId, int subtaskId){
		this.uuid = uuid;
		this.questId = questId;
		this.taskId = taskId;
		this.subtaskId = subtaskId;
		StandardContentProgressHelper.ITEM_DETECT_SUBTASKS_TO_CHECK.add(new Quadruple<>(uuid, questId, taskId, subtaskId));
	}
	
	@Override
	public IQuestingTexture getIcon(){
		return icon;
	}
	
	@Override
	public String getText(){
		if(items.size()==1){
			return count + "x " + items.get(0).getItem().getName().getString();
		}else{
			return count + "x " + new TranslationTextComponent(Ref.MODID + ".general.tag").getString() + ": " + Arrays.stream(ogRL.getPath().split("/")).map(StringUtils::capitalize).reduce((s, s2) ->s + "/" + s2).orElse("Empty Tag");
		}
	}
	
	@Override
	public int getCompletionAmount(){
		return count;
	}
	
	@Override
	public Consumer<MouseButton> onSlotClick(){
		return (mouseButton)->{
			if(mouseButton == MouseButton.LEFT){
				if(JEIHelper.hasRecipe(icon.getCurrentItemStack()))JEIHelper.openRecipe(icon.getCurrentItemStack());
			}else if(mouseButton == MouseButton.RIGHT){
				if(JEIHelper.hasUse(icon.getCurrentItemStack()))JEIHelper.openUses(icon.getCurrentItemStack());
			}
		};
	}
	
	@Override
	public void processJson(JsonObject json){
		if(json.has("parent_quest_id")){
			JsonElement jsonElement = json.get("parent_quest_id");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					parentQuestId = jsonPrimitive.getAsInt();
				}
			}
		}
		if(json.has("parent_task_id")){
			JsonElement jsonElement = json.get("parent_task_id");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					parentTaskId = jsonPrimitive.getAsInt();
				}
			}
		}
		if(json.has("parent_subtask_id")){
			JsonElement jsonElement = json.get("parent_subtask_id");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					parentSubtaskId = jsonPrimitive.getAsInt();//todo might be able to remove onload quest/task/subtask
				}
			}
		}
		
		if(json.has("item")){
			JsonElement jsonElement = json.get("item");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
					ogRL = ResourceLocation.tryCreate(jsonPrimitiveStringValue);
					if(ogRL == null){
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > tasks > entries > " + parentTaskId + " > sub_tasks > entries > " + parentSubtaskId + " > item': Value is not a valid item, please use a valid item id, defaulting to 'minecraft:grass_block'!");
						ogRL = Blocks.GRASS_BLOCK.getRegistryName();
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > tasks > entries > " + parentTaskId + " > sub_tasks > entries > " + parentSubtaskId + " > item': Value is not a String, defaulting to 'minecraft:grass_block'!");
					ogRL = Blocks.GRASS_BLOCK.getRegistryName();
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > tasks > entries > " + parentTaskId + " > sub_tasks > entries > " + parentSubtaskId + " > item': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:grass_block'!");
				ogRL = Blocks.GRASS_BLOCK.getRegistryName();
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > tasks > entries > " + parentTaskId + " > sub_tasks > entries > " + parentSubtaskId + " > item': Not detected, defaulting to 'minecraft:grass_block'!");
			ogRL = Blocks.GRASS_BLOCK.getRegistryName();
		}
		
		if(json.has("count")){
			JsonElement jsonElement = json.get("count");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					int jsonPrimitiveIntValue = jsonPrimitive.getAsInt();
					if(jsonPrimitiveIntValue >= 1){
						count = jsonPrimitiveIntValue;
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > tasks > entries > " + parentTaskId + " > sub_tasks > entries > " + parentSubtaskId + " > count': Value is not >= 1, defaulting to '1'!");
						count = 1;
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > tasks > entries > " + parentTaskId + " > sub_tasks > entries > " + parentSubtaskId + " > count': Value is not an Integer, defaulting to '1'!");
					count = 1;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > tasks > entries > " + parentTaskId + " > sub_tasks > entries > " + parentSubtaskId + " > count': Value is not a JsonPrimitive, please use a Double, defaulting to '1'!");
				count = 1;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > tasks > entries > " + parentTaskId + " > sub_tasks > entries > " + parentSubtaskId + " > count': Not detected, defaulting to '1'!");
			count = 1;
		}
		
		if(json.has("nbt")){
			JsonElement jsonElement = json.get("nbt");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					ogNBT = jsonPrimitive.getAsString();
					if(ogNBT == null || ogNBT.equals("")){
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > tasks > entries > " + parentTaskId + " > sub_tasks > entries > " + parentSubtaskId + " > nbt': Value is not valid NBT, defaulting to '{ }'!");
						ogNBT = "{}";
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > tasks > entries > " + parentTaskId + " > sub_tasks > entries > " + parentSubtaskId + " > nbt': Value is not a String, defaulting to '{ }'!");
					ogNBT = "{}";
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > tasks > entries > " + parentTaskId + " > sub_tasks > entries > " + parentSubtaskId + " > nbt': Value is not a JsonPrimitive, please use a String, defaulting to '{ }'!");
				ogNBT = "{}";
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > tasks > entries > " + parentTaskId + " > sub_tasks > entries > " + parentSubtaskId + " > nbt': Not detected, defaulting to '{ }'!");
			ogNBT = "{}";
		}
		
		CompoundNBT nbt = ApiUtils.getNbtFromJson(ogNBT);
		if(TagHelper.doesTagExist(ogRL)){
			TagHelper.getEntries(ogRL).stream().map(item1 -> new ItemStack(item1, count, nbt)).forEach(items::add);
			icon = new ItemSlideshowTexture(ogRL, items);
		}else{
			ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(ogRL), count, nbt);
			items.add(stack);
			icon = new ItemSlideshowTexture(ogRL, stack);
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("item", ogRL.toString());
		json.addProperty("count", count);
		json.addProperty("nbt", ogNBT);
		return json;
	}
	
	@Override
	public String toString(){
		return "ITaskType{" + "uuid=" + uuid + ", questId=" + questId + ", taskId=" + taskId + ", subtaskId=" + subtaskId + ", ogRL=" + ogRL + ", ogNBT='" + ogNBT + '\'' + ", count=" + count + ", icon=" + icon + '}';
	}
	
	@Override
	public List<ItemStack> getItemStacks(){
		return items;
	}
}