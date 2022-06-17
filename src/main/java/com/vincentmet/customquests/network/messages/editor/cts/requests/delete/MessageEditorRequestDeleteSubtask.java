package com.vincentmet.customquests.network.messages.editor.cts.requests.delete;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestDeleteSubtask {
	private final int questId;
	private final int taskId;
	private final int subtaskId;

	public MessageEditorRequestDeleteSubtask(int questId, int taskId, int subtaskId){
		this.questId = questId;
		this.taskId = taskId;
		this.subtaskId = subtaskId;
	}
	
	public static void encode(MessageEditorRequestDeleteSubtask packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.taskId);
		buffer.writeInt(packet.subtaskId);
	}
	
	public static MessageEditorRequestDeleteSubtask decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(12)){
			return new MessageEditorRequestDeleteSubtask(buffer.readInt(), buffer.readInt(), buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestDeleteSubtask message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesSubtaskExist(message.questId, message.taskId, message.subtaskId)){
					EditorServerProcessor.Delete.deleteSubtask(message.questId, message.taskId, message.subtaskId);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}