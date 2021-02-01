package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.api.QuestingStorage;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageClearParties{
    public static void encode(MessageClearParties packet, PacketBuffer buffer){}
    
    public static MessageClearParties decode(PacketBuffer buffer) {
        return new MessageClearParties();
    }
    
    public static void handle(final MessageClearParties message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            QuestingStorage.getSidedPartiesMap().clear();
        });
        ctx.get().setPacketHandled(true);
    }
}