package com.vincentmet.customquests.gui.elements;

import com.vincentmet.customquests.api.IHoverRenderable;
import net.minecraft.client.gui.IGuiEventListener;

public interface MovableScalableCanvasEntry extends IHoverRenderable, IGuiEventListener{
	int getParentX();
	int getParentY();
	int getX();
	int getY();
	int getWidth();
	int getHeight();
}