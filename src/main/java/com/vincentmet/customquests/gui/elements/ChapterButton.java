package com.vincentmet.customquests.gui.elements;

import com.vincentmet.customquests.api.IQuestingTexture;
import com.vincentmet.customquests.helpers.MouseButton;
import com.vincentmet.customquests.helpers.math.Vec2i;
import com.vincentmet.customquests.helpers.rendering.*;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

public class ChapterButton implements ScrollableListEntry{
	private final VariableButton button;
	private int x, y, width, height;
	private final IQuestingTexture icon;
	
	public ChapterButton(int x, int y, int width, int height, IQuestingTexture icon, String text, ButtonState buttonState, Consumer<MouseButton> onClickCallback, List<ITextComponent> tooltipLines){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.icon = icon;
		this.button = new VariableButton(0, 0, x, y, width, height, buttonState.getButtonTexture(), text, new Vec2i(10, 0), onClickCallback, tooltipLines);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks){
		GL11.glPushMatrix();
		Color.color(0xFFFFFF);
		this.button.render(mouseX, mouseY, partialTicks);
		RenderHelper.setupGui3DDiffuseLighting();
		icon.render(1, x+2, y+2, mouseX, mouseY);
		GL11.glPopMatrix();
	}
	
	@Override
	public void renderHover(int mouseX, int mouseY, float partialTicks){
		GL11.glPushMatrix();
		Color.color(0xFFFFFF);
		this.button.renderHover(mouseX, mouseY, partialTicks);
		RenderHelper.setupGui3DDiffuseLighting();
		icon.render(1, x+2, y+2, mouseX, mouseY);
		GL11.glPopMatrix();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		return this.button.mouseClicked(mouseX, mouseY, button);
	}
	
	@Override
	public void setX(int x){
		this.x = x;
		this.button.setX(x);
	}
	
	@Override
	public void setY(int y){
		this.y = y;
		this.button.setY(y);
	}
	
	@Override
	public int getX(){
		return x;
	}
	
	@Override
	public int getY(){
		return y;
	}
	
	@Override
	public int getWidth(){
		return width;
	}
	
	@Override
	public void setWidth(int width){
		this.width = width;
		this.button.setWidth(width);
	}
	
	@Override
	public void setHeight(int height){
		this.height = height;
		this.button.setHeight(height);
	}
	
	@Override
	public int getHeight(){
		return height;
	}
	
	public IQuestingTexture getIcon(){
		return icon;
	}
	
	public void setButtonState(ButtonState buttonState){
		this.button.setTexture(buttonState.getButtonTexture());
	}
}
