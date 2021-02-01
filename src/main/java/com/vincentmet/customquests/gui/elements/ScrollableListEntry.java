package com.vincentmet.customquests.gui.elements;

import com.vincentmet.customquests.api.IHoverRenderable;
import net.minecraft.client.gui.IGuiEventListener;

public interface ScrollableListEntry extends IHoverRenderable, IGuiEventListener{
	void setX(int x);
	void setY(int y);
	int getX();
	int getY();
	int getWidth();
	int getHeight();
	void setWidth(int width);
	void setHeight(int height);
}