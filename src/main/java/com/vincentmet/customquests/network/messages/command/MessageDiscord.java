package com.vincentmet.customquests.network.messages.command;

import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageDiscord{
	public static void encode(MessageDiscord packet, FriendlyByteBuf buffer){ }
	public static MessageDiscord decode(FriendlyByteBuf buffer) {
		return new MessageDiscord();
	}
	
	public static void handle(final MessageDiscord message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> Util.getPlatform().openUri("https://discord.gg/TmgVdAb"));
		ctx.get().setPacketHandled(true);
	}
}
