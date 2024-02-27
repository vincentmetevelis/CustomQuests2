package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestPositionX {
	private final int questId;
	private final int x;

	public MessageEditorRequestUpdateQuestPositionX(int questId, int x){
		this.questId = questId;
		this.x = x;
	}

	public static void encode(MessageEditorRequestUpdateQuestPositionX packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.x);
	}
	
	public static MessageEditorRequestUpdateQuestPositionX decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(2*Integer.BYTES)){
			return new MessageEditorRequestUpdateQuestPositionX(buffer.readInt(), buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestPositionX message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender)&& QuestHelper.doesQuestExist(message.questId)){
					EditorServerProcessor.Update.Quest.Position.updateX(message.questId, message.x);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
