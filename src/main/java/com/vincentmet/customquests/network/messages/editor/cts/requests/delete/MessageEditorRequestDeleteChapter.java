package com.vincentmet.customquests.network.messages.editor.cts.requests.delete;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.ChapterHelper;
import com.vincentmet.customquests.api.EditorServerProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestDeleteChapter {
	private final int chapterId;

	public MessageEditorRequestDeleteChapter(int chapterId){
		this.chapterId = chapterId;
	}
	
	public static void encode(MessageEditorRequestDeleteChapter packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.chapterId);
	}
	
	public static MessageEditorRequestDeleteChapter decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(4)){
			return new MessageEditorRequestDeleteChapter(buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestDeleteChapter message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && ChapterHelper.doesChapterExist(message.chapterId)){
					EditorServerProcessor.Delete.deleteChapter(message.chapterId);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}