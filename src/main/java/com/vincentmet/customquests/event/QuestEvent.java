package com.vincentmet.customquests.event;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class QuestEvent extends Event{
    private final ServerPlayerEntity player;
    private final int questId;
    
    public QuestEvent(ServerPlayerEntity player, int questId){
        this.player = player;
        this.questId = questId;
    }
    
    public int getQuestId(){
        return questId;
    }
    
    public ServerPlayerEntity getPlayer(){
        return player;
    }
    
    public static class Completed extends QuestEvent{
        public Completed(ServerPlayerEntity playerEntity, int questId){
            super(playerEntity, questId);
        }
    }
    
    public static class Task extends QuestEvent{
        private final int taskId;
        public Task(ServerPlayerEntity player, int questId, int taskId){
            super(player, questId);
            this.taskId = taskId;
        }
    
        public int getTaskId(){
            return taskId;
        }
        
        public static class Completed extends Task{
            public Completed(ServerPlayerEntity player, int questId, int taskId){
                super(player, questId, taskId);
            }
        }
        
        public static class Subtask extends Task{
            private final int subtaskId;
            public Subtask(ServerPlayerEntity player, int questId, int taskId, int subtaskId){
                super(player, questId, taskId);
                this.subtaskId = subtaskId;
            }
    
            public int getSubtaskId(){
                return subtaskId;
            }
    
            public static class Completed extends Subtask{
                public Completed(ServerPlayerEntity player, int questId, int taskId, int subtaskId){
                    super(player, questId, taskId, subtaskId);
                }
            }
        }
    }
    
    public static class Reward extends QuestEvent{
        public Reward(ServerPlayerEntity player, int questId){
            super(player, questId);
        }
        
        public static class Claimed extends Reward{
            public Claimed(ServerPlayerEntity player, int questId){
                super(player, questId);
            }
        }
    }
}