package com.vincentmet.customquests.network.messages.editor;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRemoveQuest{
	private final int questId;
	
	public MessageEditorRemoveQuest(int questId){
		this.questId = questId;
	}
	
	public static void encode(MessageEditorRemoveQuest packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
	}
	
	public static MessageEditorRemoveQuest decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(4)){
			return new MessageEditorRemoveQuest(buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRemoveQuest message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender)){
					EditorHelper.deleteQuest(message.questId);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}