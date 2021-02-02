package com.vincentmet.customquests.api;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IHoverRenderable extends IRenderable{
    void renderHover(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks);
}