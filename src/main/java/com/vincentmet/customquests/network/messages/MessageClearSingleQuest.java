package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.QuestingStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageClearSingleQuest{
    private int questId;
    
    public MessageClearSingleQuest(int questId){
        this.questId = questId;
    }
    
    public static void encode(MessageClearSingleQuest packet, FriendlyByteBuf buffer){
        buffer.writeInt(packet.questId);
    }
    
    public static MessageClearSingleQuest decode(FriendlyByteBuf buffer) {
        return new MessageClearSingleQuest(buffer.readInt());
    }
    
    public static void handle(final MessageClearSingleQuest message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            QuestingStorage.getSidedQuestsMap().remove(message.questId);
            ClientUtils.reloadEditorIfOpen();
        });
        ctx.get().setPacketHandled(true);
    }
}