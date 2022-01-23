package com.vincentmet.customquests.gui;

import com.vincentmet.customquests.gui.editor.MenuSelection;

public class EditorScreenManager{
	private MenuSelection selection = MenuSelection.CHAPTERS;
	
	private int selectedChapterId = -1;
	private int selectedQuestId = -1;
	private int selectedTaskId = -1;
	private int selectedSubtaskId = -1;
	private int selectedRewardId = -1;
	private int selectedSubrewardId = -1;
	
	public void set(MenuSelection menuType){
		selection = menuType;
	}
	
	public void setToParent(){
		switch(selection){
			case QUEST_DEPENDENCIES_LIST:
				selection = MenuSelection.QUEST_DEPENDENCIES;
				selectedChapterId = -1;
				selectedTaskId = -1;
				selectedSubtaskId = -1;
				selectedRewardId = -1;
				selectedSubrewardId = -1;
				break;
			case QUEST_TASK_SUBTASK:
				selection = MenuSelection.QUEST_TASK_SUBTASKS;
				selectedChapterId = -1;
				selectedSubtaskId = -1;
				selectedRewardId = -1;
				selectedSubrewardId = -1;
				break;
			case QUEST_TASK_SUBTASKS:
				selection = MenuSelection.QUEST_TASK;
				selectedChapterId = -1;
				selectedSubtaskId = -1;
				selectedRewardId = -1;
				selectedSubrewardId = -1;
				break;
			case QUEST_TASK:
				selection = MenuSelection.QUEST_TASKS;
				selectedChapterId = -1;
				selectedTaskId = -1;
				selectedSubtaskId = -1;
				selectedRewardId = -1;
				selectedSubrewardId = -1;
				break;
			case QUEST_REWARD_SUBREWARD:
				selection = MenuSelection.QUEST_REWARD_SUBREWARDS;
				selectedChapterId = -1;
				selectedTaskId = -1;
				selectedSubtaskId = -1;
				selectedSubrewardId = -1;
				break;
			case QUEST_REWARD_SUBREWARDS:
				selection = MenuSelection.QUEST_REWARD;
				selectedChapterId = -1;
				selectedTaskId = -1;
				selectedSubtaskId = -1;
				selectedSubrewardId = -1;
				break;
			case QUEST_REWARD:
				selection = MenuSelection.QUEST_REWARDS;
				selectedChapterId = -1;
				selectedTaskId = -1;
				selectedSubtaskId = -1;
				selectedRewardId = -1;
				selectedSubrewardId = -1;
				break;
			case QUEST_BUTTON:
			case QUEST_TITLE:
			case QUEST_SUBTITLE:
			case QUEST_TEXT:
			case QUEST_DEPENDENCIES:
			case QUEST_TASKS:
			case QUEST_REWARDS:
			case QUEST_POSITION:
				selection = MenuSelection.QUEST;
				selectedChapterId = -1;
				selectedTaskId = -1;
				selectedSubtaskId = -1;
				selectedRewardId = -1;
				selectedSubrewardId = -1;
				break;
			case QUEST:
				selection = MenuSelection.QUESTS;
				selectedQuestId = -1;
				selectedTaskId = -1;
				selectedSubtaskId = -1;
				selectedRewardId = -1;
				selectedSubrewardId = -1;
				break;
			case CHAPTER_TITLE:
			case CHAPTER_TEXT:
			case CHAPTER_QUESTLIST:
				selection = MenuSelection.CHAPTER;
				selectedQuestId = -1;
				selectedTaskId = -1;
				selectedSubtaskId = -1;
				selectedRewardId = -1;
				selectedSubrewardId = -1;
				break;
			default:
				selection = MenuSelection.CHAPTERS;
				selectedChapterId = -1;
				selectedQuestId = -1;
				selectedTaskId = -1;
				selectedSubtaskId = -1;
				selectedRewardId = -1;
				selectedSubrewardId = -1;
				break;
		}
	}
	
	public MenuSelection getSelection(){
		return selection;
	}
	
	public void setSelectedChapterId(int selectedChapterId){
		this.selectedChapterId = selectedChapterId;
	}
	
	public void setSelectedQuestId(int selectedQuestId){
		this.selectedQuestId = selectedQuestId;
	}
	
	public void setSelectedTaskId(int selectedTaskId){
		this.selectedTaskId = selectedTaskId;
	}
	
	public void setSelectedSubtaskId(int selectedSubtaskId){
		this.selectedSubtaskId = selectedSubtaskId;
	}
	
	public void setSelectedRewardId(int selectedRewardId){
		this.selectedRewardId = selectedRewardId;
	}
	
	public void setSelectedSubrewardId(int selectedSubrewardId){
		this.selectedSubrewardId = selectedSubrewardId;
	}
	
	public int getSelectedChapterId(){
		return selectedChapterId;
	}
	
	public int getSelectedQuestId(){
		return selectedQuestId;
	}
	
	public int getSelectedTaskId(){
		return selectedTaskId;
	}
	
	public int getSelectedSubtaskId(){
		return selectedSubtaskId;
	}
	
	public int getSelectedRewardId(){
		return selectedRewardId;
	}
	
	public int getSelectedSubrewardId(){
		return selectedSubrewardId;
	}
}