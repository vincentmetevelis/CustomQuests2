package com.vincentmet.customquests.helpers;

public enum MouseButton{
    LEFT,
    MIDDLE,
    RIGHT;
    
    public static MouseButton getButtonFromGlButton(int glButton){
        return glButton==0?LEFT:((glButton==1)?RIGHT:MIDDLE);
    }
}