package com.vincentmet.customquests.helpers.rendering;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;

public class Image{
	public static void drawTexture(ResourceLocation resource, float posX, float posY, float width, float height, double zLevel){
		Minecraft.getInstance().getTextureManager().bindForSetup(resource);
		draw(posX, posY, width, height, zLevel);
	}
	
	public static void draw(float posX, float posY, float width, float height, double zLevel){
		draw(posX, posY, width, height, zLevel, 0F, 1F, 0F, 1F);
	}
	
	public static void draw(double posX, double posY, double width, double height, double zLevel, float u1, float u2, float v1, float v2){
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		//TODO 7 = VertexFormat.Mode.LINES ??
		bufferbuilder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(posX, posY + height, zLevel).uv(u1, v2).endVertex();
		bufferbuilder.vertex(posX + width, posY + height, zLevel).uv(u2, v2).endVertex();
		bufferbuilder.vertex(posX + width, posY, zLevel).uv(u2, v1).endVertex();
		bufferbuilder.vertex(posX, posY, zLevel).uv(u1, v1).endVertex();
		tessellator.end();
	}
}