package com.vincentmet.customquests.network.messages.editor.cts.requests.delete;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestDeleteQuest {
	private final int questId;
	
	public MessageEditorRequestDeleteQuest(int questId){
		this.questId = questId;
	}
	
	public static void encode(MessageEditorRequestDeleteQuest packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
	}
	
	public static MessageEditorRequestDeleteQuest decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(4)){
			return new MessageEditorRequestDeleteQuest(buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestDeleteQuest message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesQuestExist(message.questId)){
					EditorServerProcessor.Delete.deleteQuest(message.questId);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}