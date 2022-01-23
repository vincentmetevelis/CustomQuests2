package com.vincentmet.customquests.helpers.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

public class Image{
	public static void drawTexture(ResourceLocation resource, float posX, float posY, float width, float height, double zLevel){
		RenderSystem.setShaderTexture(0, resource);
		draw(posX, posY, width, height, zLevel);
	}
	
	public static void draw(float posX, float posY, float width, float height, double zLevel){
		draw(posX, posY, width, height, zLevel, 0F, 1F, 0F, 1F);
	}
	
	public static void draw(double posX, double posY, double width, double height, double zLevel, float u1, float u2, float v1, float v2){
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(posX, posY + height, zLevel).uv(u1, v2).endVertex();
		bufferbuilder.vertex(posX + width, posY + height, zLevel).uv(u2, v2).endVertex();
		bufferbuilder.vertex(posX + width, posY, zLevel).uv(u2, v1).endVertex();
		bufferbuilder.vertex(posX, posY, zLevel).uv(u1, v1).endVertex();
		tesselator.end();
	}
}