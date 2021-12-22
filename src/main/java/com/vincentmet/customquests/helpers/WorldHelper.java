package com.vincentmet.customquests.helpers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;

public class WorldHelper{
    public static boolean isPlayerInDimension(Player player, ResourceLocation dimension){
        return player.getCommandSenderWorld().dimension().location().equals(dimension);
    }
    
    public static boolean isPlayerInBiome(Player player, ResourceLocation biome){
        return player.getCommandSenderWorld().getBiome(player.blockPosition()).getRegistryName().equals(biome);
    }
    
    public static boolean isPlayerInRange(Player player, int x, int y, int z, int range){
        return (player.getX() >= x-range && player.getX() <= x+range) && (player.getY() >= y-range && player.getY() <= y+range) && (player.getZ() >= z-range && player.getZ() <= z+range);
    }
}
