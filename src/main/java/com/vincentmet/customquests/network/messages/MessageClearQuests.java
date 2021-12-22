package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.api.QuestingStorage;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class MessageClearQuests{
    public static void encode(MessageClearQuests packet, FriendlyByteBuf buffer){}
    
    public static MessageClearQuests decode(FriendlyByteBuf buffer) {
        return new MessageClearQuests();
    }
    
    public static void handle(final MessageClearQuests message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            QuestingStorage.getSidedQuestsMap().clear();
        });
        ctx.get().setPacketHandled(true);
    }
}