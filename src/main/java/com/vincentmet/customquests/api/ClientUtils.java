package com.vincentmet.customquests.api;

import com.vincentmet.customquests.gui.QuestingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;

public class ClientUtils{
	public static String colorify(String string){
		for (ChatFormatting textFormatting : ChatFormatting.values()) {
			string = string.replaceAll(String.format("~%s~", textFormatting.getName().toUpperCase()), textFormatting.toString());
		}
		return string;
	}
	
	public static void openQuestingScreen(){
		Minecraft.getInstance().setScreen(new QuestingScreen());
	}
}
