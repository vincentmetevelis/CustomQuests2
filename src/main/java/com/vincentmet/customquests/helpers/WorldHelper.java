package com.vincentmet.customquests.helpers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

public class WorldHelper{
    public static boolean isPlayerInDimension(PlayerEntity player, ResourceLocation dimension){
        return player.level.dimension().location().equals(dimension);
    }
    
    public static boolean isPlayerInBiome(PlayerEntity player, ResourceLocation biome){
        return player.level.getBiome(player.blockPosition()).getRegistryName().equals(biome);
    }
    
    public static boolean isPlayerInRange(PlayerEntity player, int x, int y, int z, int range){
        return (player.getX() >= x-range && player.getX() <= x+range) && (player.getY() >= y-range && player.getY() <= y+range) && (player.getZ() >= z-range && player.getZ() <= z+range);
    }
}
