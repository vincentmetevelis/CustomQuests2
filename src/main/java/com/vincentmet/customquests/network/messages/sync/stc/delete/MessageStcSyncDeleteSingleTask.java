package com.vincentmet.customquests.network.messages.sync.stc.delete;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncDeleteSingleTask {
    private final int questId;
    private final int taskId;

    public MessageStcSyncDeleteSingleTask(int questId, int taskId){
        this.questId = questId;
        this.taskId = taskId;
    }
    
    public static void encode(MessageStcSyncDeleteSingleTask packet, FriendlyByteBuf buffer){
        buffer.writeInt(packet.questId);
        buffer.writeInt(packet.taskId);
    }
    
    public static MessageStcSyncDeleteSingleTask decode(FriendlyByteBuf buffer) {
        if(buffer.isReadable(8)){
            return new MessageStcSyncDeleteSingleTask(buffer.readInt(), buffer.readInt());
        }
        return null;
    }
    
    public static void handle(final MessageStcSyncDeleteSingleTask message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(message!=null){
                EditorClientProcessor.Delete.deleteSingleTask(message.questId, message.taskId);
                ClientUtils.reloadMainGuiIfOpen();
                ClientUtils.reloadEditorIfOpen();
            }
        }).thenRun(()->ctx.get().setPacketHandled(true));
    }
}