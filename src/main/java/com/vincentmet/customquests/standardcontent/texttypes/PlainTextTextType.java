package com.vincentmet.customquests.standardcontent.texttypes;

import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.ITextType;
import net.minecraft.util.ResourceLocation;

public class PlainTextTextType implements ITextType{
    private static final ResourceLocation ID = new ResourceLocation(Ref.MODID, "plaintext");
    private String text = "";
    
    public PlainTextTextType(){}
    
    public PlainTextTextType(String text){
        this.text = text;
    }
    
    @Override
    public ResourceLocation getId(){
        return ID;
    }
    
    @Override
    public String getOgText(){
        return text;
    }
    
    @Override
    public void setOgText(String newText){
        text = newText;
    }
    
    @Override
    public String getText(){
        return text;
    }
}
