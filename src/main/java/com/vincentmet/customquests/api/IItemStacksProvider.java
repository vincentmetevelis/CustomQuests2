package com.vincentmet.customquests.api;

import java.util.List;
import net.minecraft.item.ItemStack;

public interface IItemStacksProvider{
	List<ItemStack> getItemStacks();
}