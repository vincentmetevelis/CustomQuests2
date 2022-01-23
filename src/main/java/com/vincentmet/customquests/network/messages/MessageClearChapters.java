package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.QuestingStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageClearChapters{
    public static void encode(MessageClearChapters packet, FriendlyByteBuf buffer){}
    
    public static MessageClearChapters decode(FriendlyByteBuf buffer) {
        return new MessageClearChapters();
    }
    
    public static void handle(final MessageClearChapters message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            QuestingStorage.getSidedChaptersMap().clear();
            ClientUtils.reloadEditorIfOpen();
        });
        ctx.get().setPacketHandled(true);
    }
}