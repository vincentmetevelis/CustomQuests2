package com.vincentmet.customquests.gui.elements;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.*;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

public class QuestButton implements MovableScalableCanvasEntry{
	private int parentX;
	private int parentY;
	
	private State buttonState;
	private IButtonShape shape;
	private List<ITextComponent> tooltipLines;
	private Consumer<MouseButton> onClickCallback;
	private int x, y;
	private final IQuestingTexture icon;
	private final int questId;
	private final float buttonScale;
	
	public QuestButton(int parentX, int parentY, int x, int y, int questId, IQuestingTexture icon, IButtonShape shape, State buttonState, float scale, Consumer<MouseButton> onClickCallback, List<ITextComponent> tooltipLines){
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
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		GL11.glPushMatrix();
		GL11.glTranslated(parentX + x, parentY + y, 0);
		GL11.glScalef(buttonScale, buttonScale, 1);
		GL11.glTranslated(-(parentX + x), -(parentY + y), 0);
		Minecraft.getInstance().getTextureManager().bindTexture(shape.getTexture());
		if((buttonState.equals(State.NORMAL) || buttonState.equals(State.HOVER)) && ApiUtils.isMouseInBounds(mouseX, mouseY, parentX + x, parentY + y, x + parentX + (int)(buttonState.WIDTH * buttonScale), y + parentY + (int)(buttonState.HEIGHT * buttonScale))){
			buttonState = State.HOVER;
		}else if(buttonState.equals(State.NORMAL) || buttonState.equals(State.HOVER)){
			buttonState = State.NORMAL;
		}
		AbstractGui.blit(matrixStack, parentX + x, parentY + y, buttonState.u, buttonState.v, buttonState.WIDTH, buttonState.HEIGHT, 72, 72);
		icon.render(matrixStack, buttonScale, parentX + x + 4, parentY + y + 4, mouseX, mouseY);
		GL11.glPopMatrix();
	}
	
	@Override
	public void renderHover(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, parentX + x, parentY + y, parentX + x + (int)(buttonState.WIDTH * buttonScale), parentY + y + (int)(buttonState.HEIGHT * buttonScale))){
			TooltipBuffer.tooltipBuffer.add(()->{
				if(Minecraft.getInstance().currentScreen != null) Minecraft.getInstance().currentScreen.func_243308_b(matrixStack, tooltipLines, mouseX, mouseY);
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
