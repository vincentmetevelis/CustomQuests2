package com.vincentmet.customquests.standardcontent.tasktypes;

import com.google.gson.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.*;
import com.vincentmet.customquests.hierarchy.quest.ItemSlideshowTexture;
import com.vincentmet.customquests.integrations.jei.JEIHelper;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

public class BlockPlacedTaskType implements ITaskType, IItemStacksProvider{
	private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "block_placed");
	private static final ITextComponent TRANSLATION = new TranslationTextComponent(Ref.MODID + ".standardcontent.tasks.block_placed");
	public static final List<PlayerBoundSubtaskReference> TRACKING_LIST = new ArrayList<>();
	private int questId;
	private int taskId;
	private int subtaskId;
	
	private ResourceLocation ogRL;
	private int count;
	
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
	public boolean hasButton(ButtonContext context){
		return false;
	}
	
	@Override
	public void executeSubtaskCheck(PlayerEntity player, Object object){
		if(!CombinedProgressHelper.isQuestCompleted(player.getUniqueID(), questId)){
			BlockEvent.EntityPlaceEvent event = (BlockEvent.EntityPlaceEvent)object;
			items.stream()
				 .filter(itemStack->event.getState().getBlock().asItem().getRegistryName().equals(itemStack.getItem().getRegistryName()))
				 .forEach(itemStack -> {
					 CombinedProgressHelper.addValue(player.getUniqueID(), questId, taskId, subtaskId, 1);
					 ServerUtils.sendProgressAndParties((ServerPlayerEntity)player);
				 })
			;
			processValue(player);
		}
	}
	
	public void processValue(PlayerEntity player){
		if(CombinedProgressHelper.getValue(player.getUniqueID(), questId, taskId, subtaskId) >= count){
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
		
		if(json.has("block")){
			JsonElement jsonElement = json.get("block");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
					ogRL = ResourceLocation.tryCreate(jsonPrimitiveStringValue);
					if(ogRL == null){
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > block': Value is not a valid block, please use a valid item id, defaulting to 'minecraft:grass_block'!");
						ogRL = Blocks.GRASS_BLOCK.getRegistryName();
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > block': Value is not a String, defaulting to 'minecraft:grass_block'!");
					ogRL = Blocks.GRASS_BLOCK.getRegistryName();
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > block': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:grass_block'!");
				ogRL = Blocks.GRASS_BLOCK.getRegistryName();
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > block': Not detected, defaulting to 'minecraft:grass_block'!");
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
		
		if(TagHelper.doesTagExist(ogRL)){
			TagHelper.getEntries(ogRL).stream().map(item1 ->new ItemStack(item1, count)).forEach(items::add);
			icon = new ItemSlideshowTexture(ogRL, items);
		}else{
			ItemStack stack = new ItemStack(ForgeRegistries.BLOCKS.getValue(ogRL), count);
			items.add(stack);
			icon = new ItemSlideshowTexture(ogRL, stack);
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("block", ogRL.toString());
		json.addProperty("count", items.get(0).getCount());
		return json;
	}
	
	@Override
	public List<ItemStack> getItemStacks(){
		return items;
	}
}