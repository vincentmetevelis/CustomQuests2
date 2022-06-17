package com.vincentmet.customquests.network.messages.editor.cts.requests.delete;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestDeleteReward {
	private final int questId;
	private final int rewardId;

	public MessageEditorRequestDeleteReward(int questId, int rewardId){
		this.questId = questId;
		this.rewardId = rewardId;
	}
	
	public static void encode(MessageEditorRequestDeleteReward packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.rewardId);
	}
	
	public static MessageEditorRequestDeleteReward decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(8)){
			return new MessageEditorRequestDeleteReward(buffer.readInt(), buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestDeleteReward message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(sender != null && message != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesRewardExist(message.questId, message.rewardId)){
					EditorServerProcessor.Delete.deleteReward(message.questId, message.rewardId);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}