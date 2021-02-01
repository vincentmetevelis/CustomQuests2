package com.vincentmet.customquests.api;

import net.minecraftforge.api.distmarker.*;

@OnlyIn(Dist.CLIENT)
public interface IRenderable{
    void render(int mouseX, int mouseY, float partialTicks);
}