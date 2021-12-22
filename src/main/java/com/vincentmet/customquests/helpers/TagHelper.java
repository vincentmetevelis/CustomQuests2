package com.vincentmet.customquests.helpers;

import java.util.*;
import net.minecraft.world.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

public class TagHelper{
	public static List<Item> getEntries(ResourceLocation tag){
		List<Item> result = new ArrayList<>();
		if(doesTagExist(tag)){
			result.addAll(ItemTags.getAllTags().getTag(tag).getValues());
		}
		return result;
	}
	
	public static boolean doesTagExist(ResourceLocation tag){
		return ItemTags.getAllTags().getTag(tag) != null;
	}
}
