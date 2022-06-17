package com.vincentmet.customquests.network.messages.command;

import com.vincentmet.customquests.Config;
import com.vincentmet.customquests.api.ClientUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageOpenEditor{
	public static void encode(MessageOpenEditor packet, FriendlyByteBuf buffer){ }
	public static MessageOpenEditor decode(FriendlyByteBuf buffer) {
		return new MessageOpenEditor();
	}
	
	public static void handle(final MessageOpenEditor message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (Config.SidedConfig.isEditModeOn()){
				ClientUtils.openEditorScreen();
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
