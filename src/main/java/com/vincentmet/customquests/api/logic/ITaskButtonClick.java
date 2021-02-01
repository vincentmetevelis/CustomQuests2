package com.vincentmet.customquests.api.logic;

import net.minecraft.entity.player.PlayerEntity;

public interface ITaskButtonClick{
	void execute(PlayerEntity player, int questId, int taskId, int subtaskId);
}
