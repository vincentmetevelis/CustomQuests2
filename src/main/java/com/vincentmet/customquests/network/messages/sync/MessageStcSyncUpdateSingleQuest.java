package com.vincentmet.customquests.network.messages.sync;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.hierarchy.quest.Quest;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static com.vincentmet.customquests.Ref.CustomQuests.LOGGER;

public class MessageStcSyncUpdateSingleQuest {
	private final int questId;
	private JsonObject jsonObject;

	private MessageStcSyncUpdateSingleQuest(int questId, JsonObject jsonObject){
		this.questId = questId;
		this.jsonObject = jsonObject;
	}

	public MessageStcSyncUpdateSingleQuest(int questId){
		this.questId = questId;
		if(QuestHelper.doesQuestExist(questId)){
			jsonObject = QuestingStorage.getSidedQuestsMap().get(questId).getJson();
		}else{
			ServerUtils.Packets.Delete.deleteSingleQuestAtAllClients(questId);
		}
	}

	public static void encode(MessageStcSyncUpdateSingleQuest packet, FriendlyByteBuf buffer){
		if(QuestHelper.doesQuestExist(packet.questId) && packet.jsonObject != null){
			buffer.writeInt(packet.questId);
			buffer.writeUtf(packet.jsonObject.toString());
		}
	}
	
	public static MessageStcSyncUpdateSingleQuest decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(6)){//4 for int, 2+ for json
			return new MessageStcSyncUpdateSingleQuest(buffer.readInt(), JsonParser.parseString(buffer.readUtf()).getAsJsonObject());
		}
		return null;
	}
	
	public static void handle(final MessageStcSyncUpdateSingleQuest message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if(message!=null){
				EditorClientProcessor.Update.Quests.updateSingleQuest(message.questId, message.jsonObject);
				ClientUtils.reloadMainGuiIfOpen();
				ClientUtils.reloadEditorIfOpen();
				LOGGER.info("Quest " + message.questId + " synced!");
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
