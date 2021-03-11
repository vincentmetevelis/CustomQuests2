package com.vincentmet.customquests.standardcontent.rewardtypes;

import com.google.gson.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.IRewardType;
import com.vincentmet.customquests.helpers.MouseButton;
import java.util.function.Consumer;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class CommandRewardType implements IRewardType{
	private static ResourceLocation ID = new ResourceLocation(Ref.MODID, "command");
	private String command = "";
	private String displayText = "";
	private Item icon = Items.COMMAND_BLOCK;
	
	private int parentQuestId;
	private int parentRewardId;
    
    @Override
    public ResourceLocation getId(){
        return ID;
    }
    
    @Override
	public void executeReward(PlayerEntity player){
		MinecraftServer ms = player.getServer();
		if(ms!=null){
			final CommandDispatcher<CommandSource> dispatcher = ms.getCommandManager().getDispatcher();
			try {
				dispatcher.execute("execute at " + player.getDisplayName().getString() + " run " + command, player.getServer().getCommandSource().withFeedbackDisabled());
			} catch (CommandSyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Item getIcon(){
		return icon;
	}
	
	@Override
	public Runnable onSlotHover(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		return ()->{/*NOOP*/};
	}
	
	@Override
	public String getText(){
		return displayText;
	}
	
	@Override
	public Consumer<MouseButton> onSlotClick(){
		return (mouseButton)->{};
	}
	
	@Override
	public String toString(){
		return "/" + command;
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
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > icon': Value is not a valid item, please use a valid item id, defaulting to 'minecraft:command_block'!");
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > icon': Value is not a String, defaulting to 'minecraft:command_block'!");
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > icon': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:command_block'!");
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > icon': Not detected, defaulting to 'minecraft:command_block'!");
		}
		
		if(json.has("text")){
			JsonElement jsonElement = json.get("text");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					displayText =  jsonPrimitive.getAsString();
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > text': Value is not a String, defaulting to 'Hidden'!");
					displayText = new TranslationTextComponent(Ref.MODID + ".general.hidden").getString();
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > text': Value is not a JsonPrimitive, please use a String, defaulting to 'Hidden'!");
				displayText = new TranslationTextComponent(Ref.MODID + ".general.hidden").getString();
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > text': Not detected, defaulting to 'Hidden'!");
			displayText = new TranslationTextComponent(Ref.MODID + ".general.hidden").getString();
		}
		
		if(json.has("command")){
			JsonElement jsonElement = json.get("command");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					command =  jsonPrimitive.getAsString();
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > command': Value is not a String, defaulting to 'give @p minecraft:dirt'!");
					command = "give @p minecraft:dirt";
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > command': Value is not a JsonPrimitive, please use a String, defaulting to 'give @p minecraft:dirt'!");
				command = "give @p minecraft:dirt";
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > rewards > entries > " + parentRewardId + " > content > command': Not detected, defaulting to 'give @p minecraft:dirt'!");
			command = "give @p minecraft:dirt";
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("command", command);
		json.addProperty("text", displayText);
		json.addProperty("icon", icon.getRegistryName().toString());
		return json;
	}
}