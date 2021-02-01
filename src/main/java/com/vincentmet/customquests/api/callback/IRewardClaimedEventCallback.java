package com.vincentmet.customquests.api.callback;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

public interface IRewardClaimedEventCallback{
	void execute(ResourceLocation type, int questId, PlayerEntity player);
}