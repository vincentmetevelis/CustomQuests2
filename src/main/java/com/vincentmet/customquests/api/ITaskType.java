package com.vincentmet.customquests.api;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.helpers.*;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public interface ITaskType extends IJsonObjectProcessor, IJsonObjectProvider{
	ResourceLocation getId();
	Component getTranslation();
	List<PlayerBoundSubtaskReference> getCurrentlyTrackingList();
	boolean hasButton(ButtonContext context);
	void executeSubtaskCheck(Player player, Object object);
	void executeSubtaskButton(Player player);
	IQuestingTexture getIcon(LocalPlayer player);
	Runnable onSlotHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks, LocalPlayer player);
	String getText(LocalPlayer player);
	int getCompletionAmount();
	Consumer<MouseButton> onSlotClick(LocalPlayer player);
}