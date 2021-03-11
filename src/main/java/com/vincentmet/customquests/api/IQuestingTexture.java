package com.vincentmet.customquests.api;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IQuestingTexture extends IResourceLocationProvider{
	boolean isValid();
	void render(MatrixStack stack, float scale, int x, int y, int mouseX, int mouseY);
}