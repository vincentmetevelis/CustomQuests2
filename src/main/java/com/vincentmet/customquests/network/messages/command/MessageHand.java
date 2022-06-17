package com.vincentmet.customquests.network.messages.command;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.function.Supplier;

public class MessageHand {
	private ItemStack stack;

	public MessageHand(){}
	public MessageHand(ItemStack stack){
		this.stack = stack;
	}
	public static void encode(MessageHand packet, FriendlyByteBuf buffer){
		buffer.writeItemStack(packet.stack, false);
	}

	public static MessageHand decode(FriendlyByteBuf buffer) {
		return new MessageHand(buffer.readItem());
	}
	
	public static void handle(final MessageHand message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (message.stack.getTag()!=null){
				GLFW.glfwSetClipboardString(Minecraft.getInstance().getWindow().getWindow(), "{\"item\":"+message.stack.getItem().getRegistryName()+"\",\"count\":+stack.getCount()+,\"nbt\":\""+message.stack.getTag().toString()+"\"}");
			}else{
				GLFW.glfwSetClipboardString(Minecraft.getInstance().getWindow().getWindow(), "{\"item\":\""+message.stack.getItem().getRegistryName()+"\",\"count\":"+message.stack.getCount()+",\"nbt\":null}");
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
