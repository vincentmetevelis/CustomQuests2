package com.vincentmet.customquests.network.messages.sync.stc.delete;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.EditorClientProcessor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MessageStcSyncDeleteSinglePlayer {
    private final UUID uuid;

    public MessageStcSyncDeleteSinglePlayer(UUID uuid){
        this.uuid = uuid;
    }
    
    public static void encode(MessageStcSyncDeleteSinglePlayer packet, FriendlyByteBuf buffer){
        buffer.writeUUID(packet.uuid);
    }
    
    public static MessageStcSyncDeleteSinglePlayer decode(FriendlyByteBuf buffer) {
        if(buffer.isReadable(16)){//uuid == 2 long values
            return new MessageStcSyncDeleteSinglePlayer(buffer.readUUID());
        }
        return null;
    }
    
    public static void handle(final MessageStcSyncDeleteSinglePlayer message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(message!=null){
                EditorClientProcessor.Delete.deleteSinglePlayer(message.uuid);
                ClientUtils.reloadMainGuiIfOpen();
                ClientUtils.reloadEditorIfOpen();
            }
        }).thenRun(()->ctx.get().setPacketHandled(true));
    }
}