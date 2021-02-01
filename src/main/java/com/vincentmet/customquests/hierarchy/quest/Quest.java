package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.hierarchy.chapter.Chapter;
import java.util.stream.Collectors;

public class Quest implements IJsonObjectProvider, IJsonObjectProcessor{
	private final int id;
	private final QuestButton button;
	private TextType title;
	private TextType subtitle;
	private TextType text;
	private boolean isGlobal;
	private final DependencyList dependencyList;
	private final LockingList lockingList;
	private final UnlockingList unlockingList;
	private final Tasks tasks;
	private final Rewards rewards;
	private final Position position;
	
	public Quest(int id, QuestButton button, TextType title, TextType subtitle, TextType text, boolean isGlobal, DependencyList dependencyList, LockingList lockingList, UnlockingList unlockingList, Tasks tasks, Rewards rewards, Position position){
		this.id = id;
		this.button = button;
		this.title = title;
		this.subtitle = subtitle;
		this.text = text;
		this.isGlobal = isGlobal;
		this.dependencyList = dependencyList;
		this.lockingList = lockingList;
		this.unlockingList = unlockingList;
		this.tasks = tasks;
		this.rewards = rewards;
		this.position = position;
	}
	
	public Quest(int id){
		this(id, new QuestButton(id), new TextType(id, "title"), new TextType(id, "subtitle"), new TextType(id, "text"), false, new DependencyList(id), new LockingList(id), new UnlockingList(id), new Tasks(id), new Rewards(id), new Position(id));
	}
	
	@Override
	public void processJson(JsonObject json){//todo re-add global, locks, unlocks once start implementing it
		if(json.has("button")){
			JsonElement jsonElement = json.get("button");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				button.processJson(jsonObject);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > button': Value is not a JsonObject, generating a new one!");
				button.processJson(new JsonObject());
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > button': Not detected, generating a new JsonObject!");
			button.processJson(new JsonObject());
		}
		
		if(json.has("title")){
			JsonElement jsonElement = json.get("title");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				title.processJson(jsonObject);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > title': Value is not a JsonObject, generating a new one!");
				title.processJson(new JsonObject());
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > title': Not detected, generating a new JsonObject!");
			title.processJson(new JsonObject());
		}
		
		if(json.has("subtitle")){
			JsonElement jsonElement = json.get("subtitle");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				subtitle.processJson(jsonObject);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > subtitle': Value is not a JsonObject, generating a new one!");
				subtitle.processJson(new JsonObject());
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > subtitle': Not detected, generating a new JsonObject!");
			subtitle.processJson(new JsonObject());
		}
		
		if(json.has("text")){
			JsonElement jsonElement = json.get("text");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				text.processJson(jsonObject);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > text': Value is not a JsonObject, generating a new one!");
				text.processJson(new JsonObject());
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > text': Not detected, generating a new JsonObject!");
			text.processJson(new JsonObject());
		}
		
		/*if(json.has("global")){
			JsonElement jsonElement = json.get("global");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isBoolean()){
					boolean jsonPrimitiveBooleanValue = jsonPrimitive.getAsBoolean();
					setGlobal(jsonPrimitiveBooleanValue);
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > global': Value is not a Boolean, defaulting to 'false'!");
					setGlobal(false);
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > global': Value is not a JsonPrimitive, please use a boolean, defaulting to 'false'!");
				setGlobal(false);
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > global': Not detected, defaulting to 'false'");
			setGlobal(false);
		}*/
		
		if(json.has("dependencies")){
			JsonElement jsonElement = json.get("dependencies");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				dependencyList.processJson(jsonObject);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > dependencies': Value is not a JsonObject, generating a new one!");
				dependencyList.processJson(new JsonObject());
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > dependencies': Not detected, generating a new JsonObject!");
			dependencyList.processJson(new JsonObject());
		}
		
		/*if(json.has("locks")){
			JsonElement jsonElement = json.get("locks");
			if(jsonElement.isJsonArray()){
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				lockingList.processJson(jsonArray);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > locks': Value is not a JsonObject, generating a new one!");
				lockingList.processJson(new JsonArray());
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > locks': Not detected, generating a new JsonObject!");
			lockingList.processJson(new JsonArray());
		}
		
		if(json.has("unlocks")){
			JsonElement jsonElement = json.get("unlocks");
			if(jsonElement.isJsonArray()){
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				unlockingList.processJson(jsonArray);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > unlocks': Value is not a JsonObject, generating a new one!");
				unlockingList.processJson(new JsonArray());
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > unlocks': Not detected, generating a new JsonObject!");
			unlockingList.processJson(new JsonArray());
		}*/
		
		if(json.has("tasks")){
			JsonElement jsonElement = json.get("tasks");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				tasks.processJson(jsonObject);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > tasks': Value is not a JsonObject, generating a new one!");
				tasks.processJson(new JsonObject());
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > tasks': Not detected, generating a new JsonObject!");
			tasks.processJson(new JsonObject());
		}
		
		if(json.has("rewards")){
			JsonElement jsonElement = json.get("rewards");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				rewards.processJson(jsonObject);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > rewards': Value is not a JsonObject, generating a new one!");
				rewards.processJson(new JsonObject());
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > rewards': Not detected, generating a new JsonObject!");
			rewards.processJson(new JsonObject());
		}
		
		if(json.has("position")){
			JsonElement jsonElement = json.get("position");
			if(jsonElement.isJsonObject()){
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				position.processJson(jsonObject);
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > position': Value is not a JsonObject, generating a new one!");
				position.processJson(new JsonObject());
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + id + " > position': Not detected, generating a new JsonObject!");
			position.processJson(new JsonObject());
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.add("button", button.getJson());
		json.add("title", title.getJson());
		json.add("subtitle", subtitle.getJson());
		json.add("text", text.getJson());
		//json.addProperty("global", isGlobal);
		json.add("dependencies", dependencyList.getJson());
		//json.add("locks", lockingList.getJson());
		//json.add("unlocks", unlockingList.getJson());
		json.add("tasks", tasks.getJson());
		json.add("rewards", rewards.getJson());
		json.add("position", position.getJson());
		return json;
	}
	
	public void setGlobal(boolean global){
		isGlobal = global;
	}
	
	public int getId(){
		return id;
	}
	
	public QuestButton getButton(){
		return button;
	}
	
	public TextType getTitle(){
		return title;
	}
	
	public TextType getSubtitle(){
		return subtitle;
	}
	
	public TextType getText(){
		return text;
	}
	
	public boolean isGlobal(){
		return isGlobal;
	}
	
	public DependencyList getDependencyList(){
		return dependencyList;
	}
	
	public LockingList getLockingList(){
		return lockingList;
	}
	
	public UnlockingList getUnlockingList(){
		return unlockingList;
	}
	
	public Tasks getTasks(){
		return tasks;
	}
	
	public Rewards getRewards(){
		return rewards;
	}
	
	public Position getPosition(){
		return position;
	}
	
	public Chapter getChapter(){
		if(hasChapter()){
			return QuestingStorage.getSidedChaptersMap().values().stream().filter(chapter->chapter.getQuests().contains(id)).collect(Collectors.toList()).get(0);
		}
		return null;
	}
	
	public boolean hasChapter(){
		return QuestingStorage.getSidedChaptersMap().values().stream().anyMatch(chapter->chapter.getQuests().contains(id));
	}
}
