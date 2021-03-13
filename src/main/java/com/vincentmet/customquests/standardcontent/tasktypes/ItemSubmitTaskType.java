package com.vincentmet.customquests.standardcontent.tasktypes;

import com.google.gson.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.*;
import com.vincentmet.customquests.hierarchy.quest.ItemSlideshowTexture;
import com.vincentmet.customquests.integrations.jei.JEIHelper;
import com.vincentmet.customquests.network.messages.*;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

public class ItemSubmitTaskType implements ITaskType, IItemStacksProvider{
	private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "item_submit");
	private static final ITextComponent TRANSLATION = new TranslationTextComponent(Ref.MODID + ".standardcontent.tasks.item_submit");
	public static final List<PlayerBoundSubtaskReference> TRACKING_LIST = new ArrayList<>();
	
	private int questId;
	private int taskId;
	private int subtaskId;
	
	private ResourceLocation ogRL;
	private int count;
	private String ogNBT;
	
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
	public List<PlayerBoundSubtaskReference> getCurrentlyTrackingList(){
		return TRACKING_LIST;
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
	public int getCompletionAmount(){
		return count;
	}
	
	@Override
	public Consumer<MouseButton> onSlotClick(ClientPlayerEntity player){
		return (mouseButton)->{
			if(!icon.getResourceLocation().equals(new ResourceLocation("air"))){
				if(mouseButton == MouseButton.LEFT){
					if(JEIHelper.hasRecipe(icon.getCurrentItemStack()))JEIHelper.openRecipe(icon.getCurrentItemStack());
				}else if(mouseButton == MouseButton.RIGHT){
					if(JEIHelper.hasUse(icon.getCurrentItemStack()))JEIHelper.openUses(icon.getCurrentItemStack());
				}
			}
		};
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
	
		if(json.has("item")){
			JsonElement jsonElement = json.get("item");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
					ogRL = ResourceLocation.tryCreate(jsonPrimitiveStringValue);
					if(ogRL == null){
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > item': Value is not a valid item, please use a valid item id, defaulting to 'minecraft:grass_block'!");
						ogRL = Blocks.GRASS_BLOCK.getRegistryName();
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > item': Value is not a String, defaulting to 'minecraft:grass_block'!");
					ogRL = Blocks.GRASS_BLOCK.getRegistryName();
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > item': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:grass_block'!");
				ogRL = Blocks.GRASS_BLOCK.getRegistryName();
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > item': Not detected, defaulting to 'minecraft:grass_block'!");
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
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > count': Value is not >= 1, defaulting to '1'!");
						count = 1;
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > count': Value is not an Integer, defaulting to '1'!");
					count = 1;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > count': Value is not a JsonPrimitive, please use a Double, defaulting to '1'!");
				count = 1;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > count': Not detected, defaulting to '1'!");
			count = 1;
		}
	
		if(json.has("nbt")){
			JsonElement jsonElement = json.get("nbt");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					ogNBT = jsonPrimitive.getAsString();
					if(ogNBT == null || ogNBT.equals("")){
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > nbt': Value is not valid NBT, defaulting to '{ }'!");
						ogNBT = "{}";
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > nbt': Value is not a String, defaulting to '{ }'!");
					ogNBT = "{}";
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > nbt': Value is not a JsonPrimitive, please use a String, defaulting to '{ }'!");
				ogNBT = "{}";
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > nbt': Not detected, defaulting to '{ }'!");
			ogNBT = "{}";
		}
	
		CompoundNBT nbt = ApiUtils.getNbtFromJson(ogNBT);
		if(TagHelper.doesTagExist(ogRL)){
			TagHelper.getEntries(ogRL).stream().map(item1 ->{
				ItemStack stack = new ItemStack(item1, count);
				if(stack.getTag() != null){
					stack.getTag().merge(nbt);
				}else{
					stack.setTag(nbt);
				}
				return stack;
			}).forEach(items::add);
			icon = new ItemSlideshowTexture(ogRL, items);
		}else{
			ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(ogRL), count, nbt);
			if(stack.getTag() != null){
				stack.getTag().merge(nbt);
			}else{
				stack.setTag(nbt);
			}
			items.add(stack);
			icon = new ItemSlideshowTexture(ogRL, stack);
		}
	}
    
    @Override
	public boolean hasButton(ButtonContext context){
		context.setText(new TranslationTextComponent(Ref.MODID  + ".standardcontent.tasks.button.item_submit"));
		context.setOnClick((mouseButton, uuid, questId, taskId) -> {
			if(!CombinedProgressHelper.isTaskCompleted(uuid, questId, taskId)){
				PacketHandler.CHANNEL.sendToServer(new MessageTaskButton(questId, taskId));
			}
		});
		return true;
	}
	
	public int getItemCountLeftToHandIn(UUID uuid, int questId, int taskId, int subtaskId){
		return count - CombinedProgressHelper.getValue(uuid, questId, taskId, subtaskId);
	}
	
	@Override
	public void executeSubtaskCheck(PlayerEntity player, Object object){
		if(!CombinedProgressHelper.isQuestCompleted(player.getUniqueID(), questId)){
			IntCounter itemsLeftToHandIn = new IntCounter(getItemCountLeftToHandIn(player.getUniqueID(), questId, taskId, subtaskId));
			player.inventory.mainInventory.forEach(invStack -> {
				items.stream()
					 .filter(itemStack -> itemStack.getItem() == invStack.getItem())
					 .filter(itemStack -> {
						 BooleanContainer invalid = new BooleanContainer(false);
						 if(invStack.hasTag() && itemStack.hasTag()){
							 itemStack.getTag().keySet().forEach(key -> {
								 invalid.set(!invStack.getTag().contains(key));
							 });
						 }
						 return !invalid.get();
					 })
					 .forEach(itemStack -> {
						 if(itemsLeftToHandIn.getValue() >= invStack.getCount()){
							 CombinedProgressHelper.addValue(player.getUniqueID(), questId, taskId, subtaskId, invStack.getCount());
							 itemsLeftToHandIn.add(-invStack.getCount());
							 invStack.setCount(0);
						 }else{
							 CombinedProgressHelper.addValue(player.getUniqueID(), questId, taskId, subtaskId, itemsLeftToHandIn.getValue());
							 invStack.setCount(invStack.getCount() - itemsLeftToHandIn.getValue());
							 itemsLeftToHandIn.setValue(0);
						 }
						 processValue(player);
						 ServerUtils.sendProgressAndParties((ServerPlayerEntity)player);
					 });
			});
		}
	}
	
	public void processValue(PlayerEntity player){
		if(CombinedProgressHelper.getValue(player.getUniqueID(), questId, taskId, subtaskId) >= count){
			CombinedProgressHelper.completeSubtask(player.getUniqueID(), questId, taskId, subtaskId);
		}
	}
	
	@Override
	public void executeSubtaskButton(PlayerEntity player){
		executeSubtaskCheck(player, null);
	}
	
	@Override
	public IQuestingTexture getIcon(ClientPlayerEntity player){
		return icon;
	}
	
	@Override
	public Runnable onSlotHover(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, ClientPlayerEntity player){
		return ()->Minecraft.getInstance().currentScreen.renderTooltip(matrixStack, icon.getCurrentItemStack(), mouseX, mouseY);
	}
	
	@Override
	public String getText(ClientPlayerEntity player){
		if(items.size()==1){
			return count + "x " + items.get(0).getItem().getName().getString();
		}else{
			return count + "x " + new TranslationTextComponent(Ref.MODID + ".general.tag").getString() + ": " + Arrays.stream(ogRL.getPath().split("/")).map(StringUtils::capitalize).reduce((s, s2) ->s + "/" + s2).orElse(new TranslationTextComponent(Ref.MODID + ".general.tag.empty").getString());
		}
	}
	
	@Override
	public List<ItemStack> getItemStacks(){
		return items;
	}
}
