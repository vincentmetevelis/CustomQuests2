package com.vincentmet.customquests.helpers.rendering;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.IHoverRenderable;
import com.vincentmet.customquests.gui.elements.ScrollingLabel;
import com.vincentmet.customquests.helpers.MouseButton;
import com.vincentmet.customquests.helpers.TooltipBuffer;
import com.vincentmet.customquests.helpers.math.Vec2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

@OnlyIn(Dist.CLIENT)
public class VariableButton implements IHoverRenderable, GuiEventListener {
	private final int parentX;
	private final int parentY;
	
	private IntSupplier x, y, width, height;
	private ButtonTexture texture;
	private ScrollingLabel buttonText;
	private Vec2i textOffsetFromCenter;
	private Consumer<MouseButton> onClickCallback;
	private List<Component> tooltipLines;
	
	public VariableButton(int parentX, int parentY, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, ButtonTexture texture, String buttonText, Vec2i textOffsetFromCenter, Consumer<MouseButton> onClickCallback, List<Component> tooltipLines){
		this.parentX = parentX;
		this.parentY = parentY;
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.texture = texture;
		this.textOffsetFromCenter = textOffsetFromCenter;
		this.onClickCallback = onClickCallback;
		this.tooltipLines = tooltipLines;
		
		this.buttonText = new ScrollingLabel(()->parentX + x.getAsInt() + textOffsetFromCenter.getX() + (width.getAsInt()>>1) - (Math.min(getStringWidth(buttonText), getMaxTextWidth().getAsInt())>>1), ()->parentY + y.getAsInt() + textOffsetFromCenter.getY() + (height.getAsInt()>>1) - (Minecraft.getInstance().font.lineHeight>>1), buttonText, ()->Math.min(getMaxTextWidth().getAsInt(), getStringWidth(buttonText)), 30, 1);
	}
	
	public VariableButton(IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, ButtonTexture texture, String buttonText, Vec2i textOffsetFromCenter, Consumer<MouseButton> onClickCallback, List<Component> tooltipLines){
		this(0, 0, x, y, width, height, texture, buttonText, textOffsetFromCenter, onClickCallback, tooltipLines);
	}
	
	private IntSupplier getMaxTextWidth(){
		return ()->width.getAsInt() - ((textOffsetFromCenter.getX()+2)*2);
	}
	
	private int getStringWidth(String text){
		return Minecraft.getInstance().font.width(text);
	}
	
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		internalRender(matrixStack, mouseX, mouseY, partialTicks, texture);
	}
	
	@Override
	public void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, x.getAsInt(), y.getAsInt(), x.getAsInt()+width.getAsInt(), y.getAsInt()+height.getAsInt())){
			TooltipBuffer.tooltipBuffer.add(()->{
				Minecraft.getInstance().screen.renderComponentTooltip(matrixStack, tooltipLines, mouseX, mouseY);
			});
		}
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, x.getAsInt(), y.getAsInt(), x.getAsInt()+width.getAsInt(), y.getAsInt()+height.getAsInt()) && texture == ButtonTexture.DEFAULT_NORMAL){
			internalRender(matrixStack, mouseX, mouseY, partialTicks, ButtonTexture.DEFAULT_PRESSED);
		}
	}
	
	private void internalRender(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, ButtonTexture texture){
		Color.color(0xFFFFFFFF);
		Lighting.setupForFlatItems();//todo test
		
		int x = parentX + this.x.getAsInt();
		int y = parentY + this.y.getAsInt();
		
		int texU = texture.getU();
		int texV = texture.getV();
		int texWidth = texture.getTexWidth();
		int texHeight = texture.getTexHeight();
		int texP = texture.getBorderSize(); // P for Padding

		RenderSystem.setShaderTexture(0, texture.getTexture());
		
		int right = x + width.getAsInt() - texP;
		int bottom = y + height.getAsInt() - texP;
		int texRight = texU + texture.getWidth() - texP;
		int texBottom = texV + texture.getWidth() - texP;
		
		GuiComponent.blit(matrixStack, x, y, texU, texV, texP, texP, texWidth, texHeight);// Left Top corner
		GuiComponent.blit(matrixStack, right, y, texRight, texV, texP, texP, texWidth, texHeight);// Right Top corner
		GuiComponent.blit(matrixStack, right, bottom, texRight, texBottom, texP, texP, texWidth, texHeight);// Right Bottom corner
		GuiComponent.blit(matrixStack, x, bottom, texU, texBottom, texP, texP, texWidth, texHeight);// Left Bottom corner
		
		
		int strippedButtonTexWidth = texture.getWidth() - 2*texP;
		int strippedButtonTexHeight = texture.getHeight() - 2*texP;
		
		for (int left = x + texP; left < right; left += strippedButtonTexWidth) {// Fill the Middle
			for (int top = y + texP; top < bottom; top += strippedButtonTexHeight) {
				GuiComponent.blit(matrixStack, left, top, texU + texP, texV + texP, Math.min(strippedButtonTexWidth, right - left), Math.min(strippedButtonTexHeight, bottom - top), texWidth, texHeight);
			}
		}
		for (int left = x + texP; left < right; left += strippedButtonTexWidth) {// Top and Bottom Edges
			GuiComponent.blit(matrixStack, left, y, texU + texP, texV, Math.min(strippedButtonTexWidth, right - left), texP, texWidth, texHeight);// Top
			GuiComponent.blit(matrixStack, left, bottom, texU + texP, texBottom, Math.min(strippedButtonTexWidth, right - left), texP, texWidth, texHeight);// Bottom
		}
		for (int top = y + texP; top < bottom; top += strippedButtonTexHeight) {// Left and Right Edges
			GuiComponent.blit(matrixStack, x, top, texU, texV + texP, texP, Math.min(strippedButtonTexHeight, bottom - top), texWidth, texHeight);// Left
			GuiComponent.blit(matrixStack, right, top, texRight, texV + texP, texP, Math.min(strippedButtonTexHeight, bottom - top), texWidth, texHeight);// Right
		}
		buttonText.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton){
		int x = parentX + this.x.getAsInt();
		int y = parentY + this.y.getAsInt();
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, x, y, x+width.getAsInt(), y+height.getAsInt())){
			if(onClickCallback != null){
				onClickCallback.accept(MouseButton.getButtonFromGlButton(mouseButton));
				return true;
			}
		}
		return false;
	}
	
	public IntSupplier getX(){
		return x;
	}
	
	public IntSupplier getY(){
		return y;
	}
	
	public IntSupplier getWidth(){
		return width;
	}
	
	public IntSupplier getHeight(){
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
	
	public List<Component> getTooltipLines(){
		return tooltipLines;
	}
	
	public void setX(IntSupplier x){
		this.x = x;
		this.buttonText.setX(()->parentX + x.getAsInt() + textOffsetFromCenter.getX() + (width.getAsInt()>>1) - (getStringWidth(buttonText.getText())>>1));
	}
	
	public void setY(IntSupplier y){
		this.y = y;
		this.buttonText.setY(()->parentY + y.getAsInt() + textOffsetFromCenter.getY() + (height.getAsInt()>>1) - (Minecraft.getInstance().font.lineHeight>>1));
	}
	
	public void setWidth(IntSupplier width){
		this.width = width;
	}
	
	public void setHeight(IntSupplier height){
		this.height = height;
	}
	
	public void setTexture(ButtonTexture texture){
		this.texture = texture;
	}
	
	public void setButtonText(String buttonText){
		this.buttonText = new ScrollingLabel(()->x.getAsInt() + textOffsetFromCenter.getX() + (width.getAsInt()>>1) - (getStringWidth(buttonText)>>1), ()->y.getAsInt() + textOffsetFromCenter.getY() + (height.getAsInt()>>1) - (Minecraft.getInstance().font.lineHeight>>1), buttonText, getMaxTextWidth(), 30, 1);
	}
	
	public void setTextOffsetFromCenter(Vec2i textOffsetFromCenter){
		this.textOffsetFromCenter = textOffsetFromCenter;
	}
	
	public void setOnClickCallback(Consumer<MouseButton> onClickCallback){
		this.onClickCallback = onClickCallback;
	}
	
	public void setTooltipLines(List<Component> tooltipLines){
		this.tooltipLines = tooltipLines;
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