package com.vincentmet.customquests.item;

import com.vincentmet.customquests.api.ClientUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class QuestingDevice extends Item{
	public QuestingDevice(Properties properties){
		super(properties);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		if(world.isClientSide){
			ClientUtils.openQuestingScreen();
		}
		return super.use(world, player, hand);
	}
}