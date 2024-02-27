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

    private static final IButtonShape DEFAULT_SHAPE = Shape.HEXAGON;
    private static final IQuestingTexture DEFAULT_ICON = new ItemSlideshowTexture(Blocks.GRASS_BLOCK.getRegistryName(), new ItemStack(Blocks.GRASS_BLOCK));
    private static final double DEFAULT_SCALE = 1D;
    private final int parentQuestId;
    private IButtonShape shape;
    private IQuestingTexture icon;
    private double scale = 1D;
    
    public QuestButton(int parentQuestId){
        this.parentQuestId = parentQuestId;
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
        if(shape != null && CQRegistry.getButtonShapes().containsKey(shape.getId())){
            this.shape = shape;
        }else{
            Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > shape': Value does not match a registered ButtonShape, please download the addon mod it belongs to, or change it to something valid. Defaulting to '"+Shape.HEXAGON.getId()+"'!");
            setShape(DEFAULT_SHAPE);
        }
    }

    public void setShape(ResourceLocation shapeRL){
        for(Map.Entry<ResourceLocation, Supplier<IButtonShape>> entry : CQRegistry.getButtonShapes().entrySet()){
            if(entry.getKey().toString().equals(shapeRL.toString())){
                setShape(entry.getValue().get());
            }
        }
        if(shape == null){
            Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > shape': Value does not match a registered ButtonShape, please download the addon mod it belongs to, or change it to something valid. Defaulting to '"+Shape.HEXAGON.getId()+"'!");
            setShape(Shape.HEXAGON);
        }
    }
    
    public void setIcon(IQuestingTexture icon){
        if (icon != null && icon.isValid()){
            this.icon = icon;
        }else{
            Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > icon': The given texture is either null or invalid, defaulting to '"+Blocks.GRASS_BLOCK.getRegistryName()+"'!");
            setIcon(DEFAULT_ICON);
        }
    }

    public void setIcon(ResourceLocation iconRL){
        if(TagHelper.Items.doesTagExist(iconRL)){
            List<ItemStack> tagStacks = new ArrayList<>();
            TagHelper.Items.getEntries(iconRL).stream().map(ItemStack::new).forEach(tagStacks::add);
            setIcon(new ItemSlideshowTexture(iconRL, tagStacks));
        }else{
            if(ForgeRegistries.ITEMS.containsKey(iconRL)){
                setIcon(new ItemSlideshowTexture(iconRL, new ItemStack(ForgeRegistries.ITEMS.getValue(iconRL))));
            }else{
                Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > icon': There is no valid item/tag with ResourceLocation '" + iconRL + "' found, defaulting to '"+Blocks.GRASS_BLOCK.getRegistryName()+"'!");
                setIcon(DEFAULT_ICON);
            }
        }
    }
    
    public void setScale(double scale){
        if (scale < 0.5) scale = 0.5;
        if (scale > 20) scale = 20;
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
                    setShape(new ResourceLocation(jsonPrimitive.getAsString()));
                }else{
                    Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > shape': Value is not a String, defaulting to '"+DEFAULT_SHAPE.getId()+"'!");
                    setShape(DEFAULT_SHAPE);
                }
            }else{
                Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > shape': Value is not a JsonPrimitive, please use a String, defaulting to '"+DEFAULT_SHAPE.getId()+"'!");
                setShape(DEFAULT_SHAPE);
            }
        }else{
            Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > shape': Not detected, defaulting to '"+DEFAULT_SHAPE.getId()+"'!");
            setShape(DEFAULT_SHAPE);
        }
        
        if(json.has("icon")){
            JsonElement jsonElement = json.get("icon");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isString()){
                    setIcon(new ResourceLocation(jsonPrimitive.getAsString()));
                }else{
                    Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > icon': Value is not a String, defaulting to '"+Blocks.GRASS_BLOCK.getRegistryName()+"'!");
                    setIcon(DEFAULT_ICON);
                }
            }else{
                Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > icon': Value is not a JsonPrimitive, please use a String, defaulting to '"+Blocks.GRASS_BLOCK.getRegistryName()+"'!");
                setIcon(DEFAULT_ICON);
            }
        }else{
            Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > icon': Not detected, defaulting to '"+Blocks.GRASS_BLOCK.getRegistryName()+"'!");
            setIcon(DEFAULT_ICON);
        }
    
        if(json.has("scale")){
            JsonElement jsonElement = json.get("scale");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isNumber()){
                    setScale(jsonPrimitive.getAsDouble());
                }else{
                    Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > scale': Value is not a Number, defaulting to '1.0D'!");
                    setScale(DEFAULT_SCALE);
                }
            }else{
                Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > scale': Value is not a JsonPrimitive, please use a Double, defaulting to '1.0D'!");
                setScale(DEFAULT_SCALE);
            }
        }else{
            Ref.CustomQuests.LOGGER.warn("'Quest > " + parentQuestId + " > button > scale': Not detected, defaulting to '1.0D'!");
            setScale(DEFAULT_SCALE);
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
            setShape(rlValue);
            EditorGuiHelper.Update.Quest.Button.requestUpdateShape(parentQuestId, getShape().getId());
        }));
        list.add(new EditorEntryWrapper(new TranslatableComponent(Ref.MODID + ".editor.keys.icon"), new ResourceLocation(Ref.MODID, "resourcelocation"), () -> getIcon().getResourceLocation().toString(), newValueObject -> {
            ResourceLocation newRL = ResourceLocation.tryParse(newValueObject.toString());
            setIcon(newRL);
            EditorGuiHelper.Update.Quest.Button.requestUpdateIcon(parentQuestId, getIcon().getResourceLocation());
        }));
        list.add(new EditorEntryWrapper(new TranslatableComponent(Ref.MODID + ".editor.keys.scale"), new ResourceLocation(Ref.MODID, "double"), this::getScale, newValueObject -> {
            setScale(Double.parseDouble(newValueObject.toString()));
            EditorGuiHelper.Update.Quest.Button.requestUpdateScale(parentQuestId, getScale());
        }));
    }
}