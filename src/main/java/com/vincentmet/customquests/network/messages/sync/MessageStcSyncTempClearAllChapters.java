package com.vincentmet.customquests.network.messages.sync;

import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncTempClearAllChapters {
    public static void encode(MessageStcSyncTempClearAllChapters packet, FriendlyByteBuf buffer){}
    
    public static MessageStcSyncTempClearAllChapters decode(FriendlyByteBuf buffer) {
        return new MessageStcSyncTempClearAllChapters();
    }
    
    public static void handle(final MessageStcSyncTempClearAllChapters message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(EditorClientProcessor.Clear.Chapters::clearAllChapters).thenRun(() -> ctx.get().setPacketHandled(true));
    }
}