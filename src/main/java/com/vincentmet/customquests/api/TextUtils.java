package com.vincentmet.customquests.api;

import net.minecraft.ChatFormatting;

public class TextUtils{
    public static String colorify(String string){
        for (ChatFormatting textFormatting : ChatFormatting.values()) {
            string = string.replaceAll(String.format("~%s~", textFormatting.getName().toUpperCase()), textFormatting.toString());
        }
        string = string.replaceAll("~NEWLINE~", "\n");
        string = string.replaceAll("~SINGLEQUOTE~", "\'");
        string = string.replaceAll("~DOUBLEQUOTE~", "\"");
        string = string.replaceAll("~BACKSLASH~", "\\\\");
        string = string.replaceAll("~TAB~", "\t");
        return string;
    }
}