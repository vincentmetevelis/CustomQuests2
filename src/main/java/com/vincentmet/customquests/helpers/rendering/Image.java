package com.vincentmet.customquests.helpers.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Image{
	public static void drawTexture(ResourceLocation resource, double posX, double posY, double width, double height, double zLevel){
		Minecraft.getInstance().getTextureManager().bindTexture(resource);
		draw(posX, posY, width, height, zLevel);
	}
	
	public static void draw(double posX, double posY, double width, double height, double zLevel){
		draw(posX, posY, width, height, zLevel, 0D, 1D, 0D, 1D);
	}
	
	public static void draw(double posX, double posY, double width, double height, double zLevel, double u1, double u2, double v1, double v2){
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(posX, posY + height, zLevel).tex(u1, v2).endVertex();
		bufferbuilder.pos(posX + width, posY + height, zLevel).tex(u2, v2).endVertex();
		bufferbuilder.pos(posX + width, posY, zLevel).tex(u2, v1).endVertex();
		bufferbuilder.pos(posX, posY, zLevel).tex(u1, v1).endVertex();
		tessellator.draw();
	}
}