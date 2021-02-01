package com.vincentmet.customquests.api;

import net.minecraft.util.text.*;

public class ButtonContext{
	private ITextComponent text = new StringTextComponent("uwu");
	private Runnable onClick = ()->{};
	
	public ITextComponent getText(){
		return text;
	}
	
	public ButtonContext setText(ITextComponent text){
		this.text = text;
		return this;
	}
	
	public Runnable onClick(){
		return onClick;
	}
	
	public void setOnClick(Runnable onClick){
		this.onClick = onClick;
	}
}