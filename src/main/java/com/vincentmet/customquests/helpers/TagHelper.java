package com.vincentmet.customquests.helpers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public class TagHelper{
	public static List<Item> getEntries(ResourceLocation tag){
		List<Item> result = new ArrayList<>();
		if(doesTagExist(tag)){
			result.addAll(ItemTags.getAllTags().getTag(tag).getValues());
		}
		return result;
	}
	
	public static boolean doesTagExist(ResourceLocation tag){
		return tag != null && ItemTags.getAllTags().getTag(tag) != null;
	}
}
