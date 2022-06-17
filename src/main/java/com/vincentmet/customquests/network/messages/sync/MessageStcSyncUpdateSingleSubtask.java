package com.vincentmet.customquests.network.messages.sync;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.api.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncUpdateSingleSubtask {
	private final int questId;
	private final int taskId;
	private final int subtaskId;
	private JsonObject jsonObject;

	private MessageStcSyncUpdateSingleSubtask(int questId, int taskId, int subtaskId, JsonObject jsonObject){
		this.questId = questId;
		this.taskId = taskId;
		this.subtaskId = subtaskId;
		this.jsonObject = jsonObject;
	}

	public MessageStcSyncUpdateSingleSubtask(int questId, int taskId, int subtaskId){
		this.questId = questId;
		this.taskId = taskId;
		this.subtaskId = subtaskId;
		if(QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
			jsonObject = QuestingStorage.getSidedQuestsMap().get(questId).getTasks().get(taskId).getSubtasks().get(subtaskId).getJson();
		}else{
			ServerUtils.Packets.Delete.deleteSingleSubtaskAtAllClients(questId, taskId, subtaskId);
		}
	}

	public static void encode(MessageStcSyncUpdateSingleSubtask packet, FriendlyByteBuf buffer){
		if(QuestHelper.doesSubtaskExist(packet.questId, packet.taskId, packet.subtaskId) && packet.jsonObject != null){
			buffer.writeInt(packet.questId);
			buffer.writeInt(packet.taskId);
			buffer.writeInt(packet.subtaskId);
			buffer.writeUtf(packet.jsonObject.toString());
		}
	}
	
	public static MessageStcSyncUpdateSingleSubtask decode(FriendlyByteBuf buffer) {
		if(buffer.isReadable(14)){//4 for int, 4 for int, 4 for int, 2+ for json
			return new MessageStcSyncUpdateSingleSubtask(buffer.readInt(), buffer.readInt(), buffer.readInt(), JsonParser.parseString(buffer.readUtf()).getAsJsonObject());
		}
		return null;
	}
	
	public static void handle(final MessageStcSyncUpdateSingleSubtask message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if(message!=null){
				EditorClientProcessor.Update.Quests.updateSingleSubtask(message.questId, message.taskId, message.subtaskId, message.jsonObject);
				ClientUtils.reloadMainGuiIfOpen();
				ClientUtils.reloadEditorIfOpen();
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
