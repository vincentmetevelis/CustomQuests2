package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.api.QuestingStorage;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class MessageClearChapters{
    public static void encode(MessageClearChapters packet, FriendlyByteBuf buffer){}
    
    public static MessageClearChapters decode(FriendlyByteBuf buffer) {
        return new MessageClearChapters();
    }
    
    public static void handle(final MessageClearChapters message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            QuestingStorage.getSidedChaptersMap().clear();
        });
        ctx.get().setPacketHandled(true);
    }
}