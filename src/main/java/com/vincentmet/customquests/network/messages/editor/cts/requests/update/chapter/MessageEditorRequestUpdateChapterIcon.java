package com.vincentmet.customquests.network.messages.editor.cts.requests.update.chapter;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateChapterIcon {
	private final int chapterId;
	private final ResourceLocation icon;

	public MessageEditorRequestUpdateChapterIcon(int chapterId, ResourceLocation icon){
		this.chapterId = chapterId;
		this.icon = icon;
	}

	public static void encode(MessageEditorRequestUpdateChapterIcon packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.chapterId);
		buffer.writeResourceLocation(packet.icon);
	}
	
	public static MessageEditorRequestUpdateChapterIcon decode(FriendlyByteBuf buffer){
		if(buffer.isReadable(5)){//4 for int, 1+ for RL
			return new MessageEditorRequestUpdateChapterIcon(buffer.readInt(), buffer.readResourceLocation());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateChapterIcon message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(message != null && sender != null){
				if(ApiUtils.hasPlayerEditorAccess(sender)){
					EditorServerProcessor.Update.Chapter.updateIcon(message.chapterId, message.icon);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}