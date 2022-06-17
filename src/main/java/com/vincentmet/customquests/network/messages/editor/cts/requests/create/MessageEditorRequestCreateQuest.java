package com.vincentmet.customquests.network.messages.editor.cts.requests.create;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestCreateQuest {
	public static void encode(MessageEditorRequestCreateQuest packet, FriendlyByteBuf buffer){}
	
	public static MessageEditorRequestCreateQuest decode(FriendlyByteBuf buffer) {
		return new MessageEditorRequestCreateQuest();
	}
	
	public static void handle(final MessageEditorRequestCreateQuest message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null){
				if(ApiUtils.hasPlayerEditorAccess(sender)){
					EditorServerProcessor.Create.createQuest();
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
