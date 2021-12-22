package com.vincentmet.customquests.standardcontent.texttypes;

import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;

public class TranslationTextType implements ITextType{
    private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "translation_key");
    private TranslatableComponent translationKey;
    
    public TranslationTextType(){}
    
    public TranslationTextType(String translationKey){
        this.translationKey = new TranslatableComponent(translationKey);
    }
    
    @Override
    public ResourceLocation getId(){
        return ID;
    }
    
    @Override
    public String getOgText(){
        return translationKey.getKey();
    }
    
    @Override
    public void setOgText(String newKey){
        translationKey = new TranslatableComponent(newKey);
    }
    
    @Override
    public String getText(){
        if(translationKey != null){
            return ClientUtils.colorify(translationKey.getString());
        }
        return "";
    }
    
    public void setTranslationKey(TranslatableComponent translationKey){
        this.translationKey = translationKey;
    }
    
    public TranslatableComponent getTranslationKey(){
        return translationKey;
    }
}
