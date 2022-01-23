package com.vincentmet.customquests.gui.editor;

import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public interface IEditorEntry {
    Component getEditorLabel();
    IEditorEntryDataType getEditorEntryDataType();
    Supplier<Object> getEditorValue();
    void setEditorValue(Object value);
}