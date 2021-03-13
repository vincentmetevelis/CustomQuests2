package com.vincentmet.customquests.helpers.rendering;

import static org.lwjgl.opengl.GL11.glColor4f;

public class Color{
	public static void color(int color){//ARGB
		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		glColor4f(r, g, b, a);
	}
	
	public static void color(int color, float alpha){ //RGB - A
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		glColor4f(r, g, b, alpha);
	}
}