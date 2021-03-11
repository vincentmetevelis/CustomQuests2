package com.vincentmet.customquests.standardcontent.tasktypes;

import com.google.gson.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.*;
import com.vincentmet.customquests.hierarchy.quest.ItemSlideshowTexture;
import com.vincentmet.customquests.network.messages.*;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.event.entity.player.AdvancementEvent;

public class AdvancementTaskType implements ITaskType{
	private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "advancement");
	private static final ITextComponent TRANSLATION = new TranslationTextComponent(Ref.MODID + ".standardcontent.tasks.advancement");
	public static final List<PlayerBoundSubtaskReference> TRACKING_LIST = new ArrayList<>();
	private int questId;
	private int taskId;
	private int subtaskId;
	
	private ResourceLocation advancementRL;
	
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
		context.setText(new TranslationTextComponent(Ref.MODID + ".general.check"));
		context.setOnClick((mouseButton, uuid, questId, taskId) -> {
			if(!CombinedProgressHelper.isTaskCompleted(uuid, questId, taskId)){
				PacketHandler.CHANNEL.sendToServer(new MessageTaskButton(questId, taskId));
			}
		});
		return true;
	}
	
	@Override
	public void executeSubtaskCheck(PlayerEntity player, Object object){
		if(!CombinedProgressHelper.isQuestCompleted(player.getUniqueID(), questId)){
			AdvancementEvent event = (AdvancementEvent)object;
			if(event.getAdvancement().getId().equals(advancementRL)){
				CombinedProgressHelper.setValue(player.getUniqueID(), questId, taskId, subtaskId, getCompletionAmount());
				processValue(player);
			}
		}
	}
	
	public void processValue(PlayerEntity player){
		if(CombinedProgressHelper.getValue(player.getUniqueID(), questId, taskId, subtaskId) >= getCompletionAmount()){
			CombinedProgressHelper.completeSubtask(player.getUniqueID(), questId, taskId, subtaskId);
		}
	}
	
	@Override
	public void executeSubtaskButton(PlayerEntity player){
		if(player.getServer()!=null){
			Advancement advancement = player.getServer().getAdvancementManager().getAdvancement(advancementRL);
			if(advancement!=null){
				CombinedProgressHelper.setValue(player.getUniqueID(), questId, taskId, subtaskId, (((ServerPlayerEntity)player).getAdvancements().getProgress(advancement).isDone())?1:0);
				processValue(player);
			}
		}
	}
	
	@Override
	public IQuestingTexture getIcon(ClientPlayerEntity player){
		return new ItemSlideshowTexture(player.connection.getAdvancementManager().getAdvancementList().getAdvancement(advancementRL).getDisplay().getIcon().getItem().getRegistryName(), player.connection.getAdvancementManager().getAdvancementList().getAdvancement(advancementRL).getDisplay().getIcon());
	}
	
	@Override
	public Runnable onSlotHover(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, ClientPlayerEntity player){
		return ()->{
			List<ITextComponent> tooltips = new ArrayList<>();
			tooltips.add(player.connection.getAdvancementManager().getAdvancementList().getAdvancement(advancementRL).getDisplay().getTitle());
			tooltips.add(new StringTextComponent(""));
			tooltips.add(player.connection.getAdvancementManager().getAdvancementList().getAdvancement(advancementRL).getDisplay().getDescription());
			Minecraft.getInstance().currentScreen.func_243308_b(matrixStack, tooltips, mouseX, mouseY);
		};
	}
	
	@Override
	public String getText(ClientPlayerEntity player){
		return player.connection.getAdvancementManager().getAdvancementList().getAdvancement(advancementRL).getDisplayText().getString();
	}
	
	@Override
	public int getCompletionAmount(){
		return 1;
	}
    
    @Override
    public Consumer<MouseButton> onSlotClick(ClientPlayerEntity player){
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
		
		if(json.has("advancement")){
			JsonElement jsonElement = json.get("advancement");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isString()){
					String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
					advancementRL = ResourceLocation.tryCreate(jsonPrimitiveStringValue);
					if(advancementRL == null){
						Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > advancement': Value is not a valid advancement, please use a valid advancement id, defaulting to 'minecraft:story/mine_stone'!");
						advancementRL = new ResourceLocation("minecraft", "story/mine_stone");
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > advancement': Value is not a String, defaulting to 'minecraft:story/mine_stone'!");
					advancementRL = new ResourceLocation("minecraft", "story/mine_stone");
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > advancement': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:story/mine_stone'!");
				advancementRL = new ResourceLocation("minecraft", "story/mine_stone");
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + questId + " > tasks > entries > " + taskId + " > sub_tasks > entries > " + subtaskId + " > advancement': Not detected, defaulting to 'minecraft:story/mine_stone'!");
			advancementRL = new ResourceLocation("minecraft", "story/mine_stone");
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("advancement", advancementRL.toString());
		return json;
	}
}