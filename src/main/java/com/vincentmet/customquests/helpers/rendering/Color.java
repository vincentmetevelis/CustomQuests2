package com.vincentmet.customquests.helpers.rendering;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import static org.lwjgl.opengl.GL11.glColor4f;

public class Color{
	public static void color(int color){//ARGB
		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		BufferBuilder builder = Tesselator.getInstance().getBuilder();
		builder.begin(VertexFormat.Mode.LINES, DefaultVertexFormat.BLIT_SCREEN);
		builder.color(r, g, b, a);
		builder.end();
	}
	
	public static void color(int color, float alpha){ //RGB - A
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		Tesselator.getInstance().getBuilder().color(r, g, b, alpha);
	}
}