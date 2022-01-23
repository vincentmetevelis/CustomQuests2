package com.vincentmet.customquests.gui.elements;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.helpers.MouseButton;
import com.vincentmet.customquests.helpers.math.Vec2i;
import com.vincentmet.customquests.helpers.rendering.Color;
import com.vincentmet.customquests.helpers.rendering.VariableButton;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

public class TextButton implements ScrollableListEntry{
	private IntSupplier x, y, width, height;
	private final VariableButton button;
	
	public TextButton(IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, String text, ButtonState buttonState, Consumer<MouseButton> onClickCallback, List<Component> tooltipLines){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.button = new VariableButton(0, 0, x, y, width, height, buttonState.getButtonTexture(), text, new Vec2i(0, 0), onClickCallback, tooltipLines);
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
		matrixStack.pushPose();
		Color.color(0xFFFFFFFF);
		this.button.render(matrixStack, mouseX, mouseY, partialTicks);
		Lighting.setupFor3DItems();
		matrixStack.popPose();
	}
	
	@Override
	public void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
		matrixStack.pushPose();
		Color.color(0xFFFFFFFF);
		this.button.renderHover(matrixStack, mouseX, mouseY, partialTicks);
		Lighting.setupFor3DItems();
		matrixStack.popPose();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		return this.button.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public void setX(IntSupplier x){
		this.x = x;
		this.button.setX(x);
	}
	
	@Override
	public void setY(IntSupplier y){
		this.y = y;
		this.button.setY(y);
	}
	
	@Override
	public IntSupplier getX(){
		return x;
	}
	
	@Override
	public IntSupplier getY(){
		return y;
	}
	
	@Override
	public IntSupplier getWidth(){
		return width;
	}
	
	@Override
	public IntSupplier getHeight(){
		return height;
	}
	
	@Override
	public void setWidth(IntSupplier width){
		this.width = width;
		this.button.setWidth(width);
	}
	
	@Override
	public void setHeight(IntSupplier height){
		this.height = height;
		this.button.setHeight(height);
	}
	
	public void setButtonState(ButtonState buttonState){
		this.button.setTexture(buttonState.getButtonTexture());
	}
}
