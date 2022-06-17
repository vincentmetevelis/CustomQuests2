package com.vincentmet.customquests.helpers.rendering;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.IHoverRenderable;
import com.vincentmet.customquests.helpers.MouseButton;
import com.vincentmet.customquests.helpers.TooltipBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class VariableSlot implements IHoverRenderable{
	private int x, y, width, height;
	private SlotTexture texture;
	private Consumer<MouseButton> onClickCallback;
	private List<String> tooltipLines;
	
	public VariableSlot(int x, int y, int width, int height, SlotTexture texture, Consumer<MouseButton> onClickCallback, List<String> tooltipLines){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.texture = texture;
		this.onClickCallback = onClickCallback;
		this.tooltipLines = tooltipLines;
	}
	
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		Color.color(0xFFFFFFFF);
		Lighting.setupForFlatItems();
		
		int texU = this.texture.getU();
		int texV = this.texture.getV();
		int texWidth = this.texture.getWidth();
		int texHeight = this.texture.getHeight();
		int texP = this.texture.getBorderSize(); // P for Padding
		
		RenderSystem.setShaderTexture(0, this.texture.getTexture());
		
		// blit -> x, y, u, v, width, height, texSizeX, texSizeY
		GuiComponent.blit(matrixStack, x, y, texU, texV, texP, texP, texWidth, texHeight);// Left Top corner
		GuiComponent.blit(matrixStack, x, y + height - texP, texU, texHeight - texP, texP, texP, texWidth, texHeight);// Left Bottom corner
		GuiComponent.blit(matrixStack, x + width - texP, y, texWidth - texP, texV, texP, texP, texWidth, texHeight);// Right Top corner
		GuiComponent.blit(matrixStack, x + width - texP, y + height - texP, texWidth - texP, texHeight - texP, texP, texP, texWidth, texHeight);// Right Bottom corner
		
		int innerWidth = texWidth - 2 * texP;
		int innerHeight = texHeight - 2 * texP;
		int right = x + width - texP;
		int bottom = y + height - texP;
		
		for (int left = x + texP; left < right; left += innerWidth) {// Top and Bottom Edges
			GuiComponent.blit(matrixStack, left, y, texP, 0, Math.min(innerWidth, right - left), texP, texWidth, texHeight);// Top
			GuiComponent.blit(matrixStack, left, y + height - texP, texP, texHeight - texP, Math.min(innerWidth, right - left), texP, texWidth, texHeight);// Bottom
		}
		for (int top = y + texP; top < bottom; top += innerHeight) {// Left and Right Edges
			GuiComponent.blit(matrixStack, x, top, 0, texP, texP, Math.min(innerHeight, bottom - top), texWidth, texHeight);// Left
			GuiComponent.blit(matrixStack, x + width - texP, top, texWidth - texP, texP, texP, Math.min(innerHeight, bottom - top), texWidth, texHeight);// Right
		}
		for (int left = x + texP; left < right; left += innerWidth) {// Fill the Middle
			for (int top = y + texP; top < bottom; top += innerHeight) {
				GuiComponent.blit(matrixStack, left, top, texP, texP, Math.min(innerWidth, right - left), Math.min(innerHeight, bottom - top), texWidth, texHeight);
			}
		}
	}
	
	@Override
	public void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, x, y, x+width, y+height)){
			TooltipBuffer.tooltipBuffer.add(()->{
				if(Minecraft.getInstance().screen != null) Minecraft.getInstance().screen.renderTooltip(matrixStack, tooltipLines.stream().map(line -> FormattedCharSequence.forward(line, Style.EMPTY)).collect(Collectors.toList()), mouseX, mouseY);
			});
		}
	}
	
	public void onClick(double mouseX, double mouseY, int mouseButton){
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, x, y, x+width, y+height)){
			if(onClickCallback != null){
				onClickCallback.accept(MouseButton.getButtonFromGlButton(mouseButton));
			}
		}
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public SlotTexture getTexture(){
		return texture;
	}
	
	public Consumer<MouseButton> getOnClickCallback(){
		return onClickCallback;
	}
	
	public List<String> getTooltipLines(){
		return tooltipLines;
	}
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public void setWidth(int width){
		this.width = width;
	}
	
	public void setHeight(int height){
		this.height = height;
	}
	
	public void setTexture(SlotTexture texture){
		this.texture = texture;
	}
	
	public void setOnClickCallback(Consumer<MouseButton> onClickCallback){
		this.onClickCallback = onClickCallback;
	}
	
	public void setTooltipLines(List<String> tooltipLines){
		this.tooltipLines = tooltipLines;
	}
	
	public static class SlotTexture {
		public static final SlotTexture DEFAULT = new SlotTexture(0, 0, 3, 3, 1, new ResourceLocation(Ref.MODID, "textures/gui/slot_scalable.png"));
		private final ResourceLocation texture;
		private final int u;
		private final int v;
		private final int width;
		private final int height;
		private final int borderSize;
		
		public SlotTexture(int u, int v, int width, int height, int borderSize, ResourceLocation texture) {
			this.texture = texture;
			this.u = u;
			this.v = v;
			this.width = width;
			this.height = height;
			this.borderSize = borderSize;
		}
		
		public ResourceLocation getTexture() {
			return this.texture;
		}
		
		public int getU() {
			return this.u;
		}
		
		public int getV() {
			return this.v;
		}
		
		public int getWidth() {
			return this.width;
		}
		
		public int getHeight() {
			return this.height;
		}
		
		public int getBorderSize() {
			return this.borderSize;
		}
	}
}