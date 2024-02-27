package com.vincentmet.customquests.network.messages.sync.stc.delete;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageStcSyncDeleteSingleParty {
    private final int partyId;

    public MessageStcSyncDeleteSingleParty(int partyId){
        this.partyId = partyId;
    }
    
    public static void encode(MessageStcSyncDeleteSingleParty packet, FriendlyByteBuf buffer){
        buffer.writeInt(packet.partyId);
    }
    
    public static MessageStcSyncDeleteSingleParty decode(FriendlyByteBuf buffer) {
        if(buffer.isReadable(4)){
            return new MessageStcSyncDeleteSingleParty(buffer.readInt());
        }
        return null;
    }
    
    public static void handle(final MessageStcSyncDeleteSingleParty message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(message!=null){
                EditorClientProcessor.Delete.deleteSingleParty(message.partyId);
                ClientUtils.reloadMainGuiIfOpen();
                ClientUtils.reloadEditorIfOpen();
            }
        }).thenRun(()->ctx.get().setPacketHandled(true));
    }
}