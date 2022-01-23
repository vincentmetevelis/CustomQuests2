package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.QuestingStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageClearPlayers{
    public static void encode(MessageClearPlayers packet, FriendlyByteBuf buffer){}
    
    public static MessageClearPlayers decode(FriendlyByteBuf buffer) {
        return new MessageClearPlayers();
    }
    
    public static void handle(final MessageClearPlayers message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            QuestingStorage.getSidedPlayersMap().clear();
            ClientUtils.reloadEditorIfOpen();
        });
        ctx.get().setPacketHandled(true);
    }
}