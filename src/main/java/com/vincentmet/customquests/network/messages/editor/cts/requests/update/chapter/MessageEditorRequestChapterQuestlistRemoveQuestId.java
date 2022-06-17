package com.vincentmet.customquests.network.messages.editor.cts.requests.update.chapter;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.ChapterHelper;
import com.vincentmet.customquests.api.EditorServerProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestChapterQuestlistRemoveQuestId {
	private final int chapterId;
	private final int questToRemoveId;

	public MessageEditorRequestChapterQuestlistRemoveQuestId(int chapterId, int questToRemoveId){
		this.chapterId = chapterId;
		this.questToRemoveId = questToRemoveId;
	}

	public static void encode(MessageEditorRequestChapterQuestlistRemoveQuestId packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.chapterId);
		buffer.writeInt(packet.questToRemoveId);
	}
	
	public static MessageEditorRequestChapterQuestlistRemoveQuestId decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(8)){
			return new MessageEditorRequestChapterQuestlistRemoveQuestId(buffer.readInt(), buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestChapterQuestlistRemoveQuestId message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && ChapterHelper.doesChapterExist(message.chapterId)){
					EditorServerProcessor.Update.Chapter.QuestList.removeQuestId(message.chapterId, message.questToRemoveId);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}