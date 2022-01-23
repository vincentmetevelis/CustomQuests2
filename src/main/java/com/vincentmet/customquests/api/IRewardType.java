package com.vincentmet.customquests.api;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.helpers.MouseButton;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

public interface IRewardType extends IJsonObjectProcessor, IJsonObjectProvider{
	ResourceLocation getId();
	void executeReward(Player player);
	Item getIcon();
	String getText();
	Runnable onSlotHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks);
	Consumer<MouseButton> onSlotClick();
	
}