package com.vincentmet.customquests.network.messages.editor;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRemoveChapter{
	private int chapterId;
	
	public MessageEditorRemoveChapter(){}
	public MessageEditorRemoveChapter(int chapterId){
		this.chapterId = chapterId;
	}
	
	public static void encode(MessageEditorRemoveChapter packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.chapterId);
	}
	
	public static MessageEditorRemoveChapter decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(4)){
			return new MessageEditorRemoveChapter(buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRemoveChapter message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender)){
					EditorHelper.deleteChapter(message.chapterId);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}