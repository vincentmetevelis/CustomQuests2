package com.vincentmet.customquests.standardcontent.rewardtypes;

import com.google.gson.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.IRewardType;
import com.vincentmet.customquests.helpers.MouseButton;
import java.util.function.Consumer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;

public class XpRewardType implements IRewardType{
	private static ResourceLocation ID = new ResourceLocation(Ref.MODID, "xp");
	private boolean inLevels = false;
	private int amount = 0;
	
	private int parentQuestId;
	private int parentRewardId;
    
    @Override
    public ResourceLocation getId(){
        return ID;
    }
    
    @Override
	public void executeReward(PlayerEntity player){
		if(inLevels){
			player.giveExperienceLevels(amount);
		}else{
			player.giveExperiencePoints(amount);
		}
	}
	
	@Override
	public Item getIcon(){
		return Items.EXPERIENCE_BOTTLE;
	}
	
	@Override
	public Runnable onSlotHover(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		return ()->{/*NOOP*/};
	}
	
	@Override
	public String getText(){
		return amount + " Experience" + (inLevels?" Levels":" Points");
	}
	
	@Override
	public Consumer<MouseButton> onSlotClick(){
		return (mouseButton)->{};
	}
	
	@Override
	public String toString(){
		return amount + " Experience" + (inLevels?" Levels":"");
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
		
		if(json.has("in_levels")){
			JsonElement jsonElement = json.get("in_levels");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isBoolean()){
					inLevels = jsonPrimitive.getAsBoolean();
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > in_levels': Value is not a Boolean, defaulting to 'false'!");
					inLevels = false;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > in_levels': Value is not a JsonPrimitive, please use a boolean, defaulting to 'false'!");
				inLevels = false;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > in_levels': Not detected, defaulting to 'false'");
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
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > amount': Value is not >= 1, defaulting to '1'!");
						amount = 1;
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > amount': Value is not an Integer, defaulting to '1'!");
					amount = 1;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > amount': Value is not a JsonPrimitive, please use a Double, defaulting to '1'!");
				amount = 1;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > amount': Not detected, defaulting to '1'!");
			amount = 1;
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("in_levels", inLevels);
		json.addProperty("amount", amount);
		return json;
	}
}