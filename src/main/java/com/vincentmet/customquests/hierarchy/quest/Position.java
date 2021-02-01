package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.math.Vec2i;

public class Position extends Vec2i implements IJsonObjectProvider, IJsonObjectProcessor{
	private int parentQuestId;
	
	public Position(){
		super();
	}
	
	public Position(int x, int y){
		super(x, y);
	}
	
	public Position(int parentQuestId){
		super();
		this.parentQuestId = parentQuestId;
	}
	
	public Position(int parentQuestId, int x, int y){
		super(x, y);
		this.parentQuestId = parentQuestId;
	}
	
	@Override
	public void processJson(JsonObject json){
		if(json.has("x")){
			JsonElement jsonElement = json.get("x");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					int jsonPrimitiveIntValue = jsonPrimitive.getAsInt();
					if(jsonPrimitiveIntValue>=0){
						setX(jsonPrimitiveIntValue);
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > position > x': Value is not an Integer, please use an Integer which is 0 or higher, defaulting to '0'!");
						setX(0);
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > position > x': Value is not a Number, please use an Integer which is 0 or higher, defaulting to '0'!");
					setX(0);
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > position > x': Value is not a Primitive, defaulting to '0'!");
				setX(0);
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > position > x': Not detected, defaulting to '0'!");
			setX(0);
		}
		if(json.has("y")){
			JsonElement jsonElement = json.get("y");
			if(jsonElement.isJsonPrimitive()){
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if(jsonPrimitive.isNumber()){
					int jsonPrimitiveIntValue = jsonPrimitive.getAsInt();
					if(jsonPrimitiveIntValue>=0){
						setY(jsonPrimitiveIntValue);
					}else{
						Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > position > y': Value is not an Integer, please use an Integer which is 0 or higher, defaulting to '0'!");
						setY(0);
					}
				}else{
					Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > position > y': Value is not a Number, please use an Integer which is 0 or higher, defaulting to '0'!");
					setY(0);
				}
			}else{
				Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > position > y': Value is not a Primitive, defaulting to '0'!");
				setY(0);
			}
		}else{
			Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > position > y': Not detected, defaulting to '0'!");
			setY(0);
		}
	}
	
	@Override
	public JsonObject getJson(){
		JsonObject json = new JsonObject();
		json.addProperty("x", getX());
		json.addProperty("y", getY());
		return json;
	}
	
	public Position setX(int x){
		super.setX(x);
		return this;
	}
	
	public Position setY(int y){
		super.setY(y);
		return this;
	}
	
	public Position set(int x, int y){
		super.set(x, y);
		return this;
	}
	
	public Position addX(int dx){
		super.addX(dx);
		return this;
	}
	
	public Position addY(int dy){
		super.addY(dy);
		return this;
	}
	
	public Position add(int dx, int dy){
		super.add(dx, dy);
		return this;
	}
	
	public Position clone(){
		return new Position(super.getX(), super.getY());
	}
}
