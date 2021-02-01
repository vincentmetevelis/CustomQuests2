package com.vincentmet.customquests.item;

import com.vincentmet.customquests.api.ClientUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class QuestingDevice extends Item{
	public QuestingDevice(Properties properties){
		super(properties);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand){
		if(world.isRemote){
			ClientUtils.openQuestingScreen();
		}
		return super.onItemRightClick(world, player, hand);
	}
}