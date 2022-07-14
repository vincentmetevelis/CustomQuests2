package com.vincentmet.customquests.network.messages.sync.stc.clear;

import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncTempClearSingleTask {
    private final int questId;
    private final int taskId;

    public MessageStcSyncTempClearSingleTask(int questId, int taskId){
        this.questId = questId;
        this.taskId = taskId;
    }
    
    public static void encode(MessageStcSyncTempClearSingleTask packet, FriendlyByteBuf buffer){
        buffer.writeInt(packet.questId);
        buffer.writeInt(packet.taskId);
    }
    
    public static MessageStcSyncTempClearSingleTask decode(FriendlyByteBuf buffer) {
        if (buffer.isReadable(8)){
            return new MessageStcSyncTempClearSingleTask(buffer.readInt(), buffer.readInt());
        }
        return null;
    }
    
    public static void handle(final MessageStcSyncTempClearSingleTask message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (message != null){
                EditorClientProcessor.Clear.Quests.Tasks.clearSingleTask(message.questId, message.taskId);
            }
        }).thenRun(() -> ctx.get().setPacketHandled(true));
    }
}