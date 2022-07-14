package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestButtonShape {
	private final int questId;
	private final ResourceLocation shape;

	public MessageEditorRequestUpdateQuestButtonShape(int questId, ResourceLocation shape){
		this.questId = questId;
		this.shape = shape;
	}

	public static void encode(MessageEditorRequestUpdateQuestButtonShape packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeResourceLocation(packet.shape);
	}
	
	public static MessageEditorRequestUpdateQuestButtonShape decode(FriendlyByteBuf buffer){
		if(buffer.isReadable(5)){//4 for int, 1+ for RL
			return new MessageEditorRequestUpdateQuestButtonShape(buffer.readInt(), buffer.readResourceLocation());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestButtonShape message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(message != null && sender != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesQuestExist(message.questId)){
					EditorServerProcessor.Update.Quest.Button.updateShape(message.questId, message.shape);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}