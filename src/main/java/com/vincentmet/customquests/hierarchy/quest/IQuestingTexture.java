package com.vincentmet.customquests.hierarchy.quest;

import com.vincentmet.customquests.api.IResourceLocationProvider;

public interface IQuestingTexture extends IResourceLocationProvider{
	boolean isValid();
	void render(int x, int y, int mouseX, int mouseY);
}