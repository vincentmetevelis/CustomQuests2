package com.vincentmet.customquests.api;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IItemStacksProvider{
	List<ItemStack> getItemStacks();
}