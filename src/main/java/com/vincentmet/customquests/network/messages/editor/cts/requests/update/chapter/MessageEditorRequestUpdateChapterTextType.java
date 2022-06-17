package com.vincentmet.customquests.network.messages.editor.cts.requests.update.chapter;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.ChapterHelper;
import com.vincentmet.customquests.api.EditorServerProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateChapterTextType {
	private final int chapterId;
	private final ResourceLocation type;

	public MessageEditorRequestUpdateChapterTextType(int chapterId, ResourceLocation type){
		this.chapterId = chapterId;
		this.type = type;
	}

	public static void encode(MessageEditorRequestUpdateChapterTextType packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.chapterId);
		buffer.writeResourceLocation(packet.type);
	}
	
	public static MessageEditorRequestUpdateChapterTextType decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(5)){//4 for int, 1+ for ResLoc
			int chapterId = buffer.readInt();
			ResourceLocation type = buffer.readResourceLocation();
			return new MessageEditorRequestUpdateChapterTextType(chapterId, type);
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateChapterTextType message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && ChapterHelper.doesChapterExist(message.chapterId)){
					EditorServerProcessor.Update.Chapter.Text.updateType(message.chapterId, message.type);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}