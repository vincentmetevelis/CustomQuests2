package com.vincentmet.customquests.network.messages.sync;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncDeleteAllChapters {
    public static void encode(MessageStcSyncDeleteAllChapters packet, FriendlyByteBuf buffer){}
    
    public static MessageStcSyncDeleteAllChapters decode(FriendlyByteBuf buffer) {
        return new MessageStcSyncDeleteAllChapters();
    }
    
    public static void handle(final MessageStcSyncDeleteAllChapters message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            EditorClientProcessor.Delete.deleteAllChapters();
            ClientUtils.reloadMainGuiIfOpen();
            ClientUtils.reloadEditorIfOpen();
        }).thenRun(() -> ctx.get().setPacketHandled(true));
    }
}