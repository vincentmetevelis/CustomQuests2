package com.vincentmet.customquests.standardcontent.texttypes;

import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class TranslationTextType implements ITextType{
    private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "translation_key");
    private TranslationTextComponent translationKey;
    
    public TranslationTextType(){}
    
    public TranslationTextType(String translationKey){
        this.translationKey = new TranslationTextComponent(translationKey);
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
        translationKey = new TranslationTextComponent(newKey);
    }
    
    @Override
    public String getText(){
        if(translationKey != null){
            return ClientUtils.colorify(translationKey.getString());
        }
        return "";
    }
    
    public void setTranslationKey(TranslationTextComponent translationKey){
        this.translationKey = translationKey;
    }
    
    public TranslationTextComponent getTranslationKey(){
        return translationKey;
    }
}
