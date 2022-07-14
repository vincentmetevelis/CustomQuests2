package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestButtonIcon {
	private final int questId;
	private final ResourceLocation icon;

	public MessageEditorRequestUpdateQuestButtonIcon(int questId, ResourceLocation icon){
		this.questId = questId;
		this.icon = icon;
	}

	public static void encode(MessageEditorRequestUpdateQuestButtonIcon packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeResourceLocation(packet.icon);
	}
	
	public static MessageEditorRequestUpdateQuestButtonIcon decode(FriendlyByteBuf buffer){
		if(buffer.isReadable(5)){//4 for int, 1+ for RL
			return new MessageEditorRequestUpdateQuestButtonIcon(buffer.readInt(), buffer.readResourceLocation());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestButtonIcon message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(message != null && sender != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesQuestExist(message.questId)){
					EditorServerProcessor.Update.Quest.Button.updateIcon(message.questId, message.icon);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}