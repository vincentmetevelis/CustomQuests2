package com.vincentmet.customquests;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ref.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventHandler{
    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event){
        //Main
        event.getRegistry().registerAll(Objects.Items.QUESTING_DEVICE);
        event.getRegistry().registerAll(Objects.ItemBlocks.QUESTING_BLOCK);
        //Standard Content
        //event.getRegistry().registerAll(Objects.ItemBlocks.DELIVERY_BLOCK);
    }
    
    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event){
        //Main
        event.getRegistry().registerAll(Objects.Blocks.QUESTING_BLOCK);
        //Standard Content
        //event.getRegistry().registerAll(Objects.Blocks.DELIVERY_BLOCK);
    }
    
    @SubscribeEvent
    public static void registerTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event){
        //Standard Content
        //event.getRegistry().registerAll(Objects.TileEntities.DELIVERY_BLOCK);
    }
	
	/*@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event){
		//SoundEvent se = new SoundEvent(new ResourceLocation(Ref.MODID, "quest0")).setRegistryName(new ResourceLocation(Ref.MODID, "quest0"));
		//QuestingStorage.SOUNDS.put("quest0", se); //todo SOUNDS make this some kind of modular system
		//Main
		event.getRegistry().registerAll(
				//QuestingStorage.SOUNDS.get("quest0")
		);
	}*/
}