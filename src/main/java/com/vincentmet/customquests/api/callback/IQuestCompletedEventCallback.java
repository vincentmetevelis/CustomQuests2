package com.vincentmet.customquests.api.callback;

import net.minecraft.entity.player.PlayerEntity;

public interface IQuestCompletedEventCallback{
	void execute(int questId, PlayerEntity player);
}