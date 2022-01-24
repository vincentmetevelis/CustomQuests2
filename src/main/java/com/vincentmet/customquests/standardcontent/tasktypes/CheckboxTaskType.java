package com.vincentmet.customquests.standardcontent.tasktypes;

import com.google.gson.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.*;
import com.vincentmet.customquests.hierarchy.quest.PredicateTexture;
import com.vincentmet.customquests.network.messages.PacketHandler;
import com.vincentmet.customquests.standardcontent.messages.MessageCheckboxClick;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

public class CheckboxTaskType implements ITaskType{
	private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "checkbox");
	private static final ITextComponent TRANSLATION = new TranslationTextComponent(Ref.MODID + ".standardcontent.tasks.checkbox");
	private static final ITextComponent TRANSLATION_SUBTASK = new TranslationTextComponent(Ref.MODID + ".standardcontent.tasks.checkbox.text_subtask");
	public static final List<PlayerBoundSubtaskReference> TRACKING_LIST = new ArrayList<>();
	private int questId;
	private int taskId;
	private int subtaskId;
	
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
		/*NOOP*/
	}
	
	@Override
	public void executeSubtaskButton(PlayerEntity player){
		/*NOOP*/
	}
	
	@Override
	public IQuestingTexture getIcon(ClientPlayerEntity playerEntity){
		return new PredicateTexture(new ResourceLocation("minecraft", "textures/gui/container/beacon.png"), 90, 222, 16, 16, 256, 256, ()->CombinedProgressHelper.isSubtaskCompleted(playerEntity.getUUID(), questId, taskId, subtaskId));
	}
	
	@Override
	public Runnable onSlotHover(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, ClientPlayerEntity player){
		return ()->{/*NOOP*/};
	}
	
	@Override
	public String getText(ClientPlayerEntity player){
		return "\u2190 " + TRANSLATION_SUBTASK.getString();
	}
	
	@Override
	public int getCompletionAmount(){
		return 1;
	}
    
    @Override
    public Consumer<MouseButton> onSlotClick(ClientPlayerEntity player){
		return (mouseButton)->{
			if(!CombinedProgressHelper.isSubtaskCompleted(player.getUUID(), questId, taskId, subtaskId)){
				PacketHandler.CHANNEL.sendToServer(new MessageCheckboxClick(questId, taskId, subtaskId));
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
	}
	
	@Override
	public JsonObject getJson(){
		return new JsonObject();
	}
}