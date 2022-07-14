package com.vincentmet.customquests.network.messages.sync.stc.update;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.api.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

import static com.vincentmet.customquests.Ref.CustomQuests.LOGGER;

public class MessageStcSyncUpdateSinglePlayer {
	private final UUID uuid;
	private JsonObject jsonObject;

	private MessageStcSyncUpdateSinglePlayer(UUID uuid, JsonObject jsonObject){
		this.uuid = uuid;
		this.jsonObject = jsonObject;
	}

	public MessageStcSyncUpdateSinglePlayer(UUID uuid){
		this.uuid = uuid;
		if(ProgressHelper.doesPlayerExist(uuid)){
			jsonObject = QuestingStorage.getSidedPlayersMap().get(uuid.toString()).getJson();
		}else{
			ServerUtils.Packets.Delete.deleteSinglePlayerAtAllClients(uuid);
		}
	}

	public static void encode(MessageStcSyncUpdateSinglePlayer packet, FriendlyByteBuf buffer){
		if(ProgressHelper.doesPlayerExist(packet.uuid) && packet.jsonObject != null){
			buffer.writeUUID(packet.uuid);
			buffer.writeUtf(packet.jsonObject.toString());
		}
	}
	
	public static MessageStcSyncUpdateSinglePlayer decode(FriendlyByteBuf buffer){
		if(buffer.isReadable(18)){//16 for uuid, 2+ for json
			return new MessageStcSyncUpdateSinglePlayer(buffer.readUUID(), JsonParser.parseString(buffer.readUtf()).getAsJsonObject());
		}
		return null;
	}
	
	public static void handle(final MessageStcSyncUpdateSinglePlayer message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if(message!=null){
				EditorClientProcessor.Update.Players.updateSinglePlayer(message.uuid, message.jsonObject);
				ClientUtils.reloadMainGuiIfOpen();
				ClientUtils.reloadEditorIfOpen();
				LOGGER.info("Player " + message.uuid.toString() + " synced!");
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}
