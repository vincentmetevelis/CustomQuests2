package com.vincentmet.customquests.network.messages.sync.stc.delete;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncDeleteSingleSubtask {
    private final int questId;
    private final int taskId;
    private final int subtaskId;

    public MessageStcSyncDeleteSingleSubtask(int questId, int taskId, int subtaskId){
        this.questId = questId;
        this.taskId = taskId;
        this.subtaskId = subtaskId;
    }
    
    public static void encode(MessageStcSyncDeleteSingleSubtask packet, FriendlyByteBuf buffer){
        buffer.writeInt(packet.questId);
        buffer.writeInt(packet.taskId);
        buffer.writeInt(packet.subtaskId);
    }
    
    public static MessageStcSyncDeleteSingleSubtask decode(FriendlyByteBuf buffer) {
        if(buffer.isReadable(12)){
            return new MessageStcSyncDeleteSingleSubtask(buffer.readInt(), buffer.readInt(), buffer.readInt());
        }
        return null;
    }
    
    public static void handle(final MessageStcSyncDeleteSingleSubtask message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(message!=null){
                EditorClientProcessor.Delete.deleteSingleSubtask(message.questId, message.taskId, message.subtaskId);
                ClientUtils.reloadMainGuiIfOpen();
                ClientUtils.reloadEditorIfOpen();
            }
        }).thenRun(()->ctx.get().setPacketHandled(true));
    }
}