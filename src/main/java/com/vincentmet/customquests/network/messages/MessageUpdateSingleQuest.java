package com.vincentmet.customquests.network.messages;

import com.google.gson.*;
import com.vincentmet.customquests.api.QuestingStorage;
import com.vincentmet.customquests.gui.QuestingScreen;
import com.vincentmet.customquests.hierarchy.quest.Quest;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
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
	
	public static void encode(MessageUpdateSingleQuest packet, PacketBuffer buffer){
		buffer.writeInt(packet.questId);
		buffer.writeString(QuestingStorage.getSidedQuestsMap().get(packet.questId).getJson().toString());
	}
	
	public static MessageUpdateSingleQuest decode(PacketBuffer buffer) {
		int questId = buffer.readInt();
		String stringJson = buffer.readString();
		JsonObject json = new JsonParser().parse(stringJson).getAsJsonObject();
		return new MessageUpdateSingleQuest(questId, json);
	}
	
	public static void handle(final MessageUpdateSingleQuest message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			try{
				int id = message.questId;
				JsonObject data = message.json;
				Quest q = new Quest(id);
				q.processJson(data);
				QuestingStorage.getSidedQuestsMap().put(id, q);
				LOGGER.info("Quest " + id + " synced!");
			}catch(NumberFormatException exception){
				LOGGER.error("Quest " + message.questId + " should be a numeric id");
				exception.printStackTrace();
			}
			Screen currentScreen = Minecraft.getInstance().currentScreen;
			if(currentScreen instanceof QuestingScreen){
				((QuestingScreen)currentScreen).requestPosRecalc();
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
