package com.vincentmet.customquests.network.messages;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.QuestingStorage;
import com.vincentmet.customquests.hierarchy.quest.Quest;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static com.vincentmet.customquests.Ref.CustomQuests.LOGGER;

public class MessageUpdateSingleQuest{
	public JsonObject json;//used for receiving packet only
	public int questId;
	
	public MessageUpdateSingleQuest(int questId){
		this.questId = questId;
	}
	
	public MessageUpdateSingleQuest(int questId, JsonObject json){
		this.questId = questId;
		this.json = json;
	}
	
	public static void encode(MessageUpdateSingleQuest packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeUtf(QuestingStorage.getSidedQuestsMap().get(packet.questId).getJson().toString());
	}
	
	public static MessageUpdateSingleQuest decode(FriendlyByteBuf buffer) {
		int questId = buffer.readInt();
		String stringJson = buffer.readUtf();
		JsonObject json = JsonParser.parseString(stringJson).getAsJsonObject();
		return new MessageUpdateSingleQuest(questId, json);
	}
	
	public static void handle(final MessageUpdateSingleQuest message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			int id = message.questId;
			JsonObject data = message.json;
			Quest q = new Quest(id);
			q.processJson(data);
			QuestingStorage.getSidedQuestsMap().put(id, q);
			ClientUtils.reloadEditorIfOpen();
			LOGGER.info("Quest " + id + " synced!");
		});
		ctx.get().setPacketHandled(true);
	}
}
