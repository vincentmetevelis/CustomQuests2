package com.vincentmet.customquests.api;

import com.vincentmet.customquests.hierarchy.chapter.Chapter;
import com.vincentmet.customquests.hierarchy.party.Party;
import com.vincentmet.customquests.hierarchy.progress.QuestingPlayer;
import com.vincentmet.customquests.hierarchy.quest.Quest;
import java.util.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.util.thread.EffectiveSide;

public class QuestingStorage{
	public static final Map<String, SoundEvent> SOUNDS = new HashMap<>();
	
	private static final Map<Integer, Quest> ALL_QUESTS_CLIENT = new HashMap<>();
	private static final Map<Integer, Chapter> ALL_CHAPTERS_CLIENT = new HashMap<>();
	private static final Map<Integer, Party> ALL_PARTIES_CLIENT = new HashMap<>();
	private static final Map<String, QuestingPlayer> ALL_PLAYERS_CLIENT = new HashMap<>();
	
	private static final Map<Integer, Quest> ALL_QUESTS_SERVER = new HashMap<>();
	private static final Map<Integer, Chapter> ALL_CHAPTERS_SERVER = new HashMap<>();
	private static final Map<Integer, Party> ALL_PARTIES_SERVER = new HashMap<>();
	private static final Map<String, QuestingPlayer> ALL_PLAYERS_SERVER = new HashMap<>();
	
	public static Map<Integer, Quest> getSidedQuestsMap(){
		return EffectiveSide.get().isClient() ? ALL_QUESTS_CLIENT : ALL_QUESTS_SERVER;
	}
	public static Map<Integer, Chapter> getSidedChaptersMap(){
		return EffectiveSide.get().isClient() ? ALL_CHAPTERS_CLIENT : ALL_CHAPTERS_SERVER;
	}
	public static Map<Integer, Party> getSidedPartiesMap(){
		return EffectiveSide.get().isClient() ? ALL_PARTIES_CLIENT : ALL_PARTIES_SERVER;
	}
	public static Map<String, QuestingPlayer> getSidedPlayersMap(){
		return EffectiveSide.get().isClient() ? ALL_PLAYERS_CLIENT : ALL_PLAYERS_SERVER;
	}
}
