package com.vincentmet.customquests.hierarchy.chapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.gui.editor.EditorEntryWrapper;
import com.vincentmet.customquests.gui.editor.IEditorEntry;
import com.vincentmet.customquests.gui.editor.IEditorPage;
import com.vincentmet.customquests.standardcontent.texttypes.PlainTextTextType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ChapterTextTextType implements IJsonObjectProcessor, IJsonObjectProvider, IEditorPage {
    private final int parentId;
    private ITextType type;

    public ChapterTextTextType(int parentId){
        this.parentId = parentId;
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
                        Ref.CustomQuests.LOGGER.warn("'Chapter > " + parentId + " > text > type': Value does not match a registered TextType, please download the addon mod it belongs to, or change it to something valid. Defaulting to 'customquests:plaintext'");
                        setType(new PlainTextTextType());
                    }
                }else{
                    Ref.CustomQuests.LOGGER.warn("'Chapter > " + parentId + " > text > type': Value is not a String, defaulting to 'customquests:plaintext'!");
                    setType(new PlainTextTextType());
                }
            }else{
                Ref.CustomQuests.LOGGER.warn("'Chapter > " + parentId + " > text > type': Value is not a JsonPrimitive, please use a String, defaulting to 'customquests:plaintext'!");
                setType(new PlainTextTextType());
            }
        }else{
            Ref.CustomQuests.LOGGER.warn("'Chapter > " + parentId + " > text > type': Not detected, defaulting to 'customquests:plaintext'!");
            setType(new PlainTextTextType());
        }
        
        if(json.has("text")){
            JsonElement jsonElement = json.get("text");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isString()){
                    type.setOgText(jsonPrimitive.getAsString());
                }else{
                    Ref.CustomQuests.LOGGER.warn("'Chapter > " + parentId + " > text > text': Value is not a String, defaulting to an empty String!");
                }
            }else{
                Ref.CustomQuests.LOGGER.warn("'Chapter > " + parentId + " > text > text': Value is not a JsonPrimitive, please use a String, defaulting to an empty String!");
            }
        }else{
            Ref.CustomQuests.LOGGER.warn("'Chapter > " + parentId + " > text > text': Not detected, defaulting to an empty String!");
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
        String ogText = "";
        if(this.type != null){
            ogText = this.type.getOgText();
        }
        this.type = type;
        this.type.setOgText(ogText);
    }
    
    public ITextType getType(){
        return type;
    }
    
    public String getText(){
        return type.getText();
    }

    public void setText(String newText){
        type.setOgText(newText);
    }

    @Override
    public void addPageEntries(List<IEditorEntry> list) {
        list.add(new EditorEntryWrapper(new TextComponent("Type"), new ResourceLocation(Ref.MODID, "resourcelocation"), () -> type.getId().toString(), newValueObject -> {
            ResourceLocation rlValue = new ResourceLocation(newValueObject.toString());
            for(Map.Entry<ResourceLocation, Supplier<ITextType>> entry : CQRegistry.getTextTypes().entrySet()){
                if(entry.getKey().toString().equals(rlValue.toString())){
                    setType(entry.getValue().get());
                }
            }
            if(type == null){
                setType(new PlainTextTextType());
            }
            ClientUtils.EditorMessages.Update.Chapter.Text.requestUpdateChapterTextType(parentId, getType().getId());
        }));
        list.add(new EditorEntryWrapper(new TextComponent("Text"), new ResourceLocation(Ref.MODID, "plaintext"), () -> type.getOgText(), newValueObject -> {
            System.out.println(newValueObject.toString());
            setText(newValueObject.toString());
            ClientUtils.EditorMessages.Update.Chapter.Text.requestUpdateChapterTextText(parentId, getText());
        }));
    }
}