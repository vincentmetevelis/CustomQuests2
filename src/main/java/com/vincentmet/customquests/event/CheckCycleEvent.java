package com.vincentmet.customquests.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class CheckCycleEvent extends Event{
    private final Player player;
    
    public CheckCycleEvent(Player player){
        this.player = player;
    }
    
    public Player getPlayer(){
        return player;
    }
}