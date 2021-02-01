package com.vincentmet.customquests.api.exception;

public class MissingJsonKey extends Throwable{
	private String message = "Unknown";
	
	public MissingJsonKey(String message){
		this.message = message;
	}
	
	@Override
	public String getMessage(){
		return message;
	}
}
