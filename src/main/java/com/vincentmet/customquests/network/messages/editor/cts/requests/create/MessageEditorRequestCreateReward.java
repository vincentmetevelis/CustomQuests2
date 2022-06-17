package com.vincentmet.customquests.network.messages.editor.cts.requests.create;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestCreateReward {
	private final int questId;

	public MessageEditorRequestCreateReward(int questId){
		this.questId = questId;
	}

	public static void encode(MessageEditorRequestCreateReward packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
	}
	
	public static MessageEditorRequestCreateReward decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(4)){
			return new MessageEditorRequestCreateReward(buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestCreateReward message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesQuestExist(message.questId)){
					EditorServerProcessor.Create.createReward(message.questId);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
