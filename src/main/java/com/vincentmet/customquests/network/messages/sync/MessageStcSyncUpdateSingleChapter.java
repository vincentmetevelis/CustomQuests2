package com.vincentmet.customquests.network.messages.sync;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.api.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

import static com.vincentmet.customquests.Ref.CustomQuests.LOGGER;

public class MessageStcSyncUpdateSingleChapter {
    private final int chapterId;
    private JsonObject jsonObject;

    private MessageStcSyncUpdateSingleChapter(int chapterId, JsonObject json){
        this.chapterId = chapterId;
        this.jsonObject = json;
    }

    public MessageStcSyncUpdateSingleChapter(int chapterId){
        this.chapterId = chapterId;
        if(ChapterHelper.doesChapterExist(chapterId)){
            jsonObject = QuestingStorage.getSidedChaptersMap().get(chapterId).getJson();
        }else{
            ServerUtils.Packets.Delete.deleteSingleChapterAtAllClients(chapterId);
        }
    }
    
    public static void encode(MessageStcSyncUpdateSingleChapter packet, FriendlyByteBuf buffer){
        if(ChapterHelper.doesChapterExist(packet.chapterId) && packet.jsonObject != null){
            buffer.writeInt(packet.chapterId);
            buffer.writeUtf(packet.jsonObject.getAsString());
        }
    }
    
    public static MessageStcSyncUpdateSingleChapter decode(FriendlyByteBuf buffer) {
        if(buffer.isReadable(6)){//4 for int, 2+ for json
            return new MessageStcSyncUpdateSingleChapter(buffer.readInt(), JsonParser.parseString(buffer.readUtf()).getAsJsonObject());
        }
        return null;
    }
    
    public static void handle(final MessageStcSyncUpdateSingleChapter message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(message!=null){
                EditorClientProcessor.Update.Chapters.updateSingleChapter(message.chapterId, message.jsonObject);
                ClientUtils.reloadMainGuiIfOpen();
                ClientUtils.reloadEditorIfOpen();
                LOGGER.info("Chapter " + message.chapterId + " synced!");
            }
        }).thenRun(() -> ctx.get().setPacketHandled(true));
    }
}