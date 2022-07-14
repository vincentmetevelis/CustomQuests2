package com.vincentmet.customquests.network.messages.sync.stc.clear;

import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncTempClearSingleReward {
    private final int questId;
    private final int rewardId;

    public MessageStcSyncTempClearSingleReward(int questId, int rewardId){
        this.questId = questId;
        this.rewardId = rewardId;
    }
    
    public static void encode(MessageStcSyncTempClearSingleReward packet, FriendlyByteBuf buffer){
        buffer.writeInt(packet.questId);
        buffer.writeInt(packet.rewardId);
    }
    
    public static MessageStcSyncTempClearSingleReward decode(FriendlyByteBuf buffer) {
        if (buffer.isReadable(8)){
            return new MessageStcSyncTempClearSingleReward(buffer.readInt(), buffer.readInt());
        }
        return null;
    }
    
    public static void handle(final MessageStcSyncTempClearSingleReward message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (message != null){
                EditorClientProcessor.Clear.Quests.Rewards.clearSingleReward(message.questId, message.rewardId);
            }
        }).thenRun(() -> ctx.get().setPacketHandled(true));
    }
}