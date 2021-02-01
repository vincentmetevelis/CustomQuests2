package com.vincentmet.customquests.helpers;

public class Container<T>{
	private T t;
	
	public Container(T t){
		this.t = t;
	}
	
	public T get(){
		return t;
	}
	
	public void set(T t){
		this.t = t;
	}
	
	@Override
	public String toString(){
		return "Container{" + t.toString() + '}';
	}
}
