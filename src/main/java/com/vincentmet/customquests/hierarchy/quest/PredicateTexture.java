package com.vincentmet.customquests.hierarchy.quest;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.api.IQuestingTexture;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class PredicateTexture implements IQuestingTexture{
	private ResourceLocation texture;
	private int texU, texV, texWidth, texHeight, texSizeX, texSizeY;
	private Supplier<Boolean> showing;
	
	public PredicateTexture(ResourceLocation texture, int texU, int texV, int texWidth, int texHeight, int texSizeX, int texSizeY, Supplier<Boolean> showing){
		this.texture = texture;
		this.texU = texU;
		this.texV = texV;
		this.texWidth = texWidth;
		this.texHeight = texHeight;
		this.texSizeX = texSizeX;
		this.texSizeY = texSizeY;
		this.showing = showing;
	}
    
    @Override
    public boolean isValid(){
        return texture != null && texU>=0 && texV>=0 && texWidth>=0 && texHeight>=0 && texSizeX>=0 && texSizeY>=0;
    }
    
    @OnlyIn(Dist.CLIENT)
	public void render(PoseStack matrixStack, float scale, int x, int y, float offsetX, float offsetY, int mouseX, int mouseY){
		if(showing.get()){
			matrixStack.pushPose();
			matrixStack.scale(scale, scale, 1);
			RenderSystem.setShaderTexture(0, texture);
			GuiComponent.blit(matrixStack, x, y, texU, texV, texWidth, texHeight, texSizeX, texSizeY);
			matrixStack.popPose();
		}
	}
	
	@Override
	public String toString(){
		return texture.toString();
	}
	
	@Override
	public ResourceLocation getResourceLocation(){
		return texture;
	}
}