package com.vincentmet.customquests.network.messages.sync;

import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncTempClearAllQuests {
    public static void encode(MessageStcSyncTempClearAllQuests packet, FriendlyByteBuf buffer){}
    
    public static MessageStcSyncTempClearAllQuests decode(FriendlyByteBuf buffer) {
        return new MessageStcSyncTempClearAllQuests();
    }
    
    public static void handle(final MessageStcSyncTempClearAllQuests message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(EditorClientProcessor.Clear.Quests::clearAllQuests).thenRun(() -> ctx.get().setPacketHandled(true));
    }
}