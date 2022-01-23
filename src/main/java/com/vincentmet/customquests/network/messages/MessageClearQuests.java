package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.QuestingStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageClearQuests{
    public static void encode(MessageClearQuests packet, FriendlyByteBuf buffer){}
    
    public static MessageClearQuests decode(FriendlyByteBuf buffer) {
        return new MessageClearQuests();
    }
    
    public static void handle(final MessageClearQuests message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            QuestingStorage.getSidedQuestsMap().clear();
            ClientUtils.reloadEditorIfOpen();
        });
        ctx.get().setPacketHandled(true);
    }
}