package com.vincentmet.customquests.network.messages.sync.stc.update;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.api.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static com.vincentmet.customquests.Ref.CustomQuests.LOGGER;

public class MessageStcSyncUpdateSingleParty {
	private final int partyId;
	private JsonObject jsonObject;

	private MessageStcSyncUpdateSingleParty(int partyId, JsonObject jsonObject){
		this.partyId = partyId;
		this.jsonObject = jsonObject;
	}

	public MessageStcSyncUpdateSingleParty(int partyId){
		this.partyId = partyId;
		if(PartyHelper.doesPartyExist(partyId)){
			jsonObject = QuestingStorage.getSidedPartiesMap().get(partyId).getJson();
		}else{
			ServerUtils.Packets.Delete.deleteSinglePartyAtAllClients(partyId);
		}
	}

	public static void encode(MessageStcSyncUpdateSingleParty packet, FriendlyByteBuf buffer){
		if(PartyHelper.doesPartyExist(packet.partyId) && packet.jsonObject != null){
			buffer.writeInt(packet.partyId);
			buffer.writeUtf(packet.jsonObject.toString());
		}
	}
	
	public static MessageStcSyncUpdateSingleParty decode(FriendlyByteBuf buffer){
		if(buffer.isReadable(6)){//4 for int, 2+ for json
			return new MessageStcSyncUpdateSingleParty(buffer.readInt(), JsonParser.parseString(buffer.readUtf()).getAsJsonObject());
		}
		return null;
	}
	
	public static void handle(final MessageStcSyncUpdateSingleParty message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if(message!=null){
				EditorClientProcessor.Update.Parties.updateSingleParty(message.partyId, message.jsonObject);
				ClientUtils.reloadMainGuiIfOpen();
				ClientUtils.reloadEditorIfOpen();
				LOGGER.info("Party " + message.partyId + " synced!");
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
