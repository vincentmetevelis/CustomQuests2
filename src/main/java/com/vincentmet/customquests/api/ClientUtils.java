package com.vincentmet.customquests.api;

import com.vincentmet.customquests.gui.EditorScreen;
import com.vincentmet.customquests.gui.QuestingScreen;
import com.vincentmet.customquests.network.messages.PacketHandler;
import com.vincentmet.customquests.network.messages.editor.MessageEditorAddChapter;
import com.vincentmet.customquests.network.messages.editor.MessageEditorAddQuest;
import com.vincentmet.customquests.network.messages.editor.MessageEditorRemoveChapter;
import com.vincentmet.customquests.network.messages.editor.MessageEditorRemoveQuest;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ClientUtils{
	public static void openQuestingScreen(){
		Minecraft.getInstance().setScreen(new QuestingScreen());
	}
	
	public static void openEditorScreen(){
		Minecraft.getInstance().setScreen(new EditorScreen());
	}
	
	public static void reloadEditorIfOpen(){
		if(Minecraft.getInstance().screen instanceof EditorScreen){
			((EditorScreen)Minecraft.getInstance().screen).reInit();
		}
	}
	
	public static void sendEditorCreateChapter(){
		PacketHandler.CHANNEL.sendToServer(new MessageEditorAddChapter());
	}
	
	public static void sendEditorCreateQuest(){
		PacketHandler.CHANNEL.sendToServer(new MessageEditorAddQuest());
	}
	
	public static void sendEditorRemoveChapter(int chapterId){
		PacketHandler.CHANNEL.sendToServer(new MessageEditorRemoveChapter(chapterId));
	}
	
	public static void sendEditorRemoveQuest(int questId){
		PacketHandler.CHANNEL.sendToServer(new MessageEditorRemoveQuest(questId));
	}
	
	public static void sendEditorUpdateChapter(int chapterId, ResourceLocation icon, List<Integer> questIds){
		//todo
	}
	
	public static void sendEditorUpdateChapterTitle(int chapterId, ResourceLocation textType, String text){
		//todo
	}
	
	public static void sendEditorUpdateChapterText(int chapterId, ResourceLocation textType, String text){
		//todo
	}
}