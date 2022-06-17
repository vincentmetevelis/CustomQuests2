package com.vincentmet.customquests.network.messages.editor.cts.requests.delete;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestDeleteSubreward {
	private final int questId;
	private final int rewardId;
	private final int subrewardId;

	public MessageEditorRequestDeleteSubreward(int questId, int rewardId, int subrewardId){
		this.questId = questId;
		this.rewardId = rewardId;
		this.subrewardId = subrewardId;
	}
	
	public static void encode(MessageEditorRequestDeleteSubreward packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.rewardId);
		buffer.writeInt(packet.subrewardId);
	}
	
	public static MessageEditorRequestDeleteSubreward decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(12)){
			return new MessageEditorRequestDeleteSubreward(buffer.readInt(), buffer.readInt(), buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestDeleteSubreward message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesSubrewardExist(message.questId, message.rewardId, message.subrewardId)){
					EditorServerProcessor.Delete.deleteSubreward(message.questId, message.rewardId, message.subrewardId);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}