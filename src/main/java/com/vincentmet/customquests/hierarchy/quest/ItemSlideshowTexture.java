package com.vincentmet.customquests.hierarchy.quest;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.api.ICurrentItemStackProvider;
import com.vincentmet.customquests.api.IQuestingTexture;
import com.vincentmet.customquests.helpers.rendering.ItemRenderHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

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
	public void render(PoseStack matrixStack, float scale, int x, int y, float offsetX, float offsetY, int mouseX, int mouseY){
		if(!items.isEmpty()){
			int arrSize = items.size();
			int currentIndex = (int)(System.currentTimeMillis()/50/delay%arrSize);
			ItemRenderHelper.renderGuiItem(items.get(currentIndex), x, y, scale, offsetX, offsetY);
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