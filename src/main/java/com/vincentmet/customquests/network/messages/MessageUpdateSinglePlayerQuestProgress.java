package com.vincentmet.customquests.network.messages;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.ProgressHelper;
import com.vincentmet.customquests.api.QuestHelper;
import com.vincentmet.customquests.api.QuestingStorage;
import com.vincentmet.customquests.hierarchy.progress.SingleQuestUserProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MessageUpdateSinglePlayerQuestProgress{
	public JsonObject json;//used for receiving packet only
	public String uuid;
	public int questId;
	
	public MessageUpdateSinglePlayerQuestProgress(String uuid, int questId){
		this.uuid = uuid;
		this.questId = questId;
	}
	
	public MessageUpdateSinglePlayerQuestProgress(String uuid, int questId, JsonObject json){//used for receiving packet only
		this.uuid = uuid;
		this.questId = questId;
		this.json = json;
	}
	
	public static void encode(MessageUpdateSinglePlayerQuestProgress packet, FriendlyByteBuf buffer){
		if(ProgressHelper.doesPlayerExist(UUID.fromString(packet.uuid)) && QuestHelper.doesQuestExist(packet.questId)){
			buffer.writeUtf(packet.uuid);
			buffer.writeInt(packet.questId);
			buffer.writeUtf(QuestingStorage.getSidedPlayersMap().get(packet.uuid).getIndividualProgress().get(packet.questId).getJson().toString());
		}
	}
	
	public static MessageUpdateSinglePlayerQuestProgress decode(FriendlyByteBuf buffer) {
		if(buffer.readableBytes()>0){
			String uuid = buffer.readUtf();
			int questId = buffer.readInt();
			String data = buffer.readUtf();
			JsonObject json = JsonParser.parseString(data).getAsJsonObject();
			return new MessageUpdateSinglePlayerQuestProgress(uuid, questId, json);
		}else{
			return null;
		}
	}
	
	public static void handle(final MessageUpdateSinglePlayerQuestProgress message, Supplier<NetworkEvent.Context> ctx) {
		if(message != null){
			ctx.get().enqueueWork(() -> {
				String uuid = message.uuid;
				int questId = message.questId;
				JsonObject data = message.json;
				SingleQuestUserProgress squp = new SingleQuestUserProgress(UUID.fromString(uuid), questId);
				squp.processJson(data);
				QuestingStorage.getSidedPlayersMap().get(uuid).getIndividualProgress().put(questId, squp);
				ClientUtils.reloadEditorIfOpen();
			});
		}
		ctx.get().setPacketHandled(true);
	}
}