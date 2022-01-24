package com.vincentmet.customquests.network.messages;

import com.google.gson.*;
import com.vincentmet.customquests.api.QuestingStorage;
import com.vincentmet.customquests.gui.QuestingScreen;
import com.vincentmet.customquests.hierarchy.progress.QuestingPlayer;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import static com.vincentmet.customquests.Ref.CustomQuests.LOGGER;

public class MessageUpdateSinglePlayer{
	public JsonObject json;//used for receiving packet only
	public String uuid;
	
	public MessageUpdateSinglePlayer(String uuid){
		this.uuid = uuid;
	}
	
	public MessageUpdateSinglePlayer(String uuid, JsonObject json){//used for receiving packet only
		this.uuid = uuid;
		this.json = json;
	}
	
	public static void encode(MessageUpdateSinglePlayer packet, PacketBuffer buffer){
		buffer.writeUtf(packet.uuid);
		buffer.writeUtf(QuestingStorage.getSidedPlayersMap().get(packet.uuid).getJson().toString());
	}
	
	public static MessageUpdateSinglePlayer decode(PacketBuffer buffer) {
		String uuid = buffer.readUtf();
		String data = buffer.readUtf();
		JsonObject json = new JsonParser().parse(data).getAsJsonObject();
		return new MessageUpdateSinglePlayer(uuid, json);
	}
	
	public static void handle(final MessageUpdateSinglePlayer message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			try{
				String uuid = message.uuid;
				JsonObject data = message.json;
				QuestingPlayer p = new QuestingPlayer(UUID.fromString(uuid));
				p.processJson(data);
				QuestingStorage.getSidedPlayersMap().put(uuid, p);
				LOGGER.info("User " + uuid + " synced!");
			}catch(NumberFormatException exception){
				LOGGER.error("User " + message.uuid + " should be a uuid");
				exception.printStackTrace();
			}
			Screen currentScreen = Minecraft.getInstance().screen;
			if(currentScreen instanceof QuestingScreen){
				((QuestingScreen)currentScreen).requestPosRecalc();
				((QuestingScreen)currentScreen).reInit();
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
