package com.vincentmet.customquests.helpers.rendering;

import static com.mojang.blaze3d.platform.GlStateManager.color4f;

public class Color{
	public static void color(int color){
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		color4f(r, g, b, 1.0F);
	}
	
	public static void color(int color, float alpha){
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		color4f(r, g, b, alpha);
	}
}