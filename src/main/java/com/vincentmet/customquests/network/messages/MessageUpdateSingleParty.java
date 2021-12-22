package com.vincentmet.customquests.network.messages;

import com.google.gson.*;
import com.vincentmet.customquests.api.QuestingStorage;
import com.vincentmet.customquests.gui.QuestingScreen;
import com.vincentmet.customquests.hierarchy.party.Party;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import static com.vincentmet.customquests.Ref.CustomQuests.LOGGER;

public class MessageUpdateSingleParty{
	public JsonObject json;//used for receiving packet only
	public int partyId;
	
	public MessageUpdateSingleParty(int partyId){
		this.partyId = partyId;
	}
	
	public MessageUpdateSingleParty(int partyId, JsonObject json){
		this.partyId = partyId;
		this.json = json;
	}
	
	public static void encode(MessageUpdateSingleParty packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.partyId);
		buffer.writeUtf(QuestingStorage.getSidedPartiesMap().get(packet.partyId).getJson().toString());
	}
	
	public static MessageUpdateSingleParty decode(FriendlyByteBuf buffer) {
		int partyId = buffer.readInt();
		String stringJson = buffer.readUtf();
		JsonObject json = new JsonParser().parse(stringJson).getAsJsonObject();
		return new MessageUpdateSingleParty(partyId, json);
	}
	
	public static void handle(final MessageUpdateSingleParty message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			try{
				int id = message.partyId;
				JsonObject data = message.json;
				Party p = new Party(id);
				p.processJson(data);
				QuestingStorage.getSidedPartiesMap().put(id, p);
				LOGGER.info("Party " + id + " synced!");
			}catch(NumberFormatException exception){
				LOGGER.error("Party " + message.partyId + " should be a numeric id");
				exception.printStackTrace();
			}
			Screen currentScreen = Minecraft.getInstance().screen;
			if(currentScreen instanceof QuestingScreen){
				((QuestingScreen)currentScreen).requestPosRecalc();
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
