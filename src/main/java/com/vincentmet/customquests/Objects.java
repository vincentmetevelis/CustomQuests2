package com.vincentmet.customquests;

import com.vincentmet.customquests.block.QuestingBlock;
import com.vincentmet.customquests.item.QuestingDevice;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class Objects{
	public static final class ItemBlocks{
		public static final Item QUESTING_BLOCK = new BlockItem(Blocks.QUESTING_BLOCK, new Item.Properties().tab(ItemGroups.cqTab)).setRegistryName(new ResourceLocation(Ref.MODID, "questing_block"));
		//public static final Item DELIVERY_BLOCK = new BlockItem(Blocks.DELIVERY_BLOCK, new Item.Properties().group(ItemGroups.cqTab)).setRegistryName(new ResourceLocation(Ref.MODID, "delivery_block"));
	}
	
	public static final class Items{
		public static final Item QUESTING_DEVICE = new QuestingDevice(new Item.Properties().tab(ItemGroups.cqTab)).setRegistryName(new ResourceLocation(Ref.MODID, "questing_device"));
	}
	
	public static final class Blocks{
		public static final Block QUESTING_BLOCK = new QuestingBlock().setRegistryName(new ResourceLocation(Ref.MODID, "questing_block"));
		//public static final Block DELIVERY_BLOCK = new DeliveryBlock().setRegistryName(new ResourceLocation(Ref.MODID, "delivery_block"));
	}
	
	public static final class TileEntities{
		//public static final TileEntityType<?> DELIVERY_BLOCK = TileEntityType.Builder.create(DeliveryBlockTileEntity::new, Blocks.DELIVERY_BLOCK).build(null).setRegistryName(new ResourceLocation(Ref.MODID, "delivery_block"));
	}
	
	public static final class ItemGroups{
		public static final CreativeModeTab cqTab = new CreativeModeTab("customquests"){
			@Override
			public ItemStack makeIcon(){
				return new ItemStack(Items.QUESTING_DEVICE);
			}
		};
	}
	
	public static final class KeyBinds{
		public static final KeyMapping OPEN_QUESTING_SCREEN = new KeyMapping("customquests.keys.open_quests", 67, "customquests.keys.category");
		public static final KeyMapping CLAIM_ALL_REWARDS = new KeyMapping("customquests.keys.claim_all_rewards", 86, "customquests.keys.category");
	}
}
