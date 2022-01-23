package com.vincentmet.customquests.network.messages.editor;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorAddChapter{
	public static void encode(MessageEditorAddChapter packet, FriendlyByteBuf buffer){}
	
	public static MessageEditorAddChapter decode(FriendlyByteBuf buffer) {
		return new MessageEditorAddChapter();
	}
	
	public static void handle(final MessageEditorAddChapter message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null){
				if(ApiUtils.hasPlayerEditorAccess(sender)){
					EditorHelper.addEmptyChapter();
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
