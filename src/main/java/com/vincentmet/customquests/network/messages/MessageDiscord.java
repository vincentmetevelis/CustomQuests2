package com.vincentmet.customquests.network.messages;

import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageDiscord{
	public static void encode(MessageDiscord packet, PacketBuffer buffer){ }
	public static MessageDiscord decode(PacketBuffer buffer) {
		return new MessageDiscord();
	}
	
	public static void handle(final MessageDiscord message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> Util.getOSType().openURI("https://discord.gg/TmgVdAb"));
		ctx.get().setPacketHandled(true);
	}
}
