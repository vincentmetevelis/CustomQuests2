package com.vincentmet.customquests.network.messages.sync.stc.update;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.api.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncUpdateSingleTask {
	private final int questId;
	private final int taskId;
	private JsonObject jsonObject;

	private MessageStcSyncUpdateSingleTask(int questId, int taskId, JsonObject jsonObject){
		this.questId = questId;
		this.taskId = taskId;
		this.jsonObject = jsonObject;
	}

	public MessageStcSyncUpdateSingleTask(int questId, int taskId){
		this.questId = questId;
		this.taskId = taskId;
		if(QuestHelper.doesTaskExist(questId, taskId)){
			jsonObject = QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getJson();
		}else{
			ServerUtils.Packets.Delete.deleteSingleTaskAtAllClients(questId, taskId);
		}
	}

	public static void encode(MessageStcSyncUpdateSingleTask packet, FriendlyByteBuf buffer){
		if(QuestHelper.doesTaskExist(packet.questId, packet.taskId) && packet.jsonObject != null){
			buffer.writeInt(packet.questId);
			buffer.writeInt(packet.taskId);
			buffer.writeUtf(packet.jsonObject.toString());
		}
	}
	
	public static MessageStcSyncUpdateSingleTask decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(10)){//4 for int, 4 for int, 2+ for json
			return new MessageStcSyncUpdateSingleTask(buffer.readInt(), buffer.readInt(), JsonParser.parseString(buffer.readUtf()).getAsJsonObject());
		}
		return null;
	}
	
	public static void handle(final MessageStcSyncUpdateSingleTask message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if(message!=null){
				EditorClientProcessor.Update.Quests.updateSingleTask(message.questId, message.taskId, message.jsonObject);
				ClientUtils.reloadMainGuiIfOpen();
				ClientUtils.reloadEditorIfOpen();
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
