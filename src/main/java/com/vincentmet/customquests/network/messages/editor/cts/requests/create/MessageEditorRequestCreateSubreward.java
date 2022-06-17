package com.vincentmet.customquests.network.messages.editor.cts.requests.create;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestCreateSubreward {
	private final int questId;
	private final int rewardId;

	public MessageEditorRequestCreateSubreward(int questId, int rewardId){
		this.questId = questId;
		this.rewardId = rewardId;
	}

	public static void encode(MessageEditorRequestCreateSubreward packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.rewardId);
	}
	
	public static MessageEditorRequestCreateSubreward decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(8)){
			return new MessageEditorRequestCreateSubreward(buffer.readInt(), buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestCreateSubreward message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesRewardExist(message.questId, message.rewardId)){
					EditorServerProcessor.Create.createSubreward(message.questId, message.rewardId);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
