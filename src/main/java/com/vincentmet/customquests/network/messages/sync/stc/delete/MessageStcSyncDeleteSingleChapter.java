package com.vincentmet.customquests.network.messages.sync.stc.delete;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncDeleteSingleChapter {
    private final int chapterId;

    public MessageStcSyncDeleteSingleChapter(int chapterId){
        this.chapterId = chapterId;
    }
    
    public static void encode(MessageStcSyncDeleteSingleChapter packet, FriendlyByteBuf buffer){
        buffer.writeInt(packet.chapterId);
    }
    
    public static MessageStcSyncDeleteSingleChapter decode(FriendlyByteBuf buffer) {
        if(buffer.isReadable(4)){
            return new MessageStcSyncDeleteSingleChapter(buffer.readInt());
        }
        return null;
    }
    
    public static void handle(final MessageStcSyncDeleteSingleChapter message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(message!=null){
                EditorClientProcessor.Delete.deleteSingleChapter(message.chapterId);
                ClientUtils.reloadMainGuiIfOpen();
                ClientUtils.reloadEditorIfOpen();
            }
        }).thenRun(()->ctx.get().setPacketHandled(true));
    }
}