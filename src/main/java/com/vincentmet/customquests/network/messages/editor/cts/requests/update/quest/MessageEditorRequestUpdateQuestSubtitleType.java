package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestSubtitleType {
	private final int questId;
	private final ResourceLocation type;

	public MessageEditorRequestUpdateQuestSubtitleType(int questId, ResourceLocation type){
		this.questId = questId;
		this.type = type;
	}

	public static void encode(MessageEditorRequestUpdateQuestSubtitleType packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeResourceLocation(packet.type);
	}
	
	public static MessageEditorRequestUpdateQuestSubtitleType decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(5)){//4 for int, 1+ for ResLoc
			int questId = buffer.readInt();
			ResourceLocation type = buffer.readResourceLocation();
			return new MessageEditorRequestUpdateQuestSubtitleType(questId, type);
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestSubtitleType message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesQuestExist(message.questId)){
					EditorServerProcessor.Update.Quest.Subtitle.updateType(message.questId, message.type);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}