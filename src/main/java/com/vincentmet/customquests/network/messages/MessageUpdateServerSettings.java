package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.Config;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageUpdateServerSettings{
	public boolean edit_mode;
	public boolean reward_claim_one_per_party;
	public boolean give_device_on_first_login;
	
	public MessageUpdateServerSettings(boolean edit_mode, boolean reward_claim_one_per_party, boolean give_device_on_first_login){
		this.edit_mode = edit_mode;
		this.reward_claim_one_per_party = reward_claim_one_per_party;
		this.give_device_on_first_login = give_device_on_first_login;
	}
	
	public static void encode(MessageUpdateServerSettings packet, FriendlyByteBuf buffer){
		buffer.writeBoolean(packet.edit_mode);
		buffer.writeBoolean(packet.reward_claim_one_per_party);
		buffer.writeBoolean(packet.give_device_on_first_login);
	}
	
	public static MessageUpdateServerSettings decode(FriendlyByteBuf buffer) {
		return new MessageUpdateServerSettings(buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
	}
	
	public static void handle(final MessageUpdateServerSettings message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Config.ServerToClientSyncedConfig.EDIT_MODE = message.edit_mode;
			Config.ServerToClientSyncedConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE = message.reward_claim_one_per_party;
			Config.ServerToClientSyncedConfig.GIVE_DEVICE_ON_FIRST_LOGIN = message.give_device_on_first_login;
		});
		ctx.get().setPacketHandled(true);
	}
}
