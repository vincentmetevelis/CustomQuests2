package com.vincentmet.customquests.api.callback;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

public interface ISubTaskCompletedEventCallback{
	void execute(ResourceLocation type, int questId, int taskId, int subtaskId, PlayerEntity player);
}