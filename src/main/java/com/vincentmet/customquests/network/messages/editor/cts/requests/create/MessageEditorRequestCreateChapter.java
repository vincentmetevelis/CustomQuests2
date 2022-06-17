package com.vincentmet.customquests.network.messages.editor.cts.requests.create;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestCreateChapter {
	public static void encode(MessageEditorRequestCreateChapter packet, FriendlyByteBuf buffer){}
	
	public static MessageEditorRequestCreateChapter decode(FriendlyByteBuf buffer) {
		return new MessageEditorRequestCreateChapter();
	}
	
	public static void handle(final MessageEditorRequestCreateChapter message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null){
				if(ApiUtils.hasPlayerEditorAccess(sender)){
					EditorServerProcessor.Create.createChapter();
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
