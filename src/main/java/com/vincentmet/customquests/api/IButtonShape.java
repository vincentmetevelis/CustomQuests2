package com.vincentmet.customquests.api;

import net.minecraft.resources.ResourceLocation;

public interface IButtonShape{
    int WIDTH = 24;
    int HEIGHT = 24;
    ResourceLocation getId();
    ResourceLocation getTexture();
}