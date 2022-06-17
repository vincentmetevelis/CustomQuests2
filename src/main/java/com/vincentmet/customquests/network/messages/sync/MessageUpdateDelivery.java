package com.vincentmet.customquests.network.messages.sync;

import com.vincentmet.customquests.ItemStackHandlerCapability;
import com.vincentmet.customquests.tileentity.DeliveryBlockTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageUpdateDelivery{
	public BlockPos pos;
	public int quest;
	public int task;
	public int subtask;
	
	public MessageUpdateDelivery(BlockPos pos, int quest, int task, int subtask){
		this.pos = pos;
		this.quest = quest;
		this.task = task;
		this.subtask = subtask;
	}
	
	public static void encode(MessageUpdateDelivery packet, FriendlyByteBuf buffer){
		buffer.writeBlockPos(packet.pos);//8
		buffer.writeInt(packet.quest);//4
		buffer.writeInt(packet.task);//4
		buffer.writeInt(packet.subtask);//4
	}
	
	public static MessageUpdateDelivery decode(FriendlyByteBuf buffer) {
		if(buffer.readableBytes() == 20){
			return new MessageUpdateDelivery(buffer.readBlockPos(), buffer.readInt(), buffer.readInt(), buffer.readInt());
		}
		return null;
	}
	
	public static void handle(final MessageUpdateDelivery message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if(message!=null && message.pos!=null){
				ServerPlayer serverPlayerEntity = ctx.get().getSender();
				if(serverPlayerEntity!=null){
					BlockEntity te = serverPlayerEntity.level.getBlockEntity(message.pos);
					if(te instanceof DeliveryBlockTileEntity){
						DeliveryBlockTileEntity dbte = (DeliveryBlockTileEntity)te;//todo DELIVERYBLOCK add player in range check here or add some owner tag
						ItemStackHandlerCapability ishc = dbte.getItemHandler();
						if(ishc!=null){
							dbte.setCurrentSubmitter(serverPlayerEntity);
							ishc.setActiveSubtask(message.quest, message.task, message.subtask);
						}
					}
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
