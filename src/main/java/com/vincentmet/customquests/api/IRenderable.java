package com.vincentmet.customquests.api;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IRenderable{
    void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks);
}