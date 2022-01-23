package com.vincentmet.customquests.helpers.rendering;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

public class ItemRenderHelper {
    public static void renderGuiItem(ItemStack itemStack, int x, int y, double scale, float offsetX, float offsetY) {
        renderGuiItem(itemStack, x, y, Minecraft.getInstance().getItemRenderer().getModel(itemStack, null, null, 0), scale, offsetX, offsetY);
    }

    private static void renderGuiItem(ItemStack itemStack, int x, int y, BakedModel bakedModel, double scale, float offsetX, float offsetY) {
        Minecraft.getInstance().textureManager.getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Color.color(0xFFFFFFFF);
        PoseStack itemPoseStack = RenderSystem.getModelViewStack();
        itemPoseStack.pushPose();
        itemPoseStack.translate(x, y, (100.0F + Minecraft.getInstance().getItemRenderer().blitOffset));
        itemPoseStack.scale((float)scale, (float)scale, 1);
        itemPoseStack.translate(8.0D, 8.0D, 0.0D);
        itemPoseStack.scale(1.0F, -1.0F, 1.0F);
        itemPoseStack.scale(16.0F, 16.0F, 16.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack newStack = new PoseStack();
        newStack.translate(offsetX, -offsetY, 0);
        MultiBufferSource.BufferSource buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !bakedModel.usesBlockLight();
        if (flag) {
            Lighting.setupForFlatItems();
        }
        Minecraft.getInstance().getItemRenderer().render(itemStack, ItemTransforms.TransformType.GUI, false, newStack, buffersource, 0xF000F0, OverlayTexture.NO_OVERLAY, bakedModel);
        buffersource.endBatch();
        RenderSystem.enableDepthTest();
        if (flag) {
            Lighting.setupFor3DItems();
        }

        itemPoseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}