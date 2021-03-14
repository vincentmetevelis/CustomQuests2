package com.vincentmet.customquests.api;

import net.minecraft.util.ResourceLocation;

public interface IButtonShape{
    int WIDTH = 24;
    int HEIGHT = 24;
    ResourceLocation getId();
    ResourceLocation getTexture();
}