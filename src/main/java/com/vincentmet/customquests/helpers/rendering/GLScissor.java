package com.vincentmet.customquests.helpers.rendering;

import net.minecraft.client.*;
import net.minecraftforge.api.distmarker.*;
import static org.lwjgl.opengl.GL11.*;

import com.mojang.blaze3d.platform.Window;

@OnlyIn(Dist.CLIENT)
public class GLScissor{
	public static void enable(int x, int y, int width, int height){
		Window mw = Minecraft.getInstance().getWindow();
		double s = mw.getGuiScale();
		
		if(width<0)width=0;
		if(height<0)height=0;
		if(x<0)x=0;
		if(y<0)y=0;
		glPushMatrix();
		glEnable(GL_SCISSOR_TEST);
		glScissor(
				(int)(x * s),
				(int)(mw.getHeight() - ((double)(y + height) * s)),
				(int)(width * s),
				(int)(height * s)
		);
	}
	
	public static void disable(){
		glDisable(GL_SCISSOR_TEST);
		glPopMatrix();
	}
	
	public static boolean isEnabled(){
		return glIsEnabled(GL_SCISSOR_TEST);
	}
}