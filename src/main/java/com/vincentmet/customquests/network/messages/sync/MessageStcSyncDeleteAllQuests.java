package com.vincentmet.customquests.network.messages.sync;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncDeleteAllQuests {
    public static void encode(MessageStcSyncDeleteAllQuests packet, FriendlyByteBuf buffer){}
    
    public static MessageStcSyncDeleteAllQuests decode(FriendlyByteBuf buffer) {
        return new MessageStcSyncDeleteAllQuests();
    }
    
    public static void handle(final MessageStcSyncDeleteAllQuests message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            EditorClientProcessor.Delete.deleteAllQuests();
            ClientUtils.reloadMainGuiIfOpen();
            ClientUtils.reloadEditorIfOpen();
        }).thenRun(() -> ctx.get().setPacketHandled(true));
    }
}