package com.vincentmet.customquests.standardcontent.messages;

import com.vincentmet.customquests.api.CombinedProgressHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageCheckboxClick{
	public int questId;
	public int taskId;
	public int subtaskId;
	
	public MessageCheckboxClick(int questId, int taskId, int subtaskId){
		this.questId = questId;
		this.taskId = taskId;
		this.subtaskId = subtaskId;
	}
	
	public static void encode(MessageCheckboxClick packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.taskId);
		buffer.writeInt(packet.subtaskId);
	}
	
	public static MessageCheckboxClick decode(FriendlyByteBuf buffer) {
		if(buffer.readableBytes() >= 12){
			int questId = buffer.readInt();
			int taskId = buffer.readInt();
			int subtaskId = buffer.readInt();
			return new MessageCheckboxClick(questId, taskId, subtaskId);
		}
		return null;
	}
	
	public static void handle(final MessageCheckboxClick message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if(message!=null){
				CombinedProgressHelper.setValue(ctx.get().getSender().getUUID(), message.questId, message.taskId, message.subtaskId, 1);
				CombinedProgressHelper.completeSubtask(ctx.get().getSender().getUUID(), message.questId, message.taskId, message.subtaskId);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
