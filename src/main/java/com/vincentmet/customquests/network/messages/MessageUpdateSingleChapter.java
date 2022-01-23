package com.vincentmet.customquests.network.messages;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.QuestingStorage;
import com.vincentmet.customquests.hierarchy.chapter.Chapter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static com.vincentmet.customquests.Ref.CustomQuests.LOGGER;

public class MessageUpdateSingleChapter{
	public JsonObject json;//used for receiving packet only
	public int chapterId;
	
	public MessageUpdateSingleChapter(int chapterId){
		this.chapterId = chapterId;
	}
	
	public MessageUpdateSingleChapter(int chapterId, JsonObject json){
		this.chapterId = chapterId;
		this.json = json;
	}
	
	public static void encode(MessageUpdateSingleChapter packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.chapterId);
		buffer.writeUtf(QuestingStorage.getSidedChaptersMap().get(packet.chapterId).getJson().toString());
	}
	
	public static MessageUpdateSingleChapter decode(FriendlyByteBuf buffer) {
		int chapterId = buffer.readInt();
		String stringJson = buffer.readUtf();
		JsonObject json = JsonParser.parseString(stringJson).getAsJsonObject();
		return new MessageUpdateSingleChapter(chapterId, json);
	}
	
	public static void handle(final MessageUpdateSingleChapter message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			int id = message.chapterId;
			JsonObject data = message.json;
			Chapter c = new Chapter(id);
			c.processJson(data);
			QuestingStorage.getSidedChaptersMap().put(id, c);
			ClientUtils.reloadEditorIfOpen();
			LOGGER.info("Chapter " + id + " synced!");
		});
		ctx.get().setPacketHandled(true);
	}
}
