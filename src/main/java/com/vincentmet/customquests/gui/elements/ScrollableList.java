package com.vincentmet.customquests.gui.elements;

import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.IntCounter;
import com.vincentmet.customquests.helpers.rendering.GLScissorStack;
import java.util.*;
import net.minecraft.client.gui.*;
import net.minecraftforge.api.distmarker.*;

@OnlyIn(Dist.CLIENT)
public class ScrollableList implements IGuiEventListener, IHoverRenderable{
	private int x, y, width, height;
	private int scrollDistance = 0;
	private final List<ScrollableListEntry> entries = new ArrayList<>();
	
	public ScrollableList(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void render(int mouseX, int mouseY, float partialTicks){
		GLScissorStack.push(x, y, width, height);
		entries.forEach(entry->{
			entry.render(mouseX, mouseY, partialTicks);
		});
		GLScissorStack.pop();
	}
	
	public void renderHover(int mouseX, int mouseY, float partialTicks){
		GLScissorStack.push(x, y, width, height);
		entries.forEach(entry->{
			entry.renderHover(mouseX, mouseY, partialTicks);
		});
		GLScissorStack.pop();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		entries.forEach(entry -> {
			entry.mouseClicked(mouseX, mouseY, button);
		});
		return true;
	}
	
	public boolean mouseScrolled(double mouseX, double mouseY, double dyScroll){
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, x, y, x + width, y + height)){
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
			_height.add(entry.getHeight());
		});
		if(_height.getValue() < height){
			_height.setValue(height);
		}
		return _height.getValue();
	}
	
	private int getMaxScroll(){
		return Math.max(this.getContentHeight() - height, 0);
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
	
	public void setX(int x){
		this.x = x;
	}
	
	public void setY(int y){
		this.y = y;
	}
	
	public void setWidth(int width){
		this.width = width;
		entries.forEach(entry -> entry.setWidth(width));
	}
	
	public void setHeight(int height){
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