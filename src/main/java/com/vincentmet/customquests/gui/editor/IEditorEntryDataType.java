package com.vincentmet.customquests.gui.editor;

import com.vincentmet.customquests.api.IHoverRenderable;
import com.vincentmet.customquests.helpers.CQGuiEventListener;
import net.minecraft.resources.ResourceLocation;

public interface IEditorEntryDataType extends CQGuiEventListener, IHoverRenderable {
    ResourceLocation getId();
    void init(int x, int y, int width, int height, Object initialValue);
    void save(IEditorEntry editorEntry);
}