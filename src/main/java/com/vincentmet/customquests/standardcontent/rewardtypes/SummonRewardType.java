package com.vincentmet.customquests.standardcontent.rewardtypes;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.logic.IRewardType;
import com.vincentmet.customquests.helpers.MouseButton;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

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
	public void executeReward(PlayerEntity player){
		for(int i=0; i<count;i++){
			(player.getEntityWorld().getWorld()).addEntity(Objects.requireNonNull(entity.create(player.world, new CompoundNBT(), new TranslationTextComponent("Your Reward <3"), player, player.getPosition(), SpawnReason.COMMAND, true, false)));
		}
	}
	
	@Override
	public Item getIcon(){
		return icon;
	}
	
	@Override
	public String getText(){
		return count + "x " + entity.getName().getString();
	}
	
	@Override
	public Consumer<MouseButton> onSlotClick(){
		return (mouseButton)->{};
	}
	
	@Override
	public String toString(){
		return count + "x " + entity.getName().getString();
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
					ResourceLocation rl = ResourceLocation.tryCreate(jsonPrimitiveStringValue);
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
					ResourceLocation rl = ResourceLocation.tryCreate(jsonPrimitiveStringValue);
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