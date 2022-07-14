package com.vincentmet.customquests.network.messages.sync.stc.update;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.api.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncUpdateSingleReward {
	private final int questId;
	private final int rewardId;
	private JsonObject jsonObject;

	private MessageStcSyncUpdateSingleReward(int questId, int rewardId, JsonObject jsonObject){
		this.questId = questId;
		this.rewardId = rewardId;
		this.jsonObject = jsonObject;
	}

	public MessageStcSyncUpdateSingleReward(int questId, int rewardId){
		this.questId = questId;
		this.rewardId = rewardId;
		if(QuestHelper.doesRewardExist(questId, rewardId)){
			jsonObject = QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId).getJson();
		}else{
			ServerUtils.Packets.Delete.deleteSingleRewardAtAllClients(questId, rewardId);
		}
	}

	public static void encode(MessageStcSyncUpdateSingleReward packet, FriendlyByteBuf buffer){
		if(QuestHelper.doesRewardExist(packet.questId, packet.rewardId) && packet.jsonObject != null){
			buffer.writeInt(packet.questId);
			buffer.writeInt(packet.rewardId);
			buffer.writeUtf(packet.jsonObject.toString());
		}
	}
	
	public static MessageStcSyncUpdateSingleReward decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(10)){//4 for int, 4 for int, 2+ for json
			return new MessageStcSyncUpdateSingleReward(buffer.readInt(), buffer.readInt(), JsonParser.parseString(buffer.readUtf()).getAsJsonObject());
		}
		return null;
	}
	
	public static void handle(final MessageStcSyncUpdateSingleReward message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if(message!=null){
				EditorClientProcessor.Update.Quests.updateSingleReward(message.questId, message.rewardId, message.jsonObject);
				ClientUtils.reloadMainGuiIfOpen();
				ClientUtils.reloadEditorIfOpen();
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
