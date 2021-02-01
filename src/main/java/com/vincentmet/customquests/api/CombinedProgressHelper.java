package com.vincentmet.customquests.api;

import com.vincentmet.customquests.*;
import com.vincentmet.customquests.event.QuestEvent;
import com.vincentmet.customquests.hierarchy.quest.LogicType;
import java.util.*;
import net.minecraftforge.common.MinecraftForge;

public class CombinedProgressHelper{
    public static void completeQuest(UUID player, int questId){
        if(ProgressHelper.doesPlayerExist(player) && QuestHelper.doesQuestExist(questId)){
            ProgressHelper.completeQuest(player, questId);
            if(ProgressHelper.isPlayerInParty(player)){
                int partyId = ProgressHelper.getPlayerParty(player);
                PartyHelper.completeQuest(partyId, questId);
            }
            MinecraftForge.EVENT_BUS.post(new QuestEvent.Completed(Ref.currentServerInstance.getPlayerList().getPlayerByUUID(player), questId));
        }
    }
    
    public static void completeTask(UUID player, int questId, int taskId){
        if(ProgressHelper.doesPlayerExist(player) && QuestHelper.doesTaskExist(questId, taskId)){
            ProgressHelper.completeTask(player, questId, taskId);
            if(ProgressHelper.isPlayerInParty(player)){
                int partyId = ProgressHelper.getPlayerParty(player);
                PartyHelper.completeTask(partyId, questId, taskId);
            }
            MinecraftForge.EVENT_BUS.post(new QuestEvent.Task.Completed(Ref.currentServerInstance.getPlayerList().getPlayerByUUID(player), questId, taskId));
        }
    }
    
    public static void completeSubtask(UUID player, int questId, int taskId, int subtaskId){
        if(ProgressHelper.doesPlayerExist(player) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
            ProgressHelper.completeSubtask(player, questId, taskId, subtaskId);
            if(ProgressHelper.isPlayerInParty(player)){
                int partyId = ProgressHelper.getPlayerParty(player);
                PartyHelper.completeSubtask(partyId, questId, taskId, subtaskId);
            }
            MinecraftForge.EVENT_BUS.post(new QuestEvent.Task.Subtask.Completed(Ref.currentServerInstance.getPlayerList().getPlayerByUUID(player), questId, taskId, subtaskId));
        }
    }
    
    public static boolean isQuestCompleted(UUID player, int questId){
        if(ProgressHelper.doesPlayerExist(player) && QuestHelper.doesQuestExist(questId)){
            if(ProgressHelper.isPlayerInParty(player)){
                int partyId = ProgressHelper.getPlayerParty(player);
                return ProgressHelper.isQuestCompleted(player, questId) || PartyHelper.isQuestCompleted(partyId, questId);
            }
            return ProgressHelper.isQuestCompleted(player, questId);
        }
        return false;
    }
    
    public static boolean isTaskCompleted(UUID player, int questId, int taskId){
        if(ProgressHelper.doesPlayerExist(player) && QuestHelper.doesTaskExist(questId, taskId)){
            if(ProgressHelper.isPlayerInParty(player)){
                int partyId = ProgressHelper.getPlayerParty(player);
                return ProgressHelper.isTaskCompleted(player, questId, taskId) || PartyHelper.isTaskCompleted(partyId, questId, taskId);
            }
            return ProgressHelper.isTaskCompleted(player, questId, taskId);
        }
        return false;
    }
    
    public static boolean isSubtaskCompleted(UUID player, int questId, int taskId, int subtaskId){
        if(ProgressHelper.doesPlayerExist(player) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
            if(ProgressHelper.isPlayerInParty(player)){
                int partyId = ProgressHelper.getPlayerParty(player);
                return ProgressHelper.isSubtaskCompleted(player, questId, taskId, subtaskId) || PartyHelper.isSubtaskCompleted(partyId, questId, taskId, subtaskId);
            }
            return ProgressHelper.isSubtaskCompleted(player, questId, taskId, subtaskId);
        }
        return false;
    }
    
    public static void claimReward(UUID claimer, int questId, int chosenRewardId){//todo
        if(ProgressHelper.doesPlayerExist(claimer) && canClaimReward(claimer, questId)){
            if(ProgressHelper.isPlayerInParty(claimer)){
                int partyId = ProgressHelper.getPlayerParty(claimer);
                if(Config.SidedConfig.canRewardOnlyBeClaimedOnce()){
                    if(QuestHelper.doesRewardExist(questId, chosenRewardId)){
                        QuestHelper.executeAllSubrewards(claimer, questId, chosenRewardId);
                        QuestingStorage.getSidedPlayersMap().get(claimer.toString()).getIndividualProgress().get(questId).setClaimed(true);
                        QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).setClaimed(true);
                    }else{//auto-select first one
                        if(QuestHelper.doesQuestHaveRewards(questId)){
                            Optional<Integer> autoSelectedRewardIdOptional = QuestHelper.getNextExistingRewardId(questId);
                            if(autoSelectedRewardIdOptional.isPresent()){
                                int autoSelectedRewardId = autoSelectedRewardIdOptional.get();
                                QuestHelper.executeAllSubrewards(claimer, questId, autoSelectedRewardId);
                                QuestingStorage.getSidedPlayersMap().get(claimer.toString()).getIndividualProgress().get(questId).setClaimed(true);
                                QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().get(questId).setClaimed(true);
                            }
                        }else{
                            Ref.CustomQuests.LOGGER.warn("Quest " + questId + " doesn't have any rewards!");
                        }
                    }
                }else{
                    if(QuestHelper.doesRewardExist(questId, chosenRewardId)){
                        QuestHelper.executeAllSubrewards(claimer, questId, chosenRewardId);
                        QuestingStorage.getSidedPlayersMap().get(claimer.toString()).getIndividualProgress().get(questId).setClaimed(true);
                    }else{//auto-select first one
                        if(QuestHelper.doesQuestHaveRewards(questId)){
                            Optional<Integer> autoSelectedRewardIdOptional = QuestHelper.getNextExistingRewardId(questId);
                            if(autoSelectedRewardIdOptional.isPresent()){
                                int autoSelectedRewardId = autoSelectedRewardIdOptional.get();
                                QuestHelper.executeAllSubrewards(claimer, questId, autoSelectedRewardId);
                                QuestingStorage.getSidedPlayersMap().get(claimer.toString()).getIndividualProgress().get(questId).setClaimed(true);
                            }
                        }else{
                            Ref.CustomQuests.LOGGER.warn("Quest " + questId + " doesn't have any rewards!");
                        }
                    }
                }
            }else{
                if(QuestHelper.doesRewardExist(questId, chosenRewardId)){
                    QuestHelper.executeAllSubrewards(claimer, questId, chosenRewardId);
                    QuestingStorage.getSidedPlayersMap().get(claimer.toString()).getIndividualProgress().get(questId).setClaimed(true);
                }else{//auto-select first one
                    if(QuestHelper.doesQuestHaveRewards(questId)){
                        Optional<Integer> autoSelectedRewardIdOptional = QuestHelper.getNextExistingRewardId(questId);
                        if(autoSelectedRewardIdOptional.isPresent()){
                            int autoSelectedRewardId = autoSelectedRewardIdOptional.get();
                            QuestHelper.executeAllSubrewards(claimer, questId, autoSelectedRewardId);
                            QuestingStorage.getSidedPlayersMap().get(claimer.toString()).getIndividualProgress().get(questId).setClaimed(true);
                        }
                    }else{
                        Ref.CustomQuests.LOGGER.warn("Quest " + questId + " doesn't have any rewards!");
                    }
                }
            }
        }
        ServerUtils.sendProgressAndParties(Ref.currentServerInstance.getPlayerList().getPlayerByUUID(claimer));
    }
    
    public static boolean canClaimReward(UUID claimer, int questId){
        if(ProgressHelper.doesPlayerExist(claimer) && QuestHelper.doesQuestExist(questId)){
            if(ProgressHelper.isPlayerInParty(claimer)){
                if(Config.SidedConfig.canRewardOnlyBeClaimedOnce()){
                    int partyId = ProgressHelper.getPlayerParty(claimer);
                    return CombinedProgressHelper.isQuestCompleted(claimer, questId) && !PartyHelper.isQuestClaimed(partyId, questId);
                }else{
                    return CombinedProgressHelper.isQuestCompleted(claimer, questId) && !ProgressHelper.isQuestClaimed(claimer, questId);
                }
            }else{
                return CombinedProgressHelper.isQuestCompleted(claimer, questId) && !ProgressHelper.isQuestClaimed(claimer, questId);
            }
            
        }
        return false;
    }
    
    public static boolean isQuestClaimed(UUID claimer, int questId){
        if(ProgressHelper.doesPlayerExist(claimer) && QuestHelper.doesQuestExist(questId)){
            if(ProgressHelper.isPlayerInParty(claimer)){
                if(Config.SidedConfig.canRewardOnlyBeClaimedOnce()){
                    int partyId = ProgressHelper.getPlayerParty(claimer);
                    return PartyHelper.isQuestClaimed(partyId, questId);
                }else{
                    return ProgressHelper.isQuestClaimed(claimer, questId);
                }
            }else{
                return ProgressHelper.isQuestClaimed(claimer, questId);
            }
        }
        return false;
    }
    
    public static boolean isQuestUnlocked(UUID player, int questId){
        if(ProgressHelper.doesPlayerExist(player) && QuestHelper.doesQuestExist(questId)){
            if(ProgressHelper.isPlayerInParty(player)){
                int partyId = ProgressHelper.getPlayerParty(player);
                LogicType type = QuestHelper.getQuestFromId(questId).getDependencyList().getLogicType();
                List<Boolean> boolsToEval = new ArrayList<>();
                boolsToEval.add(ProgressHelper.isQuestUnlocked(player, questId));
                boolsToEval.add(PartyHelper.isQuestUnlocked(partyId, questId));
                return CQHelper.evalBool(type, boolsToEval, true);
            }
            return ProgressHelper.isQuestUnlocked(player, questId);
        }
        return false;
    }
    
    public static void setValue(UUID player, int questId, int taskId, int subtaskId, int value){
        if(ProgressHelper.doesPlayerExist(player) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
            if(ProgressHelper.isPlayerInParty(player)){
                int partyId = ProgressHelper.getPlayerParty(player);
                PartyHelper.setValue(partyId, questId, taskId, subtaskId, value);
            }
            ProgressHelper.setValue(player, questId, taskId, subtaskId, value);
        }
    }
    
    public static void addValue(UUID player, int questId, int taskId, int subtaskId, int value){
        if(ProgressHelper.doesPlayerExist(player) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
            if(ProgressHelper.isPlayerInParty(player)){
                int partyId = ProgressHelper.getPlayerParty(player);
                PartyHelper.addValue(partyId, questId, taskId, subtaskId, value);
            }
            ProgressHelper.addValue(player, questId, taskId, subtaskId, value);
        }
    }
    
    public static int getValue(UUID player, int questId, int taskId, int subtaskId){
        if(ProgressHelper.doesPlayerExist(player) && QuestHelper.doesSubtaskExist(questId, taskId, subtaskId)){
            if(ProgressHelper.isPlayerInParty(player)){
                int partyId = ProgressHelper.getPlayerParty(player);
                return PartyHelper.getValue(partyId, questId, taskId, subtaskId);
            }
            return ProgressHelper.getValue(player, questId, taskId, subtaskId);
        }
        return 0;
    }
    
    public static List<Integer> getCompletedQuests(UUID player){
        List<Integer> completedQuests = new ArrayList<>();
        if(ProgressHelper.doesPlayerExist(player)){
            completedQuests.addAll(QuestingStorage.getSidedPlayersMap().get(player.toString()).getIndividualProgress().getIndividuallyCompletedQuests());
            if(ProgressHelper.isPlayerInParty(player)){
                int partyId = ProgressHelper.getPlayerParty(player);
                completedQuests.addAll(QuestingStorage.getSidedPartiesMap().get(partyId).getCollectivelyCompletedQuestList());
            }
        }
        return completedQuests;
    }
}