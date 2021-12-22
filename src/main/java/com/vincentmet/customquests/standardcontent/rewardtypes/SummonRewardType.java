package com.vincentmet.customquests.standardcontent.rewardtypes;

import com.google.gson.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.IRewardType;
import com.vincentmet.customquests.helpers.MouseButton;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class SummonRewardType implements IRewardType{
	private static ResourceLocation ID = new ResourceLocation(Ref.MODID, "summon");
	private EntityType entity;
	private int count;
	private Item icon = Items.DIAMOND_SWORD;
	
	private int parentQuestId;
	private int parentRewardId;
    
    @Override
    public ResourceLocation getId(){
        return ID;
    }
    
    @Override
	public void executeReward(Player player){
		for(int i=0; i<count;i++){
			(player.getCommandSenderWorld()).addFreshEntity(Objects.requireNonNull(entity.create((ServerLevel)player.getCommandSenderWorld(), new CompoundTag(), new TranslatableComponent("Your Reward <3"), player, player.blockPosition(), MobSpawnType.COMMAND, true, false)));
		}
	}
	
	@Override
	public Item getIcon(){
		return icon;
	}
	
	@Override
	public Runnable onSlotHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
		return ()->{/*NOOP*/};
	}
	
	@Override
	public String getText(){
		return count + "x " + entity.getDescription().getString();
	}
	
	@Override
	public Consumer<MouseButton> onSlotClick(){
		return (mouseButton)->{};
	}
	
	@Override
	public String toString(){
		return count + "x " + entity.getDescription().getString();
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
		
		if(json.has("icon")){
			JsonElement jsonElement = json.get("icon");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
					ResourceLocation rl = ResourceLocation.tryParse(jsonPrimitiveStringValue);
					if(rl != null){
						icon = ForgeRegistries.ITEMS.getValue(rl);
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > icon': Value is not a valid item, please use a valid item id, defaulting to 'minecraft:zombie_spawn_egg'!");
						icon = Items.ZOMBIE_SPAWN_EGG;
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > icon': Value is not a String, defaulting to 'minecraft:zombie_spawn_egg'!");
					icon = Items.ZOMBIE_SPAWN_EGG;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > icon': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:zombie_spawn_egg'!");
				icon = Items.ZOMBIE_SPAWN_EGG;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > icon': Not detected, defaulting to 'minecraft:zombie_spawn_egg'!");
			icon = Items.ZOMBIE_SPAWN_EGG;
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
		
		if(json.has("entity")){
			JsonElement jsonElement = json.get("entity");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
					ResourceLocation rl = ResourceLocation.tryParse(jsonPrimitiveStringValue);
					if(rl != null){
						entity = ForgeRegistries.ENTITIES.getValue(rl);
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > entity': Value is not a valid item, please use a valid item id, defaulting to 'minecraft:zombie_spawn_egg'!");
						entity = EntityType.SHEEP;
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > entity': Value is not a String, defaulting to 'minecraft:zombie_spawn_egg'!");
					entity = EntityType.SHEEP;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > entity': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:zombie_spawn_egg'!");
				entity = EntityType.SHEEP;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > entity': Not detected, defaulting to 'minecraft:zombie_spawn_egg'!");
			entity = EntityType.SHEEP;
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("entity", entity.getRegistryName().toString());
		json.addProperty("count", count);
		json.addProperty("icon", icon.getRegistryName().toString());
		return json;
	}
}