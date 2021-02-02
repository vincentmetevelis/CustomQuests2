package com.vincentmet.customquests.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.api.distmarker.*;

@OnlyIn(Dist.CLIENT)
public interface IRenderable{
    void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks);
}