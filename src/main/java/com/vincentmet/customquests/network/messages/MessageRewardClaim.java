package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.api.CombinedProgressHelper;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageRewardClaim{
	public int questId;
	public int rewardId;
	
	public MessageRewardClaim(int questId, int rewardId){
		this.questId = questId;
		this.rewardId = rewardId;
	}
	
	public static void encode(MessageRewardClaim packet, PacketBuffer buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.rewardId);
	}
	
	public static MessageRewardClaim decode(PacketBuffer buffer) {
		if(buffer.readableBytes() >= 8){//2 ints
			int questID = buffer.readInt();
			int rewardID = buffer.readInt();
			return new MessageRewardClaim(questID, rewardID);
		}
		return null;
	}
	
	public static void handle(final MessageRewardClaim message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if(message!=null){
				CombinedProgressHelper.claimReward(Objects.requireNonNull(ctx.get().getSender()).getUniqueID(), message.questId, message.rewardId);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
