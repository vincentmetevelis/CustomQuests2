package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.api.QuestingStorage;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageClearQuests{
    public static void encode(MessageClearQuests packet, PacketBuffer buffer){}
    
    public static MessageClearQuests decode(PacketBuffer buffer) {
        return new MessageClearQuests();
    }
    
    public static void handle(final MessageClearQuests message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            QuestingStorage.getSidedQuestsMap().clear();
        });
        ctx.get().setPacketHandled(true);
    }
}