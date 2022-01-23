package com.vincentmet.customquests.api;

import net.minecraft.ChatFormatting;

public class TextUtils{
    public static String colorify(String string){
        for (ChatFormatting textFormatting : ChatFormatting.values()) {
            string = string.replaceAll(String.format("~%s~", textFormatting.getName().toUpperCase()), textFormatting.toString());
        }
        return string;
    }
}