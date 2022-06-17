package com.vincentmet.customquests.network.messages.editor.cts.requests.update.chapter;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.ChapterHelper;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestChapterQuestlistAddQuestId {
	private final int chapterId;
	private final int questToAddId;

	public MessageEditorRequestChapterQuestlistAddQuestId(int chapterId, int questToAddId){
		this.chapterId = chapterId;
		this.questToAddId = questToAddId;
	}

	public static void encode(MessageEditorRequestChapterQuestlistAddQuestId packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.chapterId);
		buffer.writeInt(packet.questToAddId);
	}
	
	public static MessageEditorRequestChapterQuestlistAddQuestId decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(8)){
			return new MessageEditorRequestChapterQuestlistAddQuestId(buffer.readInt(), buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestChapterQuestlistAddQuestId message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && ChapterHelper.doesChapterExist(message.chapterId) && QuestHelper.doesQuestExist(message.questToAddId)){
					EditorServerProcessor.Update.Chapter.QuestList.addQuestId(message.chapterId, message.questToAddId);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
