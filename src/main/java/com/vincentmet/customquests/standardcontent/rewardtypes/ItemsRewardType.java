package com.vincentmet.customquests.standardcontent.rewardtypes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.IItemStacksProvider;
import com.vincentmet.customquests.api.IRewardType;
import com.vincentmet.customquests.helpers.MouseButton;
import com.vincentmet.customquests.helpers.TagHelper;
import com.vincentmet.customquests.integrations.jei.JEIHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemsRewardType implements IRewardType, IItemStacksProvider{
	private static ResourceLocation ID = new ResourceLocation(Ref.MODID, "items");
	private ItemStack stack = new ItemStack(Blocks.AIR);
	
	private ResourceLocation ogRL;
	private int count;
	private String ogNBT;
	
	private int questId;
	private int rewardId;
	
	@Override
	public ResourceLocation getId(){
		return ID;
	}
	
	@Override
	public void executeReward(ServerPlayer player){
		ItemHandlerHelper.giveItemToPlayer(player, stack);
	}
	
	@Override
	public Item getIcon(){
		return stack.getItem();
	}
	
	@Override
	public Runnable onSlotHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
		return ()->Minecraft.getInstance().screen.renderTooltip(matrixStack, stack, mouseX, mouseY);
	}
	
	@Override
	public String getText(){
		return stack.getCount() + "x " + stack.getItem().getDescription().getString();
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
		return stack.getCount() + "x " + stack.getItem().getDescription().getString();
	}
	
	@Override
	public void processJson(JsonObject json){
		if(json.has("parent_quest_id")){
			JsonElement jsonElement = json.get("parent_quest_id");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					questId = jsonPrimitive.getAsInt();
				}
			}
		}
		if(json.has("parent_reward_id")){
			JsonElement jsonElement = json.get("parent_reward_id");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					rewardId = jsonPrimitive.getAsInt();
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
						if(!TagHelper.Items.doesTagExist(ogRL) && !ForgeRegistries.ITEMS.containsKey(ogRL)){
							Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > rewards > entries > " + rewardId + " > content > item': Value is not a valid item that exists in the game, please use a valid item, defaulting to 'minecraft:grass_block'!");
							ogRL = Blocks.GRASS_BLOCK.getRegistryName();
						}
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > rewards > entries > " + rewardId + " > content > item': Value is not a valid item ResourceLocation, defaulting to 'minecraft:grass_block'!");
						ogRL = Blocks.GRASS_BLOCK.getRegistryName();
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > rewards > entries > " + rewardId + " > content > item': Value is not a String, defaulting to 'minecraft:grass_block'!");
					ogRL = Blocks.GRASS_BLOCK.getRegistryName();
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > rewards > entries > " + rewardId + " > content > item': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:grass_block'!");
				ogRL = Blocks.GRASS_BLOCK.getRegistryName();
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > rewards > entries > " + rewardId + " > content > item': Not detected, defaulting to 'minecraft:grass_block'!");
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
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > rewards > entries > " + rewardId + " > content > count': Value is not >= 1, defaulting to '1'!");
						count = 1;
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > rewards > entries > " + rewardId + " > content > count': Value is not an Integer, defaulting to '1'!");
					count = 1;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > rewards > entries > " + rewardId + " > content > count': Value is not a JsonPrimitive, please use a Double, defaulting to '1'!");
				count = 1;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > rewards > entries > " + rewardId + " > content > count': Not detected, defaulting to '1'!");
			count = 1;
		}
		
		if(json.has("nbt")){
			JsonElement jsonElement = json.get("nbt");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					ogNBT = jsonPrimitive.getAsString();
					if(ogNBT.equals("")){
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > rewards > entries > " + rewardId + " > content > nbt': Value is not valid NBT, defaulting to null!");
						ogNBT = null;
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > rewards > entries > " + rewardId + " > content > nbt': Value is not a String, defaulting to null!");
					ogNBT = null;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > rewards > entries > " + rewardId + " > content > nbt': Value is not a JsonPrimitive, please use a String, defaulting to null!");
				ogNBT = null;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > rewards > entries > " + rewardId + " > content > nbt': Not detected, defaulting to null!");
			ogNBT = null;
		}
		stack = new ItemStack(ForgeRegistries.ITEMS.getValue(ogRL), count);
		CompoundTag tag = ApiUtils.getNbtFromJson(ogNBT);
		if(tag != null && !tag.isEmpty())stack.setTag(tag);
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("item", ogRL.toString());
		json.addProperty("count", count);
		json.addProperty("nbt", ogNBT);
		return json;
	}
	
	public List<ItemStack> getItemStacks(){
		ArrayList<ItemStack> list = new ArrayList<>();
		list.add(stack);
		return list;
	}
}