package com.vincentmet.customquests.event;

import com.vincentmet.customquests.hierarchy.party.Party;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class PartyEvent extends Event{
    private final Party party;
    private final int questId;
    //todo add onNewMember
    //todo add onRemoveMember
    //todo add onTransferOwnership
    //todo add onDeleteParty
    //todo add onCreateParty

    //todo add onQuestUpdate(+(sub)tasks), also for QuestEvent
    public PartyEvent(Party party, int questId){
        this.party = party;
        this.questId = questId;
    }
    
    public int getQuestId(){
        return questId;
    }
    
    public Party getParty(){
        return party;
    }
    
    public static class Completed extends PartyEvent {
        public Completed(Party party, int questId){
            super(party, questId);
        }
    }
    
    public static class Task extends PartyEvent {
        private final int taskId;
        public Task(Party party, int questId, int taskId){
            super(party, questId);
            this.taskId = taskId;
        }
    
        public int getTaskId(){
            return taskId;
        }
        
        public static class Completed extends Task{
            public Completed(Party party, int questId, int taskId){
                super(party, questId, taskId);
            }
        }
        
        public static class Subtask extends Task{
            private final int subtaskId;
            public Subtask(Party party, int questId, int taskId, int subtaskId){
                super(party, questId, taskId);
                this.subtaskId = subtaskId;
            }
    
            public int getSubtaskId(){
                return subtaskId;
            }
    
            public static class Completed extends Subtask{
                public Completed(Party party, int questId, int taskId, int subtaskId){
                    super(party, questId, taskId, subtaskId);
                }
            }
        }
    }
    
    public static class Reward extends PartyEvent {
        public Reward(Party party, int questId){
            super(party, questId);
        }
        
        public static class Claimed extends Reward{
            public Claimed(Party party, int questId){
                super(party, questId);
            }
        }
    }
}