package com.vincentmet.customquests.api;

import com.vincentmet.customquests.hierarchy.chapter.Chapter;
import com.vincentmet.customquests.hierarchy.quest.Quest;
import java.util.*;

public class ChapterHelper{
	public static boolean areQuestsInSameChapter(int questId1, int questId2){
		Quest q1 = QuestHelper.getQuestFromId(questId1);
		Quest q2 = QuestHelper.getQuestFromId(questId2);
		if(q1 != null && q2 != null) return q1.getChapter() == q2.getChapter();
		return false;
	}
	
	public static Chapter getChapterFromId(int id){
		return QuestingStorage.getSidedChaptersMap().entrySet().stream().filter(entry -> entry.getKey() == id).map(Map.Entry::getValue).findFirst().get();
	}
	
	public static List<Chapter> getUnlockedChapters(UUID uuid){
		List<Integer> completedQuestIds = CombinedProgressHelper.getCompletedQuests(uuid);
		List<Chapter> unlockedChapters = new ArrayList<>();
		QuestingStorage.getSidedQuestsMap().forEach((questId, quest) -> {
			if(quest.getDependencyList().isEmpty()){
				unlockedChapters.add(quest.getChapter());
			}
			for(int dependency : quest.getDependencyList()){
				if(completedQuestIds.contains(dependency) && !unlockedChapters.contains(quest.getChapter())){
					unlockedChapters.add(quest.getChapter());
				}
			}
		});
		return unlockedChapters;
	}
	
	public static boolean isChapterUnlocked(UUID uuid, Chapter chapter){
		return getUnlockedChapters(uuid).contains(chapter);
	}
}
