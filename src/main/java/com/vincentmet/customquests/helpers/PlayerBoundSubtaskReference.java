package com.vincentmet.customquests.helpers;

import java.util.UUID;

public class PlayerBoundSubtaskReference{
    private final UUID player;
    private final int questId;
    private final int taskId;
    private final int subtaskId;
    
    public PlayerBoundSubtaskReference(UUID player, int questId, int taskId, int subtaskId){
        this.player = player;
        this.questId = questId;
        this.taskId = taskId;
        this.subtaskId = subtaskId;
    }
    
    public UUID getPlayer(){
        return player;
    }
    
    public int getQuestId(){
        return questId;
    }
    
    public int getTaskId(){
        return taskId;
    }
    
    public int getSubtaskId(){
        return subtaskId;
    }
}
