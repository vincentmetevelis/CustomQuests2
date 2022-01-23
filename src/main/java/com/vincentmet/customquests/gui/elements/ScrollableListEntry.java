package com.vincentmet.customquests.gui.elements;

import com.vincentmet.customquests.api.IHoverRenderable;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.function.IntSupplier;

public interface ScrollableListEntry extends IHoverRenderable, GuiEventListener {
	void setX(IntSupplier x);
	void setY(IntSupplier y);
	IntSupplier getX();
	IntSupplier getY();
	IntSupplier getWidth();
	IntSupplier getHeight();
	void setWidth(IntSupplier width);
	void setHeight(IntSupplier height);
}