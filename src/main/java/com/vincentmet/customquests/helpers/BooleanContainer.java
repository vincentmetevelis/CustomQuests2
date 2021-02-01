package com.vincentmet.customquests.helpers;

public class BooleanContainer{
	private boolean bool;
	
	public BooleanContainer(){
		this(false);
	}
	
	public BooleanContainer(boolean bool){
		this.bool = bool;
	}
	
	public void set(boolean bool){
		this.bool = bool;
	}
	
	public boolean get(){
		return bool;
	}
	
	public void toggle(){
		this.bool = !this.bool;
	}
}