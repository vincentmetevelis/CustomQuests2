package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestDependenciesRemoveQuestId {
	private final int questId;
	private final int questToRemove;

	public MessageEditorRequestUpdateQuestDependenciesRemoveQuestId(int questId, int questToRemove){
		this.questId = questId;
		this.questToRemove = questToRemove;
	}

	public static void encode(MessageEditorRequestUpdateQuestDependenciesRemoveQuestId packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.questToRemove);
	}
	
	public static MessageEditorRequestUpdateQuestDependenciesRemoveQuestId decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(2*Integer.BYTES)){
			return new MessageEditorRequestUpdateQuestDependenciesRemoveQuestId(buffer.readInt(), buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestDependenciesRemoveQuestId message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesQuestExist(message.questId)){
					EditorServerProcessor.Update.Quest.Dependencies.removeQuestId(message.questId, message.questToRemove);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
