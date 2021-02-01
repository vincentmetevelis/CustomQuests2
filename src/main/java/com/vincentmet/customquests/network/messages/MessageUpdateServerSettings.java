package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.Config;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageUpdateServerSettings{
	public boolean edit_mode;
	public boolean reward_claim_one_per_party;
	
	public MessageUpdateServerSettings(boolean edit_mode, boolean reward_claim_one_per_party){
		this.edit_mode = edit_mode;
		this.reward_claim_one_per_party = reward_claim_one_per_party;
	}
	
	public static void encode(MessageUpdateServerSettings packet, PacketBuffer buffer){
		buffer.writeBoolean(packet.edit_mode);
		buffer.writeBoolean(packet.reward_claim_one_per_party);
	}
	
	public static MessageUpdateServerSettings decode(PacketBuffer buffer) {
		return new MessageUpdateServerSettings(buffer.readBoolean(), buffer.readBoolean());
	}
	
	public static void handle(final MessageUpdateServerSettings message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Config.ServerToClientSyncedConfig.EDIT_MODE = message.edit_mode;
			Config.ServerToClientSyncedConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE = message.reward_claim_one_per_party;
		});
		ctx.get().setPacketHandled(true);
	}
}
