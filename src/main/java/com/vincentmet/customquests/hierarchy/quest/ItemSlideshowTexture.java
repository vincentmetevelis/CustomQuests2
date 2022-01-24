package com.vincentmet.customquests.hierarchy.quest;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.vincentmet.customquests.api.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.*;

public class ItemSlideshowTexture implements IQuestingTexture, ICurrentItemStackProvider{
	private ResourceLocation ogResourceLocation;
	private List<ItemStack> items = new ArrayList<>();
	private int delay = 20;
	
	public ItemSlideshowTexture(ResourceLocation ogResourceLocation, List<ItemStack> items){
		this.items = items;
		this.ogResourceLocation = ogResourceLocation;
	}
	
	public ItemSlideshowTexture(ResourceLocation ogResourceLocation, ItemStack item){
		this.items.add(item);
		this.ogResourceLocation = ogResourceLocation;
	}
    
    @Override
    public boolean isValid(){
        return !items.isEmpty();
    }
    
    @OnlyIn(Dist.CLIENT)
	public void render(MatrixStack stack, float scale, int x, int y, int mouseX, int mouseY){
		if(!items.isEmpty()){
			int arrSize = items.size();
			int currentIndex = (int)(System.currentTimeMillis()/50/delay%arrSize);
			RenderHelper.setupFor3DItems();
			Minecraft.getInstance().getItemRenderer().renderGuiItem(items.get(currentIndex), x, y);
		}
	}
	@Override
	public ItemStack getCurrentItemStack(){
		if(!items.isEmpty()){
			int arrSize = items.size();
			int currentIndex = (int)(System.currentTimeMillis()/50/delay%arrSize);
			return items.get(currentIndex);
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public String toString(){
		return ogResourceLocation.toString();
	}
	
	@Override
	public ResourceLocation getResourceLocation(){
		return ogResourceLocation;
	}
}