package com.vincentmet.customquests.gui.elements;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.IButtonShape;
import com.vincentmet.customquests.api.IQuestingTexture;
import com.vincentmet.customquests.helpers.MouseButton;
import com.vincentmet.customquests.helpers.TooltipBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

public class QuestButton implements MovableScalableCanvasEntry{
	private int parentX;
	private int parentY;
	
	private State buttonState;
	private IButtonShape shape;
	private List<Component> tooltipLines;
	private Consumer<MouseButton> onClickCallback;
	private int x, y;
	private final IQuestingTexture icon;
	private final int questId;
	private final float buttonScale;
	
	public QuestButton(int parentX, int parentY, int x, int y, int questId, IQuestingTexture icon, IButtonShape shape, State buttonState, float scale, Consumer<MouseButton> onClickCallback, List<Component> tooltipLines){
		this.parentX = parentX;
		this.parentY = parentY;
		
		this.x = x;
		this.y = y;
		this.icon = icon;
		this.questId = questId;
		this.buttonState = buttonState;
		this.shape = shape;
		this.buttonScale = scale;
		this.onClickCallback = onClickCallback;
		this.tooltipLines = tooltipLines;
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
		matrixStack.pushPose();
		matrixStack.translate(parentX + x, parentY + y, 0);
		matrixStack.scale(buttonScale, buttonScale, 1);
		matrixStack.translate(-(parentX + x), -(parentY + y), 0);
		RenderSystem.setShaderTexture(0, shape.getTexture());
		if((buttonState.equals(State.NORMAL) || buttonState.equals(State.HOVER)) && ApiUtils.isMouseInBounds(mouseX, mouseY, parentX + x, parentY + y, x + parentX + (int)(buttonState.WIDTH * buttonScale), y + parentY + (int)(buttonState.HEIGHT * buttonScale))){
			buttonState = State.HOVER;
		}else if(buttonState.equals(State.NORMAL) || buttonState.equals(State.HOVER)){
			buttonState = State.NORMAL;
		}
		GuiComponent.blit(matrixStack, parentX + x, parentY + y, buttonState.u, buttonState.v, buttonState.WIDTH, buttonState.HEIGHT, 72, 72);
		icon.render(matrixStack, buttonScale, parentX + x, parentY + y, 0.25f, 0.25f, mouseX, mouseY);
		matrixStack.popPose();
	}
	
	@Override
	public void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, parentX + x, parentY + y, parentX + x + (int)(buttonState.WIDTH * buttonScale), parentY + y + (int)(buttonState.HEIGHT * buttonScale))){
			TooltipBuffer.tooltipBuffer.add(()->{
				if(Minecraft.getInstance().screen != null) Minecraft.getInstance().screen.renderComponentTooltip(matrixStack, tooltipLines, mouseX, mouseY);
			});
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		int x = parentX + this.x;
		int y = parentY + this.y;
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, x, y, x + (int)(buttonState.WIDTH * buttonScale), y + (int)(buttonState.HEIGHT * buttonScale))){
			if(onClickCallback != null){
				onClickCallback.accept(MouseButton.getButtonFromGlButton(button));
				return true;
			}
		}
		return false;
	}
	
	public int getQuestId(){
		return questId;
	}
	
	public enum State{
		NORMAL(0, 0),
		HOVER(24, 0),
		DISABLED(48, 0),
		BLUE(0, 24),
		GREEN(24, 24)
		;
		
		int u, v;
		final int WIDTH = IButtonShape.WIDTH, HEIGHT = IButtonShape.HEIGHT;
		
		State(int u, int v){
			this.u = u;
			this.v = v;
		}
	}
}
