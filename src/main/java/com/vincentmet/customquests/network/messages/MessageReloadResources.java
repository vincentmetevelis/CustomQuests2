package com.vincentmet.customquests.network.messages;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraftforge.network.NetworkEvent;

import net.minecraft.Util;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

public class MessageReloadResources{
	public static void encode(MessageReloadResources packet, FriendlyByteBuf buffer){}
	
	public static MessageReloadResources decode(FriendlyByteBuf buffer) {
		return new MessageReloadResources();
	}
	
	public static void handle(final MessageReloadResources message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ResourceManager rm = Minecraft.getInstance().getResourceManager();
			if(rm instanceof ReloadableResourceManager){
				((ReloadableResourceManager)rm).createReload(Util.backgroundExecutor(), Minecraft.getInstance(), CompletableFuture.completedFuture(Unit.INSTANCE), Minecraft.getInstance().getResourcePackRepository().getSelectedPacks().stream().map(Pack::open).collect(Collectors.toList()));
			}
		});
		ctx.get().setPacketHandled(true);
	}
}