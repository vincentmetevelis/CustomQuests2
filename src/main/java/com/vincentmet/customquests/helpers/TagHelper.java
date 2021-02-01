package com.vincentmet.customquests.helpers;

import java.util.*;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class TagHelper{
	public static List<Item> getEntries(ResourceLocation tag){
		List<Item> result = new ArrayList<>();
		if(doesTagExist(tag)){
			result.addAll(ItemTags.getCollection().get(tag).getAllElements());
		}
		return result;
	}
	
	public static boolean doesTagExist(ResourceLocation tag){
		return ItemTags.getCollection().get(tag) != null;
	}
}
