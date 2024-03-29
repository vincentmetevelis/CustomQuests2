package com.vincentmet.customquests.standardcontent.tasktypes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.IntCounter;
import com.vincentmet.customquests.helpers.MouseButton;
import com.vincentmet.customquests.helpers.PlayerBoundSubtaskReference;
import com.vincentmet.customquests.hierarchy.quest.ItemSlideshowTexture;
import com.vincentmet.customquests.network.messages.MessageTaskButton;
import com.vincentmet.customquests.network.messages.PacketHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class XpSubmitTaskType implements ITaskType{
	private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "xp_submit");
	private static final Component TRANSLATION = new TranslatableComponent(Ref.MODID + ".standardcontent.tasks.xp_submit");
	public static final List<PlayerBoundSubtaskReference> TRACKING_LIST = new ArrayList<>();
	private int questId;
	private int taskId;
	private int subtaskId;
	
	private int amount = 1;
	private boolean inLevels;
	ItemSlideshowTexture icon = new ItemSlideshowTexture(Items.EXPERIENCE_BOTTLE.getRegistryName(), new ItemStack(Items.EXPERIENCE_BOTTLE));
	
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
		context.setText(new TranslatableComponent(Ref.MODID  + ".standardcontent.tasks.button.xp_submit"));
		context.setOnClick((mouseButton, uuid, questId, taskId) -> {
			if(!CombinedProgressHelper.isTaskCompleted(uuid, questId, taskId)){
				PacketHandler.CHANNEL.sendToServer(new MessageTaskButton(questId, taskId));
			}
		});
		return true;
	}
	
	@Override
	public void executeSubtaskCheck(Player player, Object object){
		if(!CombinedProgressHelper.isQuestCompleted(player.getUUID(), questId)){
			IntCounter xpLeftToHandIn = new IntCounter(getXpCountLeftToHandIn(player.getUUID(), questId, taskId, subtaskId));
			int xpBefore = xpLeftToHandIn.getValue();
			if(inLevels){
				if(xpLeftToHandIn.getValue() >= player.experienceLevel){
					CombinedProgressHelper.addValue(player.getUUID(), questId, taskId, subtaskId, player.experienceLevel);
					xpLeftToHandIn.add(-player.experienceLevel);
					player.giveExperienceLevels(-player.experienceLevel);
				}else{
					CombinedProgressHelper.addValue(player.getUUID(), questId, taskId, subtaskId, xpLeftToHandIn.getValue());
					player.giveExperienceLevels(-(player.experienceLevel - xpLeftToHandIn.getValue()));
					xpLeftToHandIn.setValue(0);
				}
			}else{
				if(xpLeftToHandIn.getValue() >= player.totalExperience){
					CombinedProgressHelper.addValue(player.getUUID(), questId, taskId, subtaskId, player.totalExperience);
					xpLeftToHandIn.add(-player.totalExperience);
					player.giveExperiencePoints(-player.totalExperience);
				}else{
					CombinedProgressHelper.addValue(player.getUUID(), questId, taskId, subtaskId, xpLeftToHandIn.getValue());
					player.giveExperiencePoints(-(player.totalExperience - xpLeftToHandIn.getValue()));
					xpLeftToHandIn.setValue(0);
				}
			}
			int xpAfter = xpLeftToHandIn.getValue();
			if(xpBefore != xpAfter){
				processValue(player);
				ServerUtils.sendProgressAndParties((ServerPlayer) player);
			}
		}
	}
	
	public void processValue(Player player){
		if(CombinedProgressHelper.getValue(player.getUUID(), questId, taskId, subtaskId) >= amount){
			CombinedProgressHelper.completeSubtask(player.getUUID(), questId, taskId, subtaskId);
		}
	}
	
	public int getXpCountLeftToHandIn(UUID uuid, int questId, int taskId, int subtaskId){
		return amount - CombinedProgressHelper.getValue(uuid, questId, taskId, subtaskId);
	}
	
	@Override
	public void executeSubtaskButton(Player player){
		executeSubtaskCheck(player, null);
	}
	
	@Override
	public IQuestingTexture getIcon(LocalPlayer player){
		return icon;
	}
	
	@Override
	public Runnable onSlotHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, LocalPlayer player){
		return ()->{};
	}
	
	@Override
	public String getText(LocalPlayer player){
		return amount + " " + new TranslatableComponent(Ref.MODID + ".general.experience").getString() + (inLevels?" "+new TranslatableComponent(Ref.MODID + ".general.levels").getString():" " + new TranslatableComponent(Ref.MODID + ".general.points").getString());
	}
	
	@Override
	public int getCompletionAmount(){
		return amount;
	}
	
	@Override
	public Consumer<MouseButton> onSlotClick(LocalPlayer player){
		return (mouseButton)->{/*NOOP*/};
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
		
		if(json.has("in_levels")){
			JsonElement jsonElement = json.get("in_levels");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isBoolean()){
					inLevels = jsonPrimitive.getAsBoolean();
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > in_levels': Value is not a Boolean, defaulting to 'false'!");
					inLevels = false;
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > in_levels': Value is not a JsonPrimitive, please use a boolean, defaulting to 'false'!");
				inLevels = false;
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > in_levels': Not detected, defaulting to 'false'");
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
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > amount': Value is not >= 1, defaulting to '1'!");
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > amount': Value is not an Integer, defaulting to '1'!");
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > amount': Value is not a JsonPrimitive, please use a Double, defaulting to '1'!");
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > amount': Not detected, defaulting to '1'!");
		}
		
		if(json.has("icon")){
			JsonElement jsonElement = json.get("icon");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
					ResourceLocation rl = ResourceLocation.tryParse(jsonPrimitiveStringValue);
					if(rl != null){
						icon = new ItemSlideshowTexture(rl, new ItemStack(ForgeRegistries.ITEMS.getValue(rl)));
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > icon': Value is not a valid item, please use a valid item id, defaulting to 'minecraft:diamond_sword'!");
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > icon': Value is not a String, defaulting to 'minecraft:diamond_sword'!");
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > icon': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:diamond_sword'!");
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > icon': Not detected, defaulting to 'minecraft:diamond_sword'!");
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("in_levels", inLevels);
		json.addProperty("amount", amount);
		json.addProperty("icon", icon.getResourceLocation().toString());
		return json;
	}
}