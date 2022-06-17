package com.vincentmet.customquests.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.IHoverRenderable;
import com.vincentmet.customquests.helpers.CQGuiEventListener;
import com.vincentmet.customquests.helpers.IntCounter;
import com.vincentmet.customquests.helpers.rendering.GLScissorStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

@OnlyIn(Dist.CLIENT)
public class ScrollableList implements CQGuiEventListener, IHoverRenderable{
	private IntSupplier x, y, width, height;
	private int scrollDistance = 0;
	private final List<ScrollableListEntry> entries = new ArrayList<>();
	
	public ScrollableList(IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
		GLScissorStack.push(matrixStack, x.getAsInt(), y.getAsInt(), width.getAsInt(), height.getAsInt());
		entries.forEach(entry->{
			entry.render(matrixStack, mouseX, mouseY, partialTicks);
		});
		GLScissorStack.pop(matrixStack);
	}
	
	public void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
		GLScissorStack.push(matrixStack, x.getAsInt(), y.getAsInt(), width.getAsInt(), height.getAsInt());
		entries.forEach(entry->{
			entry.renderHover(matrixStack, mouseX, mouseY, partialTicks);
		});
		GLScissorStack.pop(matrixStack);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		entries.forEach(entry -> {
			entry.mouseClicked(mouseX, mouseY, button);
		});
		return true;
	}
	
	public boolean mouseScrolled(double mouseX, double mouseY, double dyScroll){
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, x.getAsInt(), y.getAsInt(), x.getAsInt() + width.getAsInt(), y.getAsInt() + height.getAsInt())){
			scrollDistance -= dyScroll*getScrollAmount();
		}
		
		applyScrollLimits();
		return true;
	}
	
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY){
		return true;
	}
	
	public int getContentHeight(){
		IntCounter _height = new IntCounter(0);
		entries.forEach(entry->{
			_height.add(entry.getHeight().getAsInt());
		});
		if(_height.getValue() < height.getAsInt()){
			_height.setValue(height.getAsInt());
		}
		return _height.getValue();
	}
	
	private int getMaxScroll(){
		return Math.max(this.getContentHeight() - height.getAsInt(), 0);
	}
	
	private void applyScrollLimits(){
		if(this.scrollDistance < 0){
			this.scrollDistance = 0;
		}
		
		if(this.scrollDistance > getMaxScroll()){
			this.scrollDistance = getMaxScroll();
		}
	}
	
	private int getScrollAmount(){
		return 10;
	}
	
	public ScrollableList add(ScrollableListEntry entry){
		entries.add(entry);
		return this;
	}
	
	public ScrollableList clear(){
		entries.clear();
		return this;
	}
	
	public List<ScrollableListEntry> getEntriesCopy(){
		return new ArrayList<>(entries);
	}
	
	public List<ScrollableListEntry> getEntries(){
		return entries;
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
	
	public void setX(IntSupplier x){
		this.x = x;
	}
	
	public void setY(IntSupplier y){
		this.y = y;
	}
	
	public void setWidth(IntSupplier width){
		this.width = width;
		entries.forEach(entry -> entry.setWidth(width));
	}
	
	public void setHeight(IntSupplier height){
		this.height = height;
		applyScrollLimits();
	}
	
	public int getScrollDistance(){
		return scrollDistance;
	}
	
	public void setScrollDistance(int scrollDistance){
		this.scrollDistance = scrollDistance;
		applyScrollLimits();
	}
}