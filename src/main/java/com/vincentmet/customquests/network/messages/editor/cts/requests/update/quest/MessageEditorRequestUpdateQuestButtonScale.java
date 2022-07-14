package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.ChapterHelper;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestButtonScale {
	private final int questId;
	private final double scale;

	public MessageEditorRequestUpdateQuestButtonScale(int questId, double scale){
		this.questId = questId;
		this.scale = scale;
	}

	public static void encode(MessageEditorRequestUpdateQuestButtonScale packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeDouble(packet.scale);
	}
	
	public static MessageEditorRequestUpdateQuestButtonScale decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(Integer.BYTES + Double.BYTES)){
			return new MessageEditorRequestUpdateQuestButtonScale(buffer.readInt(), buffer.readDouble());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestButtonScale message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender)&& QuestHelper.doesQuestExist(message.questId)){
					EditorServerProcessor.Update.Quest.Button.updateScale(message.questId, message.scale);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
