package com.vincentmet.customquests.gui.editor;

import com.vincentmet.customquests.api.CQRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EditorEntryWrapper implements IEditorEntry{
    private Component label;
    private IEditorEntryDataType dataType;
    private Supplier<Object> editorValueGetter;
    private Consumer<Object> editorValueSetter;

    public EditorEntryWrapper(Component label, ResourceLocation dataType, Supplier<Object> editorValueGetter, Consumer<Object> editorValueSetter){
        this.label = label;
        this.dataType = CQRegistry.getEditorEntryDataTypes().get(dataType).get();
        this.editorValueGetter = editorValueGetter;
        this.editorValueSetter = editorValueSetter;
    }

    @Override
    public Component getEditorLabel() {
        return label;
    }

    @Override
    public IEditorEntryDataType getEditorEntryDataType() {
        return dataType;
    }

    @Override
    public Supplier<Object> getEditorValue() {
        return editorValueGetter;
    }

    @Override
    public void setEditorValue(Object newValue) {
        editorValueSetter.accept(newValue);
    }
}