package com.vincentmet.customquests.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vincentmet.customquests.Config;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.hierarchy.chapter.Chapter;
import com.vincentmet.customquests.hierarchy.party.Party;
import com.vincentmet.customquests.hierarchy.progress.QuestingPlayer;
import com.vincentmet.customquests.hierarchy.quest.Quest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.vincentmet.customquests.Ref.CustomQuests.LOGGER;

public class CQHelper{
	public static JsonObject getQuestsJsonFromFile(Path path, String filename){
		return loadQuests(path, filename).get("quests").getAsJsonObject();
	}
	
	public static JsonObject getChaptersJsonFromFile(Path path, String filename){
		return loadQuests(path, filename).get("chapters").getAsJsonObject();
	}
	
	public static JsonObject getPartiesJsonFromFile(Path path, String filename){
		return loadParties(path, filename).get("parties").getAsJsonObject();
	}
	
	public static JsonObject getPlayersJsonFromFile(Path path, String filename){
		return loadParties(path, filename).get("players").getAsJsonObject();
	}
	
	public static Map<Integer, Quest> getQuestsMap(JsonObject json){
		Map<Integer, Quest> questMap = new HashMap<>();
		for(Map.Entry<String, JsonElement> idSet : json.entrySet()){
			try{
				int id = Integer.parseInt(idSet.getKey());
				Quest q = new Quest(id);
				q.processJson(json.get(idSet.getKey()).getAsJsonObject());
				questMap.put(id, q);
			}catch(NumberFormatException exception){
				LOGGER.error(idSet.getKey() + " should be a numeric id");
				exception.printStackTrace();
			}
		}
		//for loop below checks if all dependencies are quests that actually exist, and otherwise removes the reference in the dependency list
		for(Quest q : questMap.values()){
			q.getDependencyList().removeIf(dependencyQuestId -> !questMap.containsKey(dependencyQuestId));
		}
		return questMap;
	}
	
	public static Map<Integer, Chapter> getChaptersMap(JsonObject json){
		Map<Integer, Chapter> chapterMap = new HashMap<>();
		for(Map.Entry<String, JsonElement> idSet : json.entrySet()){
			try{
				int id = Integer.parseInt(idSet.getKey());
				Chapter c = new Chapter(id);
				c.processJson(json.get(idSet.getKey()).getAsJsonObject());
				chapterMap.put(id, c);
			}catch(NumberFormatException exception){
				LOGGER.error(idSet.getKey() + " should be a numeric id");
				exception.printStackTrace();
			}
		}
		return chapterMap;
	}
	
	public static Map<Integer, Party> getPartiesMap(JsonObject json){
		Map<Integer, Party> partyMap = new HashMap<>();
		for(Map.Entry<String, JsonElement> idSet : json.entrySet()){
			try{
				int id = Integer.parseInt(idSet.getKey());
				Party p = new Party(id);
				p.processJson(idSet.getValue().getAsJsonObject());
				partyMap.put(id, p);
			}catch(NumberFormatException exception){
				LOGGER.error(idSet.getKey() + " should be a numeric id");
				exception.printStackTrace();
			}
		}
		return partyMap;
	}
	
	public static Map<String, QuestingPlayer> getPlayersMap(JsonObject json){
		Map<String, QuestingPlayer> playerMap = new HashMap<>();
		for(Map.Entry<String, JsonElement> idSet : json.entrySet()){
			try{
				String uuid = idSet.getKey();
				QuestingPlayer p = new QuestingPlayer(UUID.fromString(uuid));
				p.processJson(json.get(idSet.getKey()).getAsJsonObject());
				playerMap.put(uuid, p);
			}catch(NumberFormatException exception){
				LOGGER.error(idSet.getKey() + " should be a uuid");
				exception.printStackTrace();
			}
		}
		return playerMap;
	}
	
	private static JsonObject loadParties(Path path, String filename){
		try {
			if(Config.SidedConfig.areBackupsEnabled()){
				ApiUtils.backupProgress();
			}
			StringBuilder res = new StringBuilder();
			Files.readAllLines(path.resolve(filename), StandardCharsets.UTF_8).forEach(res::append);
			return new JsonParser().parse(res.toString()).getAsJsonObject();
		}catch (IOException e) {
			ApiUtils.writeTo(path, filename, ApiUtils.generateDefaultPartiesJson());
			return loadParties(path, filename);
		}
	}
	
	private static JsonObject loadQuests(Path path, String filename){
		try {
			if(Config.SidedConfig.areBackupsEnabled()){
				ApiUtils.backupData();
			}
			StringBuilder res = new StringBuilder();
			Files.readAllLines(path.resolve(filename), StandardCharsets.UTF_8).forEach(res::append);
			return new JsonParser().parse(res.toString()).getAsJsonObject();
		}catch (IOException e) {
			ApiUtils.writeTo(path, filename, ApiUtils.generateDefaultQuestsJson());
			return loadQuests(path, filename);
		}
	}
	
	public static void generateMissingProgress(){
		Ref.currentServerInstance.getPlayerList().getPlayers().forEach(player -> {
			if(!QuestingStorage.getSidedPlayersMap().containsKey(player.getStringUUID())){
				QuestingStorage.getSidedPlayersMap().put(player.getStringUUID(), new QuestingPlayer(player.getUUID()));
			}
		});
		QuestingStorage.getSidedPlayersMap().forEach((uuid, questingPlayer) -> {
			generateMissingProgress(UUID.fromString(uuid));
		});
	}
	
	public static void generateMissingProgress(UUID uuidIn){
		if(!QuestingStorage.getSidedPlayersMap().containsKey(uuidIn.toString())){
			QuestingStorage.getSidedPlayersMap().put(uuidIn.toString(), new QuestingPlayer(uuidIn));
		}
		QuestingStorage.getSidedPlayersMap().forEach((uuid, questingPlayer) -> {
			questingPlayer.deleteExcessValues();
			questingPlayer.generateMissingValues();
		});
	}
	public static void generateMissingPartyProgress(){
		QuestingStorage.getSidedPartiesMap().keySet().forEach(CQHelper::generateMissingPartyProgress);
	}
	
	public static void generateMissingPartyProgress(int partyId){
		if(PartyHelper.doesPartyExist(partyId)){
			QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().deleteExcessValues();
			QuestingStorage.getSidedPartiesMap().get(partyId).getCollectiveProgress().generateMissingValues();
		}
	}
	
	public static boolean evalBool(LogicType logicType, List<Boolean> booleans, boolean orElse){
		return switch (logicType) {
			case OR -> booleans.stream().reduce(Boolean::logicalOr).orElse(orElse);
			case AND -> booleans.stream().reduce(Boolean::logicalAnd).orElse(orElse);
		};
	}
	
	public static void writeQuestsAndChaptersToFile(Path path, String filename){
		Ref.CustomQuests.LOGGER.info("Writing Quests and Chapters to file");
		JsonObject json = new JsonObject();
		JsonObject quests = new JsonObject();
		QuestingStorage.getSidedQuestsMap().forEach((questId, quest) -> {
			quests.add(questId.toString(), quest.getJson());
		});
		JsonObject chapters = new JsonObject();
		QuestingStorage.getSidedChaptersMap().forEach((chapterId, chapter) -> {
			chapters.add(chapterId.toString(), chapter.getJson());
		});
		json.add("chapters", chapters);
		json.add("quests", quests);
		ApiUtils.writeTo(path, filename, json.toString());
	}
	
	public static void writePlayersAndPartiesToFile(Path path, String filename){
		Ref.CustomQuests.LOGGER.info("Writing Players and Parties to file");
		JsonObject json = new JsonObject();
		JsonObject parties = new JsonObject();
		QuestingStorage.getSidedPartiesMap().forEach((partyId, party) -> {
			parties.add(partyId.toString(), party.getJson());
		});
		JsonObject players = new JsonObject();
		QuestingStorage.getSidedPlayersMap().forEach((playerUuid, player) -> {
			players.add(playerUuid, player.getJson());
		});
		json.add("parties", parties);
		json.add("players", players);
		ApiUtils.writeTo(path, filename, json.toString());
	}
	
	public static void readAllFilesAndPutIntoHashmaps(){
		QuestingStorage.getSidedQuestsMap().clear();
		QuestingStorage.getSidedChaptersMap().clear();
		QuestingStorage.getSidedPartiesMap().clear();
		QuestingStorage.getSidedPlayersMap().clear();
		QuestingStorage.getSidedQuestsMap().putAll(CQHelper.getQuestsMap(CQHelper.getQuestsJsonFromFile(Ref.PATH_CONFIG, Ref.FILENAME_QUESTS + Ref.FILE_EXT_JSON)));
		QuestingStorage.getSidedChaptersMap().putAll(CQHelper.getChaptersMap(CQHelper.getChaptersJsonFromFile(Ref.PATH_CONFIG, Ref.FILENAME_QUESTS + Ref.FILE_EXT_JSON)));
		QuestingStorage.getSidedPartiesMap().putAll(CQHelper.getPartiesMap(CQHelper.getPartiesJsonFromFile(Ref.currentProgressDirectory, Ref.FILENAME_PARTIES + Ref.FILE_EXT_JSON)));
		QuestingStorage.getSidedPlayersMap().putAll(CQHelper.getPlayersMap(CQHelper.getPlayersJsonFromFile(Ref.currentProgressDirectory, Ref.FILENAME_PARTIES + Ref.FILE_EXT_JSON)));
	}
}