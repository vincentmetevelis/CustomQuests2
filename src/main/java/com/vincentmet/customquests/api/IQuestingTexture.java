package com.vincentmet.customquests.api;

import com.mojang.blaze3d.vertex.PoseStack;

public interface IQuestingTexture extends IResourceLocationProvider{
	boolean isValid();
	void render(PoseStack stack, float scale, int x, int y, int mouseX, int mouseY);
}