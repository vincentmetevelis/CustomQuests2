package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.QuestingStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageClearParties{
    public static void encode(MessageClearParties packet, FriendlyByteBuf buffer){}
    
    public static MessageClearParties decode(FriendlyByteBuf buffer) {
        return new MessageClearParties();
    }
    
    public static void handle(final MessageClearParties message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            QuestingStorage.getSidedPartiesMap().clear();
            ClientUtils.reloadEditorIfOpen();
        });
        ctx.get().setPacketHandled(true);
    }
}