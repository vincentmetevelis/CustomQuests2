package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestSubtaskContent {
	private final int questId;
	private final int taskId;
	private final int subtaskId;
	private final JsonObject content;

	public MessageEditorRequestUpdateQuestSubtaskContent(int questId, int taskId, int subtaskId, JsonObject content){
		this.questId = questId;
		this.taskId = taskId;
		this.subtaskId = subtaskId;
		this.content = content;
	}

	public static void encode(MessageEditorRequestUpdateQuestSubtaskContent packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.taskId);
		buffer.writeInt(packet.subtaskId);
		buffer.writeUtf(packet.content.getAsString());
	}
	
	public static MessageEditorRequestUpdateQuestSubtaskContent decode(FriendlyByteBuf buffer){
		if(buffer.isReadable(14)){//3x4 for ints, 2+ for json string
			int questId = buffer.readInt();
			int taskId = buffer.readInt();
			int subtaskId = buffer.readInt();
			String content = buffer.readUtf();
			JsonElement jsonElement = JsonParser.parseString(content);
			JsonObject jsonObject = new JsonObject();
			if(jsonElement.isJsonObject()){
				jsonObject = jsonElement.getAsJsonObject();
			}
			return new MessageEditorRequestUpdateQuestSubtaskContent(questId, taskId, subtaskId, jsonObject);
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestSubtaskContent message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(message != null && sender != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesSubrewardExist(message.questId, message.taskId, message.subtaskId)){
					EditorServerProcessor.Update.Quest.Tasks.Task.Subtasks.Subtask.updateCustomContent(message.questId, message.taskId, message.subtaskId, message.content);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}