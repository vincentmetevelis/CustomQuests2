package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.api.QuestingStorage;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageClearPlayers{
    public static void encode(MessageClearPlayers packet, PacketBuffer buffer){}
    
    public static MessageClearPlayers decode(PacketBuffer buffer) {
        return new MessageClearPlayers();
    }
    
    public static void handle(final MessageClearPlayers message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            QuestingStorage.getSidedPlayersMap().clear();
        });
        ctx.get().setPacketHandled(true);
    }
}