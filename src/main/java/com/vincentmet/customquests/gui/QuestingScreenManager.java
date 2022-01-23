package com.vincentmet.customquests.gui;

public class QuestingScreenManager{
	private int currentlySelectedChapterId = -1;
	private int currentlySelectedQuestId = -1;
	
	public void setCurrentlySelectedChapterId(int newChapterId){
		if(newChapterId>=0){
			this.currentlySelectedChapterId = newChapterId;
		}
	}
	
	public void setCurrentlySelectedQuestId(int newQuestId){
		if(newQuestId>=0){
			this.currentlySelectedQuestId = newQuestId;
		}
	}
	
	public int getCurrentlySelectedChapterId(){
		return currentlySelectedChapterId;
	}
	
	public int getCurrentlySelectedQuestId(){
		return currentlySelectedQuestId;
	}
	
	public void reset(){
		this.currentlySelectedChapterId = -1;
		this.currentlySelectedQuestId = -1;
	}
	
	public void resetCurrentQuestId(){
		this.currentlySelectedQuestId = -1;
	}
	
	public boolean shouldShowQuestDetails(){
		return getCurrentlySelectedQuestId() >= 0;
	}
}