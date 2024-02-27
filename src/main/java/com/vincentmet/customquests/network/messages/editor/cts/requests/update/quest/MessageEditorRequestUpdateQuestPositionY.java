package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestPositionY {
	private final int questId;
	private final int y;

	public MessageEditorRequestUpdateQuestPositionY(int questId, int y){
		this.questId = questId;
		this.y = y;
	}

	public static void encode(MessageEditorRequestUpdateQuestPositionY packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.y);
	}
	
	public static MessageEditorRequestUpdateQuestPositionY decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(2*Integer.BYTES)){
			return new MessageEditorRequestUpdateQuestPositionY(buffer.readInt(), buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestPositionY message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender)&& QuestHelper.doesQuestExist(message.questId)){
					EditorServerProcessor.Update.Quest.Position.updateY(message.questId, message.y);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
