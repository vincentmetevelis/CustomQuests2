package com.vincentmet.customquests.helpers;

import com.google.gson.JsonObject;

public class JsonContainer{
	private JsonObject json;
	
	public JsonContainer(){
		this(new JsonObject());
	}
	
	public JsonContainer(JsonObject json){
		this.json = json;
	}
	
	public void set(JsonObject json){
		this.json = json;
	}
	
	public JsonObject get(){
		return json;
	}
}