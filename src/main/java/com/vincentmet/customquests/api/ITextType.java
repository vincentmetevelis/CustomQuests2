package com.vincentmet.customquests.api;

import net.minecraft.util.ResourceLocation;

public interface ITextType{
    ResourceLocation getId();
    String getOgText();
    void setOgText(String newText);
    String getText();
}