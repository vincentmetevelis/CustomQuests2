package com.vincentmet.customquests.gui.elements;

import com.vincentmet.customquests.helpers.rendering.VariableButton;

public enum ButtonState{
	NORMAL(VariableButton.ButtonTexture.DEFAULT_NORMAL),
	DISABLED(VariableButton.ButtonTexture.DEFAULT_DISABLED),
	BLUE(VariableButton.ButtonTexture.DEFAULT_BLUE),
	GREEN(VariableButton.ButtonTexture.DEFAULT_GREEN);
	
	private VariableButton.ButtonTexture buttonTexture;
	
	ButtonState(VariableButton.ButtonTexture buttonTexture){
		this.buttonTexture = buttonTexture;
	}
	
	public VariableButton.ButtonTexture getButtonTexture(){
		return buttonTexture;
	}
}
