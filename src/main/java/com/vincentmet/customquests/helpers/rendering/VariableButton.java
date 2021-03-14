package com.vincentmet.customquests.helpers.rendering;

import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.gui.elements.ScrollingLabel;
import com.vincentmet.customquests.helpers.*;
import com.vincentmet.customquests.helpers.math.Vec2i;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.*;

@OnlyIn(Dist.CLIENT)
public class VariableButton implements IHoverRenderable, IGuiEventListener{
	private final int parentX;
	private final int parentY;
	
	private int x, y, width, height;
	private ButtonTexture texture;
	private ScrollingLabel buttonText;
	private Vec2i textOffsetFromCenter;
	private Consumer<MouseButton> onClickCallback;
	private List<ITextComponent> ogTooltipLines;
	private List<String> tooltipLines;
	
	public VariableButton(int parentX, int parentY, int x, int y, int width, int height, ButtonTexture texture, String buttonText, Vec2i textOffsetFromCenter, Consumer<MouseButton> onClickCallback, List<ITextComponent> tooltipLines){
		this.parentX = parentX;
		this.parentY = parentY;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.texture = texture;
		this.textOffsetFromCenter = textOffsetFromCenter;
		this.onClickCallback = onClickCallback;
		this.ogTooltipLines = tooltipLines;
		this.tooltipLines = tooltipLines.stream().map(ITextComponent::getFormattedText).collect(Collectors.toList());
		
		this.buttonText = new ScrollingLabel(parentX + x + textOffsetFromCenter.getX() + (width>>1) - (Math.min(getStringWidth(buttonText), getMaxTextWidth())>>1), parentY + y + textOffsetFromCenter.getY() + (height>>1) - (Minecraft.getInstance().fontRenderer.FONT_HEIGHT>>1), buttonText, Math.min(getMaxTextWidth(), getStringWidth(buttonText)), 30, 1);
	}
	
	public VariableButton(int x, int y, int width, int height, ButtonTexture texture, String buttonText, Vec2i textOffsetFromCenter, Consumer<MouseButton> onClickCallback, List<ITextComponent> tooltipLines){
		this(0, 0, x, y, width, height, texture, buttonText, textOffsetFromCenter, onClickCallback, tooltipLines);
	}
	
	private int getMaxTextWidth(){
		return width - ((textOffsetFromCenter.getX()+2)*2);
	}
	
	private int getStringWidth(String text){
		return Minecraft.getInstance().fontRenderer.getStringWidth(text);
	}
	
	public void render(int mouseX, int mouseY, float partialTicks) {
		internalRender(mouseX, mouseY, partialTicks, texture);
	}
	
	@Override
	public void renderHover(int mouseX, int mouseY, float partialTicks){
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, x, y, x+width, y+height)){
			TooltipBuffer.tooltipBuffer.add(()->{
				if(Minecraft.getInstance().currentScreen != null) Minecraft.getInstance().currentScreen.renderTooltip(tooltipLines, mouseX, mouseY);
			});
		}
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, x, y, x+width, y+height) && texture == ButtonTexture.DEFAULT_NORMAL){
			internalRender(mouseX, mouseY, partialTicks, ButtonTexture.DEFAULT_PRESSED);
		}
	}
	
	private void internalRender(int mouseX, int mouseY, float partialTicks, ButtonTexture texture){
		Color.color(0xFFFFFFFF);
		RenderHelper.disableStandardItemLighting();
		
		int x = parentX + this.x;
		int y = parentY + this.y;
		
		int texU = texture.getU();
		int texV = texture.getV();
		int texWidth = texture.getTexWidth();
		int texHeight = texture.getTexHeight();
		int texP = texture.getBorderSize(); // P for Padding
		
		Minecraft.getInstance().getTextureManager().bindTexture(texture.getTexture());
		//blit -> x, y, u, v, width, height, texSizeX, texSizeY
		
		int right = x + width - texP;
		int bottom = y + height - texP;
		int texRight = texU + texture.getWidth() - texP;
		int texBottom = texV + texture.getWidth() - texP;
		
		AbstractGui.blit(x, y, texU, texV, texP, texP, texWidth, texHeight);// Left Top corner
		AbstractGui.blit(right, y, texRight, texV, texP, texP, texWidth, texHeight);// Right Top corner
		AbstractGui.blit(right, bottom, texRight, texBottom, texP, texP, texWidth, texHeight);// Right Bottom corner
		AbstractGui.blit(x, bottom, texU, texBottom, texP, texP, texWidth, texHeight);// Left Bottom corner
		
		
		int strippedButtonTexWidth = texture.getWidth() - 2*texP;
		int strippedButtonTexHeight = texture.getHeight() - 2*texP;
		
		for (int left = x + texP; left < right; left += strippedButtonTexWidth) {// Fill the Middle
			for (int top = y + texP; top < bottom; top += strippedButtonTexHeight) {
				AbstractGui.blit(left, top, texU + texP, texV + texP, Math.min(strippedButtonTexWidth, right - left), Math.min(strippedButtonTexHeight, bottom - top), texWidth, texHeight);
			}
		}
		for (int left = x + texP; left < right; left += strippedButtonTexWidth) {// Top and Bottom Edges
			AbstractGui.blit(left, y, texU + texP, texV, Math.min(strippedButtonTexWidth, right - left), texP, texWidth, texHeight);// Top
			AbstractGui.blit(left, bottom, texU + texP, texBottom, Math.min(strippedButtonTexWidth, right - left), texP, texWidth, texHeight);// Bottom
		}
		for (int top = y + texP; top < bottom; top += strippedButtonTexHeight) {// Left and Right Edges
			AbstractGui.blit(x, top, texU, texV + texP, texP, Math.min(strippedButtonTexHeight, bottom - top), texWidth, texHeight);// Left
			AbstractGui.blit(right, top, texRight, texV + texP, texP, Math.min(strippedButtonTexHeight, bottom - top), texWidth, texHeight);// Right
		}
		buttonText.render(mouseX, mouseY, partialTicks);
	}
	
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton){
		int x = parentX + this.x;
		int y = parentY + this.y;
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, x, y, x+width, y+height)){
			if(onClickCallback != null){
				onClickCallback.accept(MouseButton.getButtonFromGlButton(mouseButton));
				return true;
			}
		}
		return false;
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
	
	public ButtonTexture getTexture(){
		return texture;
	}
	
	public ScrollingLabel getButtonText(){
		return buttonText;
	}
	
	public Vec2i getTextOffsetFromCenter(){
		return textOffsetFromCenter;
	}
	
	public Consumer<MouseButton> getOnClickCallback(){
		return onClickCallback;
	}
	
	public List<ITextComponent> getTooltipLines(){
		return ogTooltipLines;
	}
	
	public void setX(int x){
		this.x = x;
		this.buttonText.setX(parentX + x + textOffsetFromCenter.getX() + (width>>1) - (getStringWidth(buttonText.getText())>>1));
	}
	
	public void setY(int y){
		this.y = y;
		this.buttonText.setY(parentY + y + textOffsetFromCenter.getY() + (height>>1) - (Minecraft.getInstance().fontRenderer.FONT_HEIGHT>>1));
	}
	
	public void setWidth(int width){
		this.width = width;
	}
	
	public void setHeight(int height){
		this.height = height;
	}
	
	public void setTexture(ButtonTexture texture){
		this.texture = texture;
	}
	
	public void setButtonText(String buttonText){
		this.buttonText = new ScrollingLabel(x + textOffsetFromCenter.getX() + (width>>1) - (getStringWidth(buttonText)>>1), y + textOffsetFromCenter.getY() + (height>>1) - (Minecraft.getInstance().fontRenderer.FONT_HEIGHT>>1), buttonText, getMaxTextWidth(), 30, 1);
	}
	
	public void setTextOffsetFromCenter(Vec2i textOffsetFromCenter){
		this.textOffsetFromCenter = textOffsetFromCenter;
	}
	
	public void setOnClickCallback(Consumer<MouseButton> onClickCallback){
		this.onClickCallback = onClickCallback;
	}
	
	public void setTooltipLines(List<ITextComponent> tooltipLines){
		this.ogTooltipLines = tooltipLines;
		this.tooltipLines = tooltipLines.stream().map(ITextComponent::getFormattedText).collect(Collectors.toList());
	}
	
	public static class ButtonTexture {
		public static final ButtonTexture DEFAULT_NORMAL = new ButtonTexture(0, 0, 24, 24, 72, 72, 2, new ResourceLocation(Ref.MODID, "textures/gui/button_square.png"));
		public static final ButtonTexture DEFAULT_PRESSED = new ButtonTexture(24, 0, 24, 24, 72, 72, 2, new ResourceLocation(Ref.MODID, "textures/gui/button_square.png"));
		public static final ButtonTexture DEFAULT_DISABLED = new ButtonTexture(48, 0, 24, 24, 72, 72, 2, new ResourceLocation(Ref.MODID, "textures/gui/button_square.png"));
		public static final ButtonTexture DEFAULT_BLUE = new ButtonTexture(0, 24, 24, 24, 72, 72, 2, new ResourceLocation(Ref.MODID, "textures/gui/button_square.png"));
		public static final ButtonTexture DEFAULT_GREEN = new ButtonTexture(24, 24, 24, 24, 72, 72, 2, new ResourceLocation(Ref.MODID, "textures/gui/button_square.png"));
		private final ResourceLocation texture;
		private final int u;
		private final int v;
		private final int width;
		private final int height;
		private final int texWidth;
		private final int texHeight;
		private final int borderSize;
		
		public ButtonTexture(int u, int v, int width, int height, int texWidth, int texHeight, int borderSize, ResourceLocation texture) {
			this.texture = texture;
			this.u = u;
			this.v = v;
			this.width = width;
			this.height = height;
			this.texWidth = texWidth;
			this.texHeight = texHeight;
			this.borderSize = borderSize;
		}
		
		public ResourceLocation getTexture(){
			return texture;
		}
		
		public int getU() {
			return this.u;
		}
		
		public int getV() {
			return this.v;
		}
		
		public int getWidth(){
			return width;
		}
		
		public int getHeight(){
			return height;
		}
		
		public int getTexWidth() {
			return this.texWidth;
		}
		
		public int getTexHeight() {
			return this.texHeight;
		}
		
		public int getBorderSize() {
			return this.borderSize;
		}
	}
}