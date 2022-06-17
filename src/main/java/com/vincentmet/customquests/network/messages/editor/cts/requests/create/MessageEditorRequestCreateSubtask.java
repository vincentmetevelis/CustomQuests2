package com.vincentmet.customquests.network.messages.editor.cts.requests.create;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestCreateSubtask {
	private final int questId;
	private final int taskId;

	public MessageEditorRequestCreateSubtask(int questId, int taskId){
		this.questId = questId;
		this.taskId = taskId;
	}

	public static void encode(MessageEditorRequestCreateSubtask packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.taskId);
	}
	
	public static MessageEditorRequestCreateSubtask decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(8)){
			return new MessageEditorRequestCreateSubtask(buffer.readInt(), buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestCreateSubtask message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesTaskExist(message.questId, message.taskId)){
					EditorServerProcessor.Create.createSubtask(message.questId, message.taskId);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
