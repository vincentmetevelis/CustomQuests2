package com.vincentmet.customquests.hierarchy.quest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.gui.editor.EditorEntryWrapper;
import com.vincentmet.customquests.gui.editor.IEditorEntry;
import com.vincentmet.customquests.gui.editor.IEditorPage;
import com.vincentmet.customquests.helpers.TagHelper;
import com.vincentmet.customquests.standardcontent.buttonshapes.Shape;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class QuestButton implements IJsonObjectProvider, IJsonObjectProcessor, IEditorPage {
    private int parentQuestId;
    private IButtonShape shape;
    private IQuestingTexture icon;
    private double scale = 1D;
    
    public QuestButton(int parentQuestId, IButtonShape shape, IQuestingTexture icon, double scale){
        this.shape = shape;
        this.icon = icon;
        this.parentQuestId = parentQuestId;
        this.scale = scale;
    }
    
    public QuestButton(int parentQuestId, IButtonShape shape, IQuestingTexture icon){
        this.shape = shape;
        this.icon = icon;
        this.parentQuestId = parentQuestId;
    }
    
    public QuestButton(IButtonShape shape, IQuestingTexture icon, double scale){
        this.shape = shape;
        this.icon = icon;
        this.scale = scale;
    }
    
    public QuestButton(IButtonShape shape, IQuestingTexture icon){
        this.shape = shape;
        this.icon = icon;
    }
    
    public QuestButton(int parentQuestId){
        this.parentQuestId = parentQuestId;
    }
    
    public QuestButton(){
    
    }
    
    public IButtonShape getShape(){
        return shape;
    }
    
    public IQuestingTexture getIcon(){
        return icon;
    }
    
    public int getParentQuestId(){
        return parentQuestId;
    }
    
    public void setShape(IButtonShape shape){
        this.shape = shape;
    }
    
    public void setIcon(IQuestingTexture icon){
        this.icon = icon;
    }
    
    public void setScale(double scale){
        this.scale = scale;
    }
    
    public double getScale(){
        return scale;
    }
    
    @Override
    public void processJson(JsonObject json){
        if(json.has("shape")){
            JsonElement jsonElement = json.get("shape");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isString()){
                    String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
                    ResourceLocation jsonResourceLocationValue = new ResourceLocation(jsonPrimitiveStringValue);
                    for(Map.Entry<ResourceLocation, Supplier<IButtonShape>> entry : CQRegistry.getButtonShapes().entrySet()){
                        if(entry.getKey().toString().equals(jsonResourceLocationValue.toString())){
                            setShape(entry.getValue().get());
                        }
                    }
                    if(shape == null){
                        Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > shape': Value does not match a registered ButtonShape, please download the addon mod it belongs to, or change it to something valid. Defaulting to 'customquests:hexagon'!");
                        setShape(Shape.HEXAGON);
                    }
                }else{
                    Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > shape': Value is not a String, defaulting to 'customquests:hexagon'!");
                    setShape(Shape.HEXAGON);
                }
            }else{
                Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > shape': Value is not a JsonPrimitive, please use a String, defaulting to 'customquests:hexagon'!");
                setShape(Shape.HEXAGON);
            }
        }else{
            Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > shape': Not detected, defaulting to 'customquests:hexagon'!");
            setShape(Shape.HEXAGON);
        }
        
        if(json.has("icon")){
            JsonElement jsonElement = json.get("icon");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isString()){
                    String jsonPrimitiveStringValue = jsonPrimitive.getAsString();
                    ResourceLocation jsonResourceLocationValue = new ResourceLocation(jsonPrimitiveStringValue);
                    if(TagHelper.doesTagExist(jsonResourceLocationValue)){
                        List<ItemStack> tagStacks = new ArrayList<>();
                        TagHelper.getEntries(jsonResourceLocationValue).stream().map(ItemStack::new).forEach(tagStacks::add);
                        setIcon(new ItemSlideshowTexture(jsonResourceLocationValue, tagStacks));
                    }else{
                        if(ForgeRegistries.ITEMS.containsKey(jsonResourceLocationValue)){
                            setIcon(new ItemSlideshowTexture(jsonResourceLocationValue, new ItemStack(ForgeRegistries.ITEMS.getValue(jsonResourceLocationValue))));
                        }else{
                            Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > icon': There is no valid item/tag with ResourceLocation '" + jsonPrimitiveStringValue + "' found, defaulting to 'minecraft:grass_block'!");
                            setIcon(new ItemSlideshowTexture(Blocks.GRASS_BLOCK.getRegistryName(), new ItemStack(Blocks.GRASS_BLOCK)));
                        }
                    }
                }else{
                    Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > icon': Value is not a String, defaulting to 'minecraft:grass_block'!");
                    setIcon(new ItemSlideshowTexture(Blocks.GRASS_BLOCK.getRegistryName(), new ItemStack(Blocks.GRASS_BLOCK)));
                }
            }else{
                Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > icon': Value is not a JsonPrimitive, please use a String, defaulting to 'minecraft:grass_block'!");
                setIcon(new ItemSlideshowTexture(Blocks.GRASS_BLOCK.getRegistryName(), new ItemStack(Blocks.GRASS_BLOCK)));
            }
        }else{
            Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > icon': Not detected, defaulting to 'minecraft:grass_block'!");
            setIcon(new ItemSlideshowTexture(Blocks.GRASS_BLOCK.getRegistryName(), new ItemStack(Blocks.GRASS_BLOCK)));
        }
    
        if(json.has("scale")){
            JsonElement jsonElement = json.get("scale");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isNumber()){
                    double jsonPrimitiveDoubleValue = jsonPrimitive.getAsDouble();
                    setScale(jsonPrimitiveDoubleValue);
                }else{
                    Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > scale': Value is not a Double, defaulting to '1.0D'!");
                    setScale(1D);
                }
            }else{
                Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > scale': Value is not a JsonPrimitive, please use a Double, defaulting to '1.0D'!");
                setScale(1D);
            }
        }else{
            Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > scale': Not detected, defaulting to '1.0D'!");
            setScale(1D);
        }
    }
    
    @Override
    public JsonObject getJson(){
        JsonObject json = new JsonObject();
        json.addProperty("shape", shape.getId().toString());
        json.addProperty("icon", icon.toString());
        json.addProperty("scale", scale);
        return json;
    }

    @Override
    public void addPageEntries(List<IEditorEntry> list) {
        list.add(new EditorEntryWrapper(new TranslatableComponent(Ref.MODID + ".editor.keys.shape"), new ResourceLocation(Ref.MODID, "resourcelocation"), () -> getShape().getId().toString(), newValueObject -> {
            ResourceLocation rlValue = new ResourceLocation(newValueObject.toString());
            for(Map.Entry<ResourceLocation, Supplier<IButtonShape>> entry : CQRegistry.getButtonShapes().entrySet()){
                if(entry.getKey().toString().equals(rlValue.toString())){
                    setShape(entry.getValue().get());
                }
            }
            if(shape == null){
                setShape(Shape.HEXAGON);
            }
        }));
        list.add(new EditorEntryWrapper(new TranslatableComponent(Ref.MODID + ".editor.keys.icon"), new ResourceLocation(Ref.MODID, "resourcelocation"), () -> getIcon().getResourceLocation().toString(), newValueObject -> {
            ResourceLocation newRL = ResourceLocation.tryParse(newValueObject.toString());
            if(TagHelper.doesTagExist(newRL)){
                List<ItemStack> tagStacks = new ArrayList<>();
                TagHelper.getEntries(newRL).stream().map(ItemStack::new).forEach(tagStacks::add);
                setIcon(new ItemSlideshowTexture(newRL, tagStacks));
            }else{
                if(ForgeRegistries.ITEMS.containsKey(newRL)){
                    setIcon(new ItemSlideshowTexture(newRL, new ItemStack(ForgeRegistries.ITEMS.getValue(newRL))));
                }else{
                    setIcon(new ItemSlideshowTexture(Blocks.GRASS_BLOCK.getRegistryName(), new ItemStack(Blocks.GRASS_BLOCK)));
                }
            }
        }));
        list.add(new EditorEntryWrapper(new TranslatableComponent(Ref.MODID + ".editor.keys.scale"), new ResourceLocation(Ref.MODID, "resourcelocation"), this::getScale, newValueObject -> {
            setScale(Double.parseDouble(newValueObject.toString()));
        }));
    }
}