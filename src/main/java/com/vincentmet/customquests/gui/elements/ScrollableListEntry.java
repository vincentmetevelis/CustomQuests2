package com.vincentmet.customquests.gui.elements;

import com.vincentmet.customquests.api.IHoverRenderable;
import net.minecraft.client.gui.components.events.GuiEventListener;

public interface ScrollableListEntry extends IHoverRenderable, GuiEventListener{
	void setX(int x);
	void setY(int y);
	int getX();
	int getY();
	int getWidth();
	int getHeight();
	void setWidth(int width);
	void setHeight(int height);
}