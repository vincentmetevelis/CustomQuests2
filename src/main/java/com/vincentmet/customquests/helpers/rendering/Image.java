package com.vincentmet.customquests.helpers.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Image{
	public static void drawTexture(ResourceLocation resource, float posX, float posY, float width, float height, double zLevel){
		Minecraft.getInstance().getTextureManager().bind(resource);
		draw(posX, posY, width, height, zLevel);
	}
	
	public static void draw(float posX, float posY, float width, float height, double zLevel){
		draw(posX, posY, width, height, zLevel, 0F, 1F, 0F, 1F);
	}
	
	public static void draw(double posX, double posY, double width, double height, double zLevel, float u1, float u2, float v1, float v2){
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.vertex(posX, posY + height, zLevel).uv(u1, v2).endVertex();
		bufferbuilder.vertex(posX + width, posY + height, zLevel).uv(u2, v2).endVertex();
		bufferbuilder.vertex(posX + width, posY, zLevel).uv(u2, v1).endVertex();
		bufferbuilder.vertex(posX, posY, zLevel).uv(u1, v1).endVertex();
		tessellator.end();
	}
}