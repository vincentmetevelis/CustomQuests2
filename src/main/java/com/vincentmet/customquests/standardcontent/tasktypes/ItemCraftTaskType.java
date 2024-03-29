package com.vincentmet.customquests.standardcontent.tasktypes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.BooleanContainer;
import com.vincentmet.customquests.helpers.MouseButton;
import com.vincentmet.customquests.helpers.PlayerBoundSubtaskReference;
import com.vincentmet.customquests.helpers.TagHelper;
import com.vincentmet.customquests.hierarchy.quest.ItemSlideshowTexture;
import com.vincentmet.customquests.integrations.jei.JEIHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ItemCraftTaskType implements ITaskType, IItemStacksProvider{
	private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "item_craft");
	private static final Component TRANSLATION = new TranslatableComponent(Ref.MODID + ".standardcontent.tasks.item_craft");
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
			PlayerEvent.ItemCraftedEvent event = (PlayerEvent.ItemCraftedEvent)object;
			items.stream()
				 .filter(itemStack->event.getCrafting().getItem().equals(itemStack.getItem()))
				 .filter(itemStack -> {
					 BooleanContainer invalid = new BooleanContainer(false);
					 if(event.getCrafting().hasTag() && itemStack.hasTag()){
						 itemStack.getTag().getAllKeys().forEach(key -> {
							 invalid.set(!event.getCrafting().getTag().contains(key));
						 });
					 }
					 return !invalid.get();
				 })
				 .forEach(itemStack -> {
					 CombinedProgressHelper.addValue(player.getUUID(), questId, taskId, subtaskId, event.getCrafting().getCount());
					 ServerUtils.sendProgressAndParties((ServerPlayer) player);
				 });
			processValue(player);
		}
	}
	
	public void processValue(Player player){
		if(CombinedProgressHelper.getValue(player.getUUID(), questId, taskId, subtaskId) >= count){
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
		return ()->Minecraft.getInstance().screen.renderTooltip(matrixStack, icon.getCurrentItemStack(), mouseX, mouseY);
	}
	
	@Override
	public String getText(LocalPlayer player){
		if(items.size()==1){
			return count + "x " + items.get(0).getItem().getDescription().getString();
		}else{
			return count + "x " + new TranslatableComponent(Ref.MODID + ".general.tag").getString() + ": " + Arrays.stream(ogRL.getPath().split("/")).map(StringUtils::capitalize).reduce((s, s2) ->s + "/" + s2).orElse(new TranslatableComponent(Ref.MODID + ".general.tag.empty").getString());
		}
	}
	
	@Override
	public int getCompletionAmount(){
		return count;
	}
    
    @Override
    public Consumer<MouseButton> onSlotClick(LocalPlayer player){
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
					ogRL = ResourceLocation.tryParse(jsonPrimitiveStringValue);
					if(ogRL != null){
						if(!TagHelper.doesTagExist(ogRL) && !ForgeRegistries.ITEMS.containsKey(ogRL)){
							Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > item': Value is not a valid item that exists in the game, please use a valid item, defaulting to 'minecraft:grass_block'!");
							ogRL = Blocks.GRASS_BLOCK.getRegistryName();
						}
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > item': Value is not a valid item ResourceLocation, defaulting to 'minecraft:grass_block'!");
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
		
		CompoundTag nbt = ApiUtils.getNbtFromJson(ogNBT);
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
			ItemStack stack = new ItemStack(ForgeRegistries.ITEMS.getValue(ogRL), count);
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
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("item", ogRL.toString());
		json.addProperty("count", items.get(0).getCount());
		json.addProperty("nbt", ogNBT);
		return json;
	}
	
	@Override
	public List<ItemStack> getItemStacks(){
		return items;
	}
}