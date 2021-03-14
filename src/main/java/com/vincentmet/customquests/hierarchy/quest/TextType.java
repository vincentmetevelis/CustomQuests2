package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.*;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.standardcontent.texttypes.PlainTextTextType;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.ResourceLocation;

public class TextType implements IJsonObjectProcessor, IJsonObjectProvider{
    private final int parentId;
    private final String textTypeName;
    private final String questOrChapter;
    private ITextType type;
    
    public TextType(int parentId, String questOrChapter, String textTypeName){
        this.parentId = parentId;
        this.questOrChapter = questOrChapter;
        this.textTypeName = textTypeName;
    }
    
    @Override
    public void processJson(JsonObject json){
        if(json.has("type")){
            JsonElement jsonElement = json.get("type");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isString()){
                    String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
                    ResourceLocation jsonResourceLocationValue = new ResourceLocation(jsonPrimitiveStringValue);
                    for(Map.Entry<ResourceLocation, Supplier<ITextType>> entry : CQRegistry.getTextTypes().entrySet()){
                        if(entry.getKey().toString().equals(jsonResourceLocationValue.toString())){
                            setType(entry.getValue().get());
                        }
                    }
                    if(type == null){
                        Ref.CustomQuests.LOGGER.warn("'" + questOrChapter + " > " + parentId + " > " + textTypeName + " > type': Value does not match a registered ButtonShape, please download the addon mod it belongs to, or change it to something valid. Defaulting to 'customquests:plaintext'");
                        setType(new PlainTextTextType());
                    }
                }else{
                    Ref.CustomQuests.LOGGER.warn("'" + questOrChapter + " > " + parentId + " > " + textTypeName + " > type': Value is not a String, defaulting to 'customquests:plaintext'!");
                    setType(new PlainTextTextType());
                }
            }else{
                Ref.CustomQuests.LOGGER.warn("'" + questOrChapter + " > " + parentId + " > " + textTypeName + " > type': Value is not a JsonPrimitive, please use a String, defaulting to 'customquests:plaintext'!");
                setType(new PlainTextTextType());
            }
        }else{
            Ref.CustomQuests.LOGGER.warn("'" + questOrChapter + " > " + parentId + " > " + textTypeName + " > type': Not detected, defaulting to 'customquests:plaintext'!");
            setType(new PlainTextTextType());
        }
        
        if(json.has("text")){
            JsonElement jsonElement = json.get("text");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isString()){
                    type.setOgText(jsonPrimitive.getAsString());
                }else{
                    Ref.CustomQuests.LOGGER.warn("'" + questOrChapter + " > " + parentId + " > " + textTypeName + " > text': Value is not a String, defaulting to an empty String!");
                }
            }else{
                Ref.CustomQuests.LOGGER.warn("'" + questOrChapter + " > " + parentId + " > " + textTypeName + " > text': Value is not a JsonPrimitive, please use a String, defaulting to an empty String!");
            }
        }else{
            Ref.CustomQuests.LOGGER.warn("'" + questOrChapter + " > " + parentId + " > " + textTypeName + " > text': Not detected, defaulting to an empty String!");
        }
    }
    
    @Override
    public JsonObject getJson(){
        JsonObject json = new JsonObject();
        json.addProperty("type", type.getId().toString());
        json.addProperty("text", type.getOgText());
        return json;
    }
    
    public void setType(ITextType type){
        this.type = type;
    }
    
    public ITextType getType(){
        return type;
    }
    
    public String getText(){
        return type.getText();
    }
}
