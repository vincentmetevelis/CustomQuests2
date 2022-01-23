package com.vincentmet.customquests.network.messages.editor;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorAddQuest{
	public static void encode(MessageEditorAddQuest packet, FriendlyByteBuf buffer){}
	
	public static MessageEditorAddQuest decode(FriendlyByteBuf buffer) {
		return new MessageEditorAddQuest();
	}
	
	public static void handle(final MessageEditorAddQuest message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null){
				if(ApiUtils.hasPlayerEditorAccess(sender)){
					EditorHelper.addEmptyQuest();
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
