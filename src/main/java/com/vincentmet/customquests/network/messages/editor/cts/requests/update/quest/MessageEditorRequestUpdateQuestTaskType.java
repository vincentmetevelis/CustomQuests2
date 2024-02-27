package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.LogicType;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestTaskType {
	private final int questId;
	private final int taskId;
	private final ResourceLocation taskType;

	public MessageEditorRequestUpdateQuestTaskType(int questId, int taskId, ResourceLocation taskType){
		this.questId = questId;
		this.taskId = taskId;
		this.taskType = taskType;
	}

	public static void encode(MessageEditorRequestUpdateQuestTaskType packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.taskId);
		buffer.writeResourceLocation(packet.taskType);
	}
	
	public static MessageEditorRequestUpdateQuestTaskType decode(FriendlyByteBuf buffer){
		if(buffer.isReadable(10)){//2x4 for ints, 2+ for logic type
			int questId = buffer.readInt();
			int taskId = buffer.readInt();
			ResourceLocation type = buffer.readResourceLocation();
			return new MessageEditorRequestUpdateQuestTaskType(questId, taskId, type);
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestTaskType message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(message != null && sender != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesTaskExist(message.questId, message.taskId)){
					EditorServerProcessor.Update.Quest.Tasks.Task.updateTaskType(message.questId, message.taskId, message.taskType);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}