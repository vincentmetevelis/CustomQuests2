package com.vincentmet.customquests.api;

import com.google.gson.JsonObject;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.hierarchy.chapter.Chapter;
import com.vincentmet.customquests.hierarchy.quest.Quest;
import java.util.Collections;

public class EditorHelper{
    public static int getNextChapterId(){
        if(!QuestingStorage.getSidedChaptersMap().isEmpty()){
            return Collections.max(QuestingStorage.getSidedChaptersMap().keySet())+1;
        }
        return 0;
    }
    
    public static int getNextQuestId(){
        if(!QuestingStorage.getSidedQuestsMap().isEmpty()){
            return Collections.max(QuestingStorage.getSidedQuestsMap().keySet())+1;
        }
        return 0;
    }
    
    public static void addEmptyChapter(){
        int newId = getNextChapterId();
        Chapter newChapter = new Chapter(newId);
        newChapter.processJson(new JsonObject());
        QuestingStorage.getSidedChaptersMap().put(newId, newChapter);
        ServerUtils.sendChapterToAllPlayers(newId);
    }
    
    public static void addEmptyQuest(){
        int newId = getNextQuestId();
        Quest newQuest = new Quest(newId);
        newQuest.processJson(new JsonObject());
        QuestingStorage.getSidedQuestsMap().put(newId, newQuest);
        ServerUtils.sendQuestToAllPlayers(newId);
    }
    
    public static void deleteChapter(int chapterId){
        QuestingStorage.getSidedChaptersMap().remove(chapterId);
        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player->ServerUtils.clearSingleChapterAtClient(player, chapterId));
    }
    
    public static void deleteQuest(int questId){
        QuestingStorage.getSidedChaptersMap().values().forEach(chapter -> chapter.getQuests().remove(questId));
        QuestingStorage.getSidedQuestsMap().remove(questId);
        Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player->ServerUtils.clearSingleQuestAtClient(player, questId));
    }
}