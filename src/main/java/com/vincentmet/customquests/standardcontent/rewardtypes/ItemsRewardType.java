package com.vincentmet.customquests.standardcontent.rewardtypes;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.logic.IRewardType;
import com.vincentmet.customquests.helpers.MouseButton;
import com.vincentmet.customquests.integrations.jei.JEIHelper;
import java.util.function.Consumer;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemsRewardType implements IRewardType{
	private static ResourceLocation ID = new ResourceLocation(Ref.MODID, "items");
	private ItemStack stack = new ItemStack(Blocks.AIR);
	
	private ResourceLocation ogRL;
	private int count;
	private String ogNBT;
	
	private int parentQuestId;
	private int parentRewardId;
	
	@Override
	public ResourceLocation getId(){
		return ID;
	}
	
	@Override
	public void executeReward(PlayerEntity player){
		ItemHandlerHelper.giveItemToPlayer(player, stack);
	}
	
	@Override
	public Item getIcon(){
		return stack.getItem();
	}
	
	@Override
	public String getText(){
		return stack.getCount() + "x " + stack.getItem().getName().getString();
	}
	
	@Override
	public Consumer<MouseButton> onSlotClick(){
		return (mouseButton)->{
			if(mouseButton == MouseButton.LEFT){
				JEIHelper.openRecipe(stack);
			}else if(mouseButton == MouseButton.RIGHT){
				JEIHelper.openUses(stack);
			}
		};
	}
	
	@Override
	public String toString(){
		return stack.getCount() + "x " + stack.getItem().getName().getString();
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
		if(json.has("parent_reward_id")){
			JsonElement jsonElement = json.get("parent_reward_id");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					parentRewardId = jsonPrimitive.getAsInt();
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
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > item': Value is not a valid item, please use a valid item id, defaulting to 'minecraft:grass_block'!");
						ogRL = Blocks.GRASS_BLOCK.getRegistryName();
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > item': Value is not a String, defaulting to 'minecraft:grass_block'!");
					ogRL = Blocks.GRASS_BLOCK.getRegistryName();
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > item': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:grass_block'!");
				ogRL = Blocks.GRASS_BLOCK.getRegistryName();
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > item': Not detected, defaulting to 'minecraft:grass_block'!");
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
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > count': Value is not >= 1, defaulting to '1'!");
						count = 1;
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > count': Value is not an Integer, defaulting to '1'!");
					count = 1;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > count': Value is not a JsonPrimitive, please use a Double, defaulting to '1'!");
				count = 1;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > count': Not detected, defaulting to '1'!");
			count = 1;
		}
		
		if(json.has("nbt")){
			JsonElement jsonElement = json.get("nbt");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					ogNBT = jsonPrimitive.getAsString();
					if(ogNBT == null || ogNBT.equals("")){
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > nbt': Value is not valid NBT, defaulting to '{ }'!");
						ogNBT = "{}";
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > nbt': Value is not a String, defaulting to '{ }'!");
					ogNBT = "{}";
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > nbt': Value is not a JsonPrimitive, please use a String, defaulting to '{ }'!");
				ogNBT = "{}";
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > nbt': Not detected, defaulting to '{ }'!");
			ogNBT = "{}";
		}
		stack = new ItemStack(ForgeRegistries.ITEMS.getValue(ogRL), count, ApiUtils.getNbtFromJson(ogNBT));
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("item", ogRL.toString());
		json.addProperty("count", count);
		json.addProperty("nbt", ogNBT);
		return json;
	}
	
	public ItemStack getStack(){
		return stack;
	}
}