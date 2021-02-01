package com.vincentmet.customquests.helpers;

import com.vincentmet.customquests.Ref;
import java.util.*;

public class PartyInviteCache{
    private static final Map<UUID, Integer> CACHE = new HashMap<>();
    
    public static void addInvite(UUID player, int party){
        if(CACHE.keySet().stream().noneMatch(uuid -> uuid.equals(player))){
            CACHE.put(player, party);
        }
    }
    
    public static int getPartyForInvite(UUID player){
        return CACHE.entrySet().stream().filter(uuidIntegerEntry -> player.equals(uuidIntegerEntry.getKey())).map(Map.Entry::getValue).findFirst().orElse(Ref.NO_PARTY);
    }
    
    public static boolean isPlayerInvitedToParty(UUID player, int party){
        return CACHE.entrySet().stream().anyMatch(entry -> entry.getKey().equals(player) && entry.getValue().equals(party));
    }
    
    public static boolean isPlayerInvitedToAnyParty(UUID player){
        return CACHE.keySet().stream().anyMatch(pair -> pair.equals(player));
    }
    
    public static void removePlayerInvite(UUID player){
        CACHE.entrySet().removeIf(entry -> entry.getKey().equals(player));
    }
}
