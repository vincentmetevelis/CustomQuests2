package com.vincentmet.customquests.gui.editor;

import com.vincentmet.customquests.api.IHoverRenderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.resources.ResourceLocation;

public interface IEditorEntryDataType extends GuiEventListener, IHoverRenderable {
    ResourceLocation getId();
    void init(int x, int y, int width, int height, Object initialValue);
    boolean check(Object value);
    Object correct(Object value);
    void reset(IEditorEntry editorEntry);
    void save(IEditorEntry editorEntry);
}