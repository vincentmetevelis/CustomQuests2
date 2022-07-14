package com.vincentmet.customquests.network.messages.sync.stc.delete;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncDeleteSingleReward {
    private final int questId;
    private final int rewardId;

    public MessageStcSyncDeleteSingleReward(int questId, int rewardId){
        this.questId = questId;
        this.rewardId = rewardId;
    }
    
    public static void encode(MessageStcSyncDeleteSingleReward packet, FriendlyByteBuf buffer){
        buffer.writeInt(packet.questId);
        buffer.writeInt(packet.rewardId);
    }
    
    public static MessageStcSyncDeleteSingleReward decode(FriendlyByteBuf buffer) {
        if(buffer.isReadable(8)){
            return new MessageStcSyncDeleteSingleReward(buffer.readInt(), buffer.readInt());
        }
        return null;
    }
    
    public static void handle(final MessageStcSyncDeleteSingleReward message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(message!=null){
                EditorClientProcessor.Delete.deleteSingleReward(message.questId, message.rewardId);
                ClientUtils.reloadMainGuiIfOpen();
                ClientUtils.reloadEditorIfOpen();
            }
        }).thenRun(()->ctx.get().setPacketHandled(true));
    }
}