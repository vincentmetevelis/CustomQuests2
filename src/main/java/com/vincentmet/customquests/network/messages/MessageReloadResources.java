package com.vincentmet.customquests.network.messages;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageReloadResources{
	public static void encode(MessageReloadResources packet, PacketBuffer buffer){}
	
	public static MessageReloadResources decode(PacketBuffer buffer) {
		return new MessageReloadResources();
	}
	
	public static void handle(final MessageReloadResources message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			IResourceManager rm = Minecraft.getInstance().getResourceManager();
			if(rm instanceof IReloadableResourceManager){
				((IReloadableResourceManager)rm).reloadResources(Util.getServerExecutor(), Minecraft.getInstance(), CompletableFuture.completedFuture(Unit.INSTANCE), Minecraft.getInstance().getResourcePackList().getEnabledPacks().stream().map(ResourcePackInfo::getResourcePack).collect(Collectors.toList()));
			}
		});
		ctx.get().setPacketHandled(true);
	}
}