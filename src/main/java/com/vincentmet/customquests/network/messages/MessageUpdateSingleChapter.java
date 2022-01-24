package com.vincentmet.customquests.network.messages;

import com.google.gson.*;
import com.vincentmet.customquests.api.QuestingStorage;
import com.vincentmet.customquests.gui.QuestingScreen;
import com.vincentmet.customquests.hierarchy.chapter.Chapter;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
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
	
	public static void encode(MessageUpdateSingleChapter packet, PacketBuffer buffer){
		buffer.writeInt(packet.chapterId);
		buffer.writeUtf(QuestingStorage.getSidedChaptersMap().get(packet.chapterId).getJson().toString());
	}
	
	public static MessageUpdateSingleChapter decode(PacketBuffer buffer) {
		int chapterId = buffer.readInt();
		String stringJson = buffer.readUtf();
		JsonObject json = new JsonParser().parse(stringJson).getAsJsonObject();
		return new MessageUpdateSingleChapter(chapterId, json);
	}
	
	public static void handle(final MessageUpdateSingleChapter message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			try{
				int id = message.chapterId;
				JsonObject data = message.json;
				Chapter c = new Chapter(id);
				c.processJson(data);
				QuestingStorage.getSidedChaptersMap().put(id, c);
				LOGGER.info("Chapter " + id + " synced!");
			}catch(NumberFormatException exception){
				LOGGER.error("Chapter " + message.chapterId + " should be a numeric id");
				exception.printStackTrace();
			}
			Screen currentScreen = Minecraft.getInstance().screen;
			if(currentScreen instanceof QuestingScreen){
				((QuestingScreen)currentScreen).requestPosRecalc();
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
