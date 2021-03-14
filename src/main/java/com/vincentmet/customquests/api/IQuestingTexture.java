package com.vincentmet.customquests.api;

public interface IQuestingTexture extends IResourceLocationProvider{
	boolean isValid();
	void render(float scale, int x, int y, int mouseX, int mouseY);
}