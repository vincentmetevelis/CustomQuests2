package com.vincentmet.customquests.network.messages.sync;

import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncTempClearAllParties {
    public static void encode(MessageStcSyncTempClearAllParties packet, FriendlyByteBuf buffer){}
    
    public static MessageStcSyncTempClearAllParties decode(FriendlyByteBuf buffer) {
        return new MessageStcSyncTempClearAllParties();
    }
    
    public static void handle(final MessageStcSyncTempClearAllParties message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(EditorClientProcessor.Clear.Parties::clearAllParties).thenRun(() -> ctx.get().setPacketHandled(true));
    }
}