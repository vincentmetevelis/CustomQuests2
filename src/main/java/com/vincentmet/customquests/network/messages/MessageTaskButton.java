package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.api.ProgressHelper;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class MessageTaskButton{
	public int questId;
	public int taskId;
	
	public MessageTaskButton(int questId, int taskId){
		this.questId = questId;
		this.taskId = taskId;
	}
	
	public static void encode(MessageTaskButton packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.taskId);
	}
	
	public static MessageTaskButton decode(FriendlyByteBuf buffer) {
		if(buffer.readableBytes() >= 8){
			int questId = buffer.readInt();
			int taskId = buffer.readInt();
			return new MessageTaskButton(questId, taskId);
		}
		return null;
	}
	
	public static void handle(final MessageTaskButton message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if(message!=null){
				ProgressHelper.executeTaskCallback(Objects.requireNonNull(ctx.get().getSender()), message.questId, message.taskId);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
