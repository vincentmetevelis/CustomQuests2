package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestSubrewardType {
	private final int questId;
	private final int rewardId;
	private final int subrewardId;
	private final ResourceLocation subrewardType;

	public MessageEditorRequestUpdateQuestSubrewardType(int questId, int rewardId, int subrewardId, ResourceLocation subrewardType){
		this.questId = questId;
		this.rewardId = rewardId;
		this.subrewardId = subrewardId;
		this.subrewardType = subrewardType;
	}

	public static void encode(MessageEditorRequestUpdateQuestSubrewardType packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.rewardId);
		buffer.writeInt(packet.subrewardId);
		buffer.writeResourceLocation(packet.subrewardType);
	}
	
	public static MessageEditorRequestUpdateQuestSubrewardType decode(FriendlyByteBuf buffer){
		if(buffer.isReadable(14)){//3x4 for ints, 2+ for logic type
			int questId = buffer.readInt();
			int rewardId = buffer.readInt();
			int subrewardId = buffer.readInt();
			ResourceLocation type = buffer.readResourceLocation();
			return new MessageEditorRequestUpdateQuestSubrewardType(questId, rewardId, subrewardId, type);
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestSubrewardType message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(message != null && sender != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesSubrewardExist(message.questId, message.rewardId, message.subrewardId)){
					EditorServerProcessor.Update.Quest.Rewards.Reward.Subrewards.Subreward.updateSubrewardType(message.questId, message.rewardId, message.subrewardId, message.subrewardType);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}