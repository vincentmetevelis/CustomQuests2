package com.vincentmet.customquests.network.messages.sync;

import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncTempClearSingleQuest {
    private final int questId;

    public MessageStcSyncTempClearSingleQuest(int questId){
        this.questId = questId;
    }
    
    public static void encode(MessageStcSyncTempClearSingleQuest packet, FriendlyByteBuf buffer){
        buffer.writeInt(packet.questId);
    }
    
    public static MessageStcSyncTempClearSingleQuest decode(FriendlyByteBuf buffer) {
        if (buffer.isReadable(4)){
            return new MessageStcSyncTempClearSingleQuest(buffer.readInt());
        }
        return null;
    }
    
    public static void handle(final MessageStcSyncTempClearSingleQuest message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (message != null){
                EditorClientProcessor.Clear.Quests.clearSingleQuest(message.questId);
            }
        }).thenRun(() -> ctx.get().setPacketHandled(true));
    }
}