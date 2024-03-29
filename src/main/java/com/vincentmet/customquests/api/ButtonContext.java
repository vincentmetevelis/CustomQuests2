package com.vincentmet.customquests.api;

import com.vincentmet.customquests.helpers.MouseButton;
import com.vincentmet.customquests.helpers.QuadConsumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

import java.util.UUID;

public class ButtonContext{
	private Component text = new TextComponent("uwu");
	private QuadConsumer<MouseButton, UUID, Integer, Integer> onClick = (mouseButton, uuid, questId, taskId)->{};
	
	public Component getText(){
		return text;
	}
	
	public ButtonContext setText(Component text){
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