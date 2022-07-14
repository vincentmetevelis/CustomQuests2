package com.vincentmet.customquests.network.messages.sync.stc.clear;

import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncTempClearAllPlayers {
    public static void encode(MessageStcSyncTempClearAllPlayers packet, FriendlyByteBuf buffer){}
    
    public static MessageStcSyncTempClearAllPlayers decode(FriendlyByteBuf buffer) {
        return new MessageStcSyncTempClearAllPlayers();
    }
    
    public static void handle(final MessageStcSyncTempClearAllPlayers message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(EditorClientProcessor.Clear.Players::clearAllPlayers).thenRun(() -> ctx.get().setPacketHandled(true));
    }
}