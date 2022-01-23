package com.vincentmet.customquests.helpers.rendering;

import com.mojang.blaze3d.systems.RenderSystem;

public class Color{
	public static void color(int color){//ARGB
		float a = (color >> 24 & 0xFF) / 255.0F;
		float r = (color >> 16 & 0xFF) / 255.0F;
		float g = (color >> 8 & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;
		RenderSystem.setShaderColor(r, g, b, a);
	}

	public static void color(int color, float alpha){ //RGB - A
		float r = (color >> 16 & 0xFF) / 255.0F;
		float g = (color >> 8 & 0xFF) / 255.0F;
		float b = (color & 0xFF) / 255.0F;
		RenderSystem.setShaderColor(r, g, b, alpha);
	}
}