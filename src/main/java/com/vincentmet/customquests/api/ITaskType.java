package com.vincentmet.customquests.api;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.vincentmet.customquests.helpers.*;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public interface ITaskType extends IJsonObjectProcessor, IJsonObjectProvider{
	ResourceLocation getId();
	ITextComponent getTranslation();
	List<PlayerBoundSubtaskReference> getCurrentlyTrackingList();
	boolean hasButton(ButtonContext context);
	void executeSubtaskCheck(PlayerEntity player, Object object);
	void executeSubtaskButton(PlayerEntity player);
	IQuestingTexture getIcon(ClientPlayerEntity player);
	Runnable onSlotHover(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, ClientPlayerEntity player);
	String getText(ClientPlayerEntity player);
	int getCompletionAmount();
	Consumer<MouseButton> onSlotClick(ClientPlayerEntity player);
}