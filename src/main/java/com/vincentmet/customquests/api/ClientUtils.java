package com.vincentmet.customquests.api;

import com.vincentmet.customquests.gui.QuestingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class ClientUtils{
	public static String colorify(String string){
		for (TextFormatting textFormatting : TextFormatting.values()) {
			string = string.replaceAll(String.format("~%s~", textFormatting.getFriendlyName().toUpperCase()), textFormatting.toString());
		}
		return string;
	}
	
	public static void openQuestingScreen(){
		Minecraft.getInstance().displayGuiScreen(new QuestingScreen());
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void renderTooltip(ItemStack itemStack, int x, int y) {
		Screen currentScreen = Minecraft.getInstance().currentScreen;
		if(currentScreen!=null){
			FontRenderer font = itemStack.getItem().getFontRenderer(itemStack);
			GuiUtils.preItemToolTip(itemStack);
			currentScreen.renderTooltip(currentScreen.getTooltipFromItem(itemStack), x, y, (font == null ? Minecraft.getInstance().fontRenderer : font));
			GuiUtils.postItemToolTip();
		}
	}
}
