package com.vincentmet.customquests.network.messages.sync.stc.clear;

import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncTempClearSingleSubtask {
    private final int questId;
    private final int taskId;
    private final int subtaskId;

    public MessageStcSyncTempClearSingleSubtask(int questId, int taskId, int subtaskId){
        this.questId = questId;
        this.taskId = taskId;
        this.subtaskId = subtaskId;
    }
    
    public static void encode(MessageStcSyncTempClearSingleSubtask packet, FriendlyByteBuf buffer){
        buffer.writeInt(packet.questId);
        buffer.writeInt(packet.taskId);
        buffer.writeInt(packet.subtaskId);
    }
    
    public static MessageStcSyncTempClearSingleSubtask decode(FriendlyByteBuf buffer) {
        if (buffer.isReadable(12)){
            return new MessageStcSyncTempClearSingleSubtask(buffer.readInt(), buffer.readInt(), buffer.readInt());
        }
        return null;
    }
    
    public static void handle(final MessageStcSyncTempClearSingleSubtask message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (message != null){
                EditorClientProcessor.Clear.Quests.Tasks.Subtasks.clearSingleSubtask(message.questId, message.taskId, message.subtaskId);
            }
        }).thenRun(() -> ctx.get().setPacketHandled(true));
    }
}