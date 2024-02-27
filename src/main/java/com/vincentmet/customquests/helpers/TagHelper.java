package com.vincentmet.customquests.helpers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TagHelper{
	public static class Items{
		public static List<Item> getEntries(ResourceLocation tag){
			ITagManager<Item> itemTagRegistry = ForgeRegistries.ITEMS.tags();
			TagKey<Item> tagKey = getTagKeyFromRL(tag);
			List<Item> result = new ArrayList<>();
			if(itemTagRegistry != null  && doesTagExist(tagKey)){
				result.addAll(itemTagRegistry.getTag(tagKey).stream().toList());
			}
			return result;
		}

		public static boolean doesTagExist(ResourceLocation tag){
			return getTagKeyFromRL(tag)!=null;
		}

		public static boolean doesTagExist(TagKey<Item> tag){
			return tag!=null;
		}

		public static TagKey<Item> getTagKeyFromRL(ResourceLocation tagRL){
			ITagManager<Item> itemTagRegistry = ForgeRegistries.ITEMS.tags();
			if(itemTagRegistry != null && tagRL != null){
				Optional<TagKey<Item>> optional = itemTagRegistry.getTagNames().filter(itemTagKey -> itemTagKey.location().equals(tagRL)).findFirst();
				if(optional.isPresent()){
					return optional.get();
				}
			}
			return null;
		}
	}
}