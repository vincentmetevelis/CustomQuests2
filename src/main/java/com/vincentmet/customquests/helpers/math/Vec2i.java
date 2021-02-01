package com.vincentmet.customquests.helpers.math;

public class Vec2i{
	private int x, y;
	
	public Vec2i(){
		this.x = 0;
		this.y = 0;
	}
	
	public Vec2i(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return x;
	}
	
	public Vec2i setX(int x){
		this.x = x;
		return this;
	}
	
	public int getY(){
		return y;
	}
	
	public Vec2i setY(int y){
		this.y = y;
		return this;
	}
	
	public Vec2i set(int x, int y){
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Vec2i addX(int dx){
		this.x += dx;
		return this;
	}
	
	public Vec2i addY(int dy){
		this.y += dy;
		return this;
	}
	
	public Vec2i add(int dx, int dy){
		this.x += dx;
		this.y += dy;
		return this;
	}
}