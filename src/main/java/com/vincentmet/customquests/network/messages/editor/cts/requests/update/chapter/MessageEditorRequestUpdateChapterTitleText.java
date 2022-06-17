package com.vincentmet.customquests.network.messages.editor.cts.requests.update.chapter;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.ChapterHelper;
import com.vincentmet.customquests.api.EditorServerProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateChapterTitleText {
	private final int chapterId;
	private final String text;

	public MessageEditorRequestUpdateChapterTitleText(int chapterId, String text){
		this.chapterId = chapterId;
		this.text = text;
	}

	public static void encode(MessageEditorRequestUpdateChapterTitleText packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.chapterId);
		buffer.writeUtf(packet.text);
	}
	
	public static MessageEditorRequestUpdateChapterTitleText decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(5)) {//4 for int, 1+ for String
			int chapterId = buffer.readInt();
			String text = buffer.readUtf();
			return new MessageEditorRequestUpdateChapterTitleText(chapterId, text);
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateChapterTitleText message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && ChapterHelper.doesChapterExist(message.chapterId)){
					EditorServerProcessor.Update.Chapter.Title.updateText(message.chapterId, message.text);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}