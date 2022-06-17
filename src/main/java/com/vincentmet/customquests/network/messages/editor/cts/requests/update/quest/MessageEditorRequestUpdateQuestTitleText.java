package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestTitleText {
	private final int questId;
	private final String text;

	public MessageEditorRequestUpdateQuestTitleText(int questId, String text){
		this.questId = questId;
		this.text = text;
	}

	public static void encode(MessageEditorRequestUpdateQuestTitleText packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeUtf(packet.text);
	}
	
	public static MessageEditorRequestUpdateQuestTitleText decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(5)) {//4 for int, 1+ for String
			int questId = buffer.readInt();
			String text = buffer.readUtf();
			return new MessageEditorRequestUpdateQuestTitleText(questId, text);
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestTitleText message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesQuestExist(message.questId)){
					EditorServerProcessor.Update.Quest.Subtitle.updateText(message.questId, message.text);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
