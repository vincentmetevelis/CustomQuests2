package com.vincentmet.customquests.network.messages.sync;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncDeleteSingleSubreward {
    private final int questId;
    private final int taskId;
    private final int subrewardId;

    public MessageStcSyncDeleteSingleSubreward(int questId, int taskId, int subrewardId){
        this.questId = questId;
        this.taskId = taskId;
        this.subrewardId = subrewardId;
    }
    
    public static void encode(MessageStcSyncDeleteSingleSubreward packet, FriendlyByteBuf buffer){
        buffer.writeInt(packet.questId);
        buffer.writeInt(packet.taskId);
        buffer.writeInt(packet.subrewardId);
    }
    
    public static MessageStcSyncDeleteSingleSubreward decode(FriendlyByteBuf buffer) {
        if(buffer.isReadable(12)){
            return new MessageStcSyncDeleteSingleSubreward(buffer.readInt(), buffer.readInt(), buffer.readInt());
        }
        return null;
    }
    
    public static void handle(final MessageStcSyncDeleteSingleSubreward message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(message!=null){
                EditorClientProcessor.Delete.deleteSingleSubreward(message.questId, message.taskId, message.subrewardId);
                ClientUtils.reloadMainGuiIfOpen();
                ClientUtils.reloadEditorIfOpen();
            }
        }).thenRun(()->ctx.get().setPacketHandled(true));
    }
}