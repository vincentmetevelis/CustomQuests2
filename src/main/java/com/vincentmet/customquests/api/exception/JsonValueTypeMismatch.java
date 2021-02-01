package com.vincentmet.customquests.api.exception;

public class JsonValueTypeMismatch extends Throwable{
	private String message = "Unknown";
	
	public JsonValueTypeMismatch(String message){
		this.message = message;
	}
	
	@Override
	public String getMessage(){
		return message;
	}
}
