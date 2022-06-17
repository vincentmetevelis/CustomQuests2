package com.vincentmet.customquests.network.messages.sync;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.api.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncUpdateSingleSubreward {
	private final int questId;
	private final int rewardId;
	private final int subrewardId;
	private JsonObject jsonObject;

	private MessageStcSyncUpdateSingleSubreward(int questId, int rewardId, int subrewardId, JsonObject jsonObject){
		this.questId = questId;
		this.rewardId = rewardId;
		this.subrewardId = subrewardId;
		this.jsonObject = jsonObject;
	}

	public MessageStcSyncUpdateSingleSubreward(int questId, int rewardId, int subrewardId){
		this.questId = questId;
		this.rewardId = rewardId;
		this.subrewardId = subrewardId;
		if(QuestHelper.doesSubrewardExist(questId, rewardId, subrewardId)){
			jsonObject = QuestingStorage.getSidedQuestsMap().get(questId).getRewards().get(rewardId).getSubRewards().get(subrewardId).getJson();
		}else{
			ServerUtils.Packets.Delete.deleteSingleSubrewardAtAllClients(questId, rewardId, subrewardId);
		}
	}

	public static void encode(MessageStcSyncUpdateSingleSubreward packet, FriendlyByteBuf buffer){
		if(QuestHelper.doesSubrewardExist(packet.questId, packet.rewardId, packet.subrewardId) && packet.jsonObject != null){
			buffer.writeInt(packet.questId);
			buffer.writeInt(packet.rewardId);
			buffer.writeInt(packet.subrewardId);
			buffer.writeUtf(packet.jsonObject.toString());
		}
	}
	
	public static MessageStcSyncUpdateSingleSubreward decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(14)){//4 for int, 4 for int, 4 for int, 2+ for json
			return new MessageStcSyncUpdateSingleSubreward(buffer.readInt(), buffer.readInt(), buffer.readInt(), JsonParser.parseString(buffer.readUtf()).getAsJsonObject());
		}
		return null;
	}
	
	public static void handle(final MessageStcSyncUpdateSingleSubreward message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if(message!=null){
				EditorClientProcessor.Update.Quests.updateSingleSubreward(message.questId, message.rewardId, message.subrewardId, message.jsonObject);
				ClientUtils.reloadMainGuiIfOpen();
				ClientUtils.reloadEditorIfOpen();
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}