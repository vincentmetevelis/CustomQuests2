package com.vincentmet.customquests.helpers.rendering;

import static org.lwjgl.opengl.GL11.glColor4f;

public class Color{
	public static void color(int color){
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		glColor4f(r, g, b, 1.0F);
	}
	
	public static void color(int color, float alpha){
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		glColor4f(r, g, b, alpha);
	}
}