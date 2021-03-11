package com.vincentmet.customquests.api;

import com.vincentmet.customquests.helpers.*;
import java.util.UUID;
import net.minecraft.util.text.*;

public class ButtonContext{
	private ITextComponent text = new StringTextComponent("uwu");
	private QuadConsumer<MouseButton, UUID, Integer, Integer> onClick = (mouseButton, uuid, questId, taskId)->{};
	
	public ITextComponent getText(){
		return text;
	}
	
	public ButtonContext setText(ITextComponent text){
		this.text = text;
		return this;
	}
	
	public QuadConsumer<MouseButton, UUID, Integer, Integer> onClick(){
		return onClick;
	}
	
	public void setOnClick(QuadConsumer<MouseButton, UUID, Integer, Integer> onClick){
		this.onClick = onClick;
	}
}