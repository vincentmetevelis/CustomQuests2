package com.vincentmet.customquests.network.messages.sync;

import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncTempClearSingleSubreward {
    private final int questId;
    private final int rewardId;
    private final int subrewardId;

    public MessageStcSyncTempClearSingleSubreward(int questId, int rewardId, int subrewardId){
        this.questId = questId;
        this.rewardId = rewardId;
        this.subrewardId = subrewardId;
    }
    
    public static void encode(MessageStcSyncTempClearSingleSubreward packet, FriendlyByteBuf buffer){
        buffer.writeInt(packet.questId);
        buffer.writeInt(packet.rewardId);
        buffer.writeInt(packet.subrewardId);
    }
    
    public static MessageStcSyncTempClearSingleSubreward decode(FriendlyByteBuf buffer) {
        if (buffer.isReadable(12)){
            return new MessageStcSyncTempClearSingleSubreward(buffer.readInt(), buffer.readInt(), buffer.readInt());
        }
        return null;
    }
    
    public static void handle(final MessageStcSyncTempClearSingleSubreward message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (message != null){
                EditorClientProcessor.Clear.Quests.Rewards.Subrewards.clearSingleSubreward(message.questId, message.rewardId, message.subrewardId);
            }
        }).thenRun(() -> ctx.get().setPacketHandled(true));
    }
}