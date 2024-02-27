package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.IJsonObjectProcessor;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestSubrewardContent {
	private final int questId;
	private final int rewardId;
	private final int subrewardId;
	private final JsonObject content;

	public MessageEditorRequestUpdateQuestSubrewardContent(int questId, int rewardId, int subrewardId, JsonObject content){
		this.questId = questId;
		this.rewardId = rewardId;
		this.subrewardId = subrewardId;
		this.content = content;
	}

	public static void encode(MessageEditorRequestUpdateQuestSubrewardContent packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.rewardId);
		buffer.writeInt(packet.subrewardId);
		buffer.writeUtf(packet.content.getAsString());
	}
	
	public static MessageEditorRequestUpdateQuestSubrewardContent decode(FriendlyByteBuf buffer){
		if(buffer.isReadable(14)){//3x4 for ints, 2+ for json string
			int questId = buffer.readInt();
			int rewardId = buffer.readInt();
			int subrewardId = buffer.readInt();
			String content = buffer.readUtf();
			JsonElement jsonElement = JsonParser.parseString(content);
			JsonObject jsonObject = new JsonObject();
			if(jsonElement.isJsonObject()){
				jsonObject = jsonElement.getAsJsonObject();
			}
			return new MessageEditorRequestUpdateQuestSubrewardContent(questId, rewardId, subrewardId, jsonObject);
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestSubrewardContent message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(message != null && sender != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesSubrewardExist(message.questId, message.rewardId, message.subrewardId)){
					EditorServerProcessor.Update.Quest.Rewards.Reward.Subrewards.Subreward.updateSubrewardContent(message.questId, message.rewardId, message.subrewardId, message.content);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}