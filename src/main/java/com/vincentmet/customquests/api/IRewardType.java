package com.vincentmet.customquests.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.vincentmet.customquests.helpers.MouseButton;
import java.util.function.Consumer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public interface IRewardType extends IJsonObjectProcessor, IJsonObjectProvider{
	ResourceLocation getId();
	void executeReward(PlayerEntity player);
	Item getIcon();
	String getText();
	Runnable onSlotHover(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks);
	Consumer<MouseButton> onSlotClick();
}