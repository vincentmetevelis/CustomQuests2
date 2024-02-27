package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestDependenciesAddQuestId {
	private final int questId;
	private final int questToAddId;

	public MessageEditorRequestUpdateQuestDependenciesAddQuestId(int questId, int questToAddId){
		this.questId = questId;
		this.questToAddId = questToAddId;
	}

	public static void encode(MessageEditorRequestUpdateQuestDependenciesAddQuestId packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.questToAddId);
	}
	
	public static MessageEditorRequestUpdateQuestDependenciesAddQuestId decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(2*Integer.BYTES)){
			return new MessageEditorRequestUpdateQuestDependenciesAddQuestId(buffer.readInt(), buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestDependenciesAddQuestId message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesQuestExist(message.questId) && QuestHelper.doesQuestExist(message.questToAddId)){
					EditorServerProcessor.Update.Quest.Dependencies.addQuestId(message.questId, message.questToAddId);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
