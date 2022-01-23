package com.vincentmet.customquests.api;

import com.mojang.blaze3d.vertex.PoseStack;

public interface IHoverRenderable extends IRenderable{
    void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks);
}