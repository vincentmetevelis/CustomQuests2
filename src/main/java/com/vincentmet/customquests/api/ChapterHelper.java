package com.vincentmet.customquests.api;

import com.google.gson.JsonObject;
import com.vincentmet.customquests.gui.editor.IEditorEntry;
import com.vincentmet.customquests.hierarchy.chapter.*;
import com.vincentmet.customquests.hierarchy.quest.Quest;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.util.thread.EffectiveSide;

import java.util.*;

public class ChapterHelper{
	public static boolean doesChapterExist(int chapterId){
		return QuestingStorage.getSidedChaptersMap().get(chapterId) != null;
	}

	public static boolean areQuestsInSameChapter(int questId1, int questId2){
		Quest q1 = QuestHelper.getQuestFromId(questId1);
		Quest q2 = QuestHelper.getQuestFromId(questId2);
		if(q1 != null && q2 != null) return q1.getChapter() == q2.getChapter();
		return false;
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
	
	public static ResourceLocation getIcon(int chapterId){
		if(doesChapterExist(chapterId)){
			return QuestingStorage.getSidedChaptersMap().get(chapterId).getIcon().getResourceLocation();
		}
		return new ResourceLocation("minecraft:grass_block");
	}

	public static QuestList getQuests(int chapterId){
		if(doesChapterExist(chapterId)){
			return QuestingStorage.getSidedChaptersMap().get(chapterId).getQuests();
		}
		return new QuestList(chapterId);
	}

	public static List<IEditorEntry> getEditorChapterEntries(int chapterId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesChapterExist(chapterId)){
			QuestingStorage.getSidedChaptersMap().get(chapterId).addPageEntries(entries);
		}
		return entries;
	}

	public static List<IEditorEntry> getEditorChapterTitleEntries(int chapterId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesChapterExist(chapterId)){
			QuestingStorage.getSidedChaptersMap().get(chapterId).getTitle().addPageEntries(entries);
		}
		return entries;
	}

	public static List<IEditorEntry> getEditorChapterTextEntries(int chapterId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesChapterExist(chapterId)){
			QuestingStorage.getSidedChaptersMap().get(chapterId).getText().addPageEntries(entries);
		}
		return entries;
	}

	public static List<IEditorEntry> getEditorChapterQuestlistEntries(int chapterId){
		List<IEditorEntry> entries = new ArrayList<>();
		if(doesChapterExist(chapterId)){
			QuestingStorage.getSidedChaptersMap().get(chapterId).getQuests().addPageEntries(entries);
		}
		return entries;
	}

	public static int getNextAvailableChapterId(){
		if(!QuestingStorage.getSidedChaptersMap().isEmpty()){
			return Collections.max(QuestingStorage.getSidedChaptersMap().keySet())+1;
		}
		return 0;
	}
}