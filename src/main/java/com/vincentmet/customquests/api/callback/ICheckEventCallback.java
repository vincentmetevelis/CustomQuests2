package com.vincentmet.customquests.api.callback;

import net.minecraft.entity.player.PlayerEntity;

public interface ICheckEventCallback{
	void execute(PlayerEntity player);
}