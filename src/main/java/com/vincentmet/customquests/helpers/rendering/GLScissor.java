package com.vincentmet.customquests.helpers.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.*;
import net.minecraftforge.api.distmarker.*;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;

import com.mojang.blaze3d.platform.Window;

@OnlyIn(Dist.CLIENT)
public class GLScissor{
	public static void enable(PoseStack matrixStack, int x, int y, int width, int height){
		Window mw = Minecraft.getInstance().getWindow();
		double s = mw.getGuiScale();
		
		if(width<0)width=0;
		if(height<0)height=0;
		if(x<0)x=0;
		if(y<0)y=0;
		matrixStack.pushPose();
		glEnable(GL_SCISSOR_TEST);
		glScissor(
				(int)(x * s),
				(int)(mw.getHeight() - ((double)(y + height) * s)),
				(int)(width * s),
				(int)(height * s)
		);
	}
	
	public static void disable(PoseStack matrixStack){
		glDisable(GL_SCISSOR_TEST);
		matrixStack.popPose();
	}
	
	public static boolean isEnabled(){
		return glIsEnabled(GL_SCISSOR_TEST);
	}
}