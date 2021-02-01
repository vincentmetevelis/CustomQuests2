package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.api.QuestingStorage;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageClearChapters{
    public static void encode(MessageClearChapters packet, PacketBuffer buffer){}
    
    public static MessageClearChapters decode(PacketBuffer buffer) {
        return new MessageClearChapters();
    }
    
    public static void handle(final MessageClearChapters message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            QuestingStorage.getSidedChaptersMap().clear();
        });
        ctx.get().setPacketHandled(true);
    }
}