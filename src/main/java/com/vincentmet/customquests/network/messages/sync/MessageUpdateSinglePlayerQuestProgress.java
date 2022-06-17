package com.vincentmet.customquests.network.messages.sync;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.hierarchy.progress.SingleQuestUserProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

import static com.vincentmet.customquests.Ref.CustomQuests.LOGGER;

public class MessageUpdateSinglePlayerQuestProgress{
	public final UUID uuid;
	public final int questId;
	public JsonObject jsonObject;

	private MessageUpdateSinglePlayerQuestProgress(UUID uuid, int questId, JsonObject json){
		this.uuid = uuid;
		this.questId = questId;
		this.jsonObject = json;
	}

	public MessageUpdateSinglePlayerQuestProgress(UUID uuid, int questId){
		this.uuid = uuid;
		this.questId = questId;
		if(ProgressHelper.doesPlayerExist(uuid)){
			jsonObject = QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getIndividualProgress().get(questId).getJson();
		}else{
			ServerUtils.Packets.Delete.deleteSinglePlayerAtAllClients(uuid);
		}
	}

	public static void encode(MessageUpdateSinglePlayerQuestProgress packet, FriendlyByteBuf buffer){
		if(ProgressHelper.doesPlayerExist(packet.uuid) && QuestHelper.doesQuestExist(packet.questId) && packet.jsonObject != null){
			buffer.writeUUID(packet.uuid);
			buffer.writeInt(packet.questId);
			buffer.writeUtf(QuestingStorage.getSidedPlayersMap().get(packet.uuid.toString()).getIndividualProgress().get(packet.questId).getJson().toString());
		}
	}
	
	public static MessageUpdateSinglePlayerQuestProgress decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(22)){//16 for uuid, 4 for int, 2+ for json
			return new MessageUpdateSinglePlayerQuestProgress(buffer.readUUID(), buffer.readInt(), JsonParser.parseString(buffer.readUtf()).getAsJsonObject());
		}
		return null;
	}
	
	public static void handle(final MessageUpdateSinglePlayerQuestProgress message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if(message!=null){
				EditorClientProcessor.Update.Players.Progress.updateSingleQuestingPlayer(message.uuid, message.questId, message.jsonObject);
				ClientUtils.reloadMainGuiIfOpen();
				ClientUtils.reloadEditorIfOpen();
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}