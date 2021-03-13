package com.vincentmet.customquests.helpers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

public class WorldHelper{
    public static boolean isPlayerInDimension(PlayerEntity player, ResourceLocation dimension){
        return player.getEntityWorld().getDimensionKey().getLocation().equals(dimension);
    }
    
    public static boolean isPlayerInBiome(PlayerEntity player, ResourceLocation biome){
        return player.getEntityWorld().getBiome(player.getPosition()).getRegistryName().equals(biome);
    }
    
    public static boolean isPlayerInRange(PlayerEntity player, int x, int y, int z, int range){
        return (player.getPosX() >= x-range && player.getPosX() <= x+range) && (player.getPosY() >= y-range && player.getPosY() <= y+range) && (player.getPosZ() >= z-range && player.getPosZ() <= z+range);
    }
}
