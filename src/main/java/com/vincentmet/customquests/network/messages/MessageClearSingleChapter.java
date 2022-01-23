package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.QuestingStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageClearSingleChapter{
    private final int chapterId;
    
    public MessageClearSingleChapter(int chapterId){
        this.chapterId = chapterId;
    }
    
    public static void encode(MessageClearSingleChapter packet, FriendlyByteBuf buffer){
        buffer.writeInt(packet.chapterId);
    }
    
    public static MessageClearSingleChapter decode(FriendlyByteBuf buffer) {
        return new MessageClearSingleChapter(buffer.readInt());
    }
    
    public static void handle(final MessageClearSingleChapter message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            QuestingStorage.getSidedChaptersMap().remove(message.chapterId);
            ClientUtils.reloadEditorIfOpen();
        });
        ctx.get().setPacketHandled(true);
    }
}