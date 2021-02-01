package com.vincentmet.customquests.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class CheckCycleEvent extends Event{
    private final PlayerEntity player;
    
    public CheckCycleEvent(PlayerEntity player){
        this.player = player;
    }
    
    public PlayerEntity getPlayer(){
        return player;
    }
}