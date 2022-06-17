package com.vincentmet.customquests.network.messages.sync;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncDeleteSingleQuest {
    private final int questId;
    
    public MessageStcSyncDeleteSingleQuest(int questId){
        this.questId = questId;
    }
    
    public static void encode(MessageStcSyncDeleteSingleQuest packet, FriendlyByteBuf buffer){
        buffer.writeInt(packet.questId);
    }
    
    public static MessageStcSyncDeleteSingleQuest decode(FriendlyByteBuf buffer) {
        if(buffer.isReadable(4)){
            return new MessageStcSyncDeleteSingleQuest(buffer.readInt());
        }
        return null;
    }
    
    public static void handle(final MessageStcSyncDeleteSingleQuest message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(message!=null){
                EditorClientProcessor.Delete.deleteSingleQuest(message.questId);
                ClientUtils.reloadMainGuiIfOpen();
                ClientUtils.reloadEditorIfOpen();
            }
        }).thenRun(()->ctx.get().setPacketHandled(true));
    }
}