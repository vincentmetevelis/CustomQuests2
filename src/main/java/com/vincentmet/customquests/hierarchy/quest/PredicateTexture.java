package com.vincentmet.customquests.hierarchy.quest;

import com.vincentmet.customquests.api.IQuestingTexture;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.*;
import org.lwjgl.opengl.GL11;

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
	public void render(float scale, int x, int y, int mouseX, int mouseY){
		if(showing.get()){
			GL11.glPushMatrix();
			GL11.glScalef(scale, scale, 1);
			Minecraft.getInstance().textureManager.bindTexture(texture);
			AbstractGui.blit(x, y, texU, texV, texWidth, texHeight, texSizeX, texSizeY);
			GL11.glPopMatrix();
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