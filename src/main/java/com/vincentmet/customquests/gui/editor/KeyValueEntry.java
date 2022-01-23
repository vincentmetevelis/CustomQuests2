package com.vincentmet.customquests.gui.editor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.api.IHoverRenderable;
import com.vincentmet.customquests.gui.EditorScreenManager;
import com.vincentmet.customquests.gui.elements.ScrollingLabel;
import com.vincentmet.customquests.helpers.math.Vec2i;
import com.vincentmet.customquests.helpers.rendering.VariableButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;

import java.util.ArrayList;
import java.util.function.IntSupplier;

public class KeyValueEntry implements GuiEventListener, IHoverRenderable{
    private EditorScreenManager screenManager;
    private ScrollingLabel label;
    private IEditorEntryDataType fieldType;
    private VariableButton reset;
    private VariableButton save;
    
    private IntSupplier x, y, width, height;
    
    private final IntSupplier LABEL_X = ()->x.getAsInt()+5;
    private final IntSupplier LABEL_Y = ()->y.getAsInt() + ((height.getAsInt()>>1) - (Minecraft.getInstance().font.lineHeight >> 1));
    private final IntSupplier LABEL_WIDTH = ()->width.getAsInt()/3-4;
    private final IntSupplier LABEL_HEIGHT = ()->Minecraft.getInstance().font.lineHeight;
    
    private final IntSupplier RESET_WIDTH = ()->height.getAsInt();
    private final IntSupplier RESET_HEIGHT = ()->height.getAsInt();
    private final IntSupplier RESET_X = ()-> x.getAsInt() + width.getAsInt() - RESET_WIDTH.getAsInt();
    private final IntSupplier RESET_Y = ()->y.getAsInt();
    
    private final IntSupplier SAVE_WIDTH = ()->height.getAsInt();
    private final IntSupplier SAVE_HEIGHT = ()->height.getAsInt();
    private final IntSupplier SAVE_X = ()->x.getAsInt() + width.getAsInt() - RESET_WIDTH.getAsInt() - SAVE_WIDTH.getAsInt();
    private final IntSupplier SAVE_Y = ()->y.getAsInt();
    
    private final IntSupplier TEXTFIELD_X = ()->x.getAsInt() + (width.getAsInt()/3);
    private final IntSupplier TEXTFIELD_Y = ()->y.getAsInt();
    private final IntSupplier TEXTFIELD_WIDTH = ()->width.getAsInt()/3*2 - 5 - RESET_WIDTH.getAsInt() - SAVE_WIDTH.getAsInt();
    private final IntSupplier TEXTFIELD_HEIGHT = ()->height.getAsInt();
    
    public KeyValueEntry(EditorScreenManager screenManager, IEditorEntry editorEntry, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height){
        this.screenManager = screenManager;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = new ScrollingLabel(LABEL_X, LABEL_Y, editorEntry.getEditorLabel().getString(), LABEL_WIDTH, 0, 1);
        this.fieldType = editorEntry.getEditorEntryDataType();
        this.fieldType.init(TEXTFIELD_X.getAsInt(), TEXTFIELD_Y.getAsInt(), TEXTFIELD_WIDTH.getAsInt(), TEXTFIELD_HEIGHT.getAsInt(), editorEntry.getEditorValue().get());
        this.save = new VariableButton(SAVE_X, SAVE_Y, SAVE_WIDTH, SAVE_HEIGHT, VariableButton.ButtonTexture.DEFAULT_NORMAL, "S", new Vec2i(), mouseButton -> fieldType.save(editorEntry), new ArrayList<>());//todo add buttonTexture changer
        this.reset = new VariableButton(RESET_X, RESET_Y, RESET_WIDTH, RESET_HEIGHT, VariableButton.ButtonTexture.DEFAULT_NORMAL, "R", new Vec2i(), mouseButton -> fieldType.reset(editorEntry), new ArrayList<>());//todo add buttonTexture changer
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        label.render(matrixStack, mouseX, mouseY, partialTicks);
        fieldType.render(matrixStack, mouseX, mouseY, partialTicks);
        save.render(matrixStack, mouseX, mouseY, partialTicks);
        reset.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        fieldType.renderHover(matrixStack, mouseX, mouseY, partialTicks);
        save.renderHover(matrixStack, mouseX, mouseY, partialTicks);
        reset.renderHover(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        fieldType.keyPressed(keyCode, scanCode, modifiers);
        save.keyPressed(keyCode, scanCode, modifiers);
        reset.keyPressed(keyCode, scanCode, modifiers);
        return true;
    }
    
    @Override
    public void mouseMoved(double mouseX, double mouseY){
        fieldType.mouseMoved(mouseX, mouseY);
        save.mouseMoved(mouseX, mouseY);
        reset.mouseMoved(mouseX, mouseY);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        fieldType.mouseClicked(mouseX, mouseY, button);
        save.mouseClicked(mouseX, mouseY, button);
        reset.mouseClicked(mouseX, mouseY, button);
        return true;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button){
        fieldType.mouseReleased(mouseX, mouseY, button);
        save.mouseReleased(mouseX, mouseY, button);
        reset.mouseReleased(mouseX, mouseY, button);
        return true;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY){
        fieldType.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        save.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        reset.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        return true;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta){
        fieldType.mouseScrolled(mouseX, mouseY, delta);
        save.mouseScrolled(mouseX, mouseY, delta);
        reset.mouseScrolled(mouseX, mouseY, delta);
        return true;
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers){
        fieldType.keyReleased(keyCode, scanCode, modifiers);
        save.keyReleased(keyCode, scanCode, modifiers);
        reset.keyReleased(keyCode, scanCode, modifiers);
        return true;
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers){
        fieldType.charTyped(codePoint, modifiers);
        save.charTyped(codePoint, modifiers);
        reset.charTyped(codePoint, modifiers);
        return true;
    }
    
    @Override
    public boolean changeFocus(boolean focus){
        fieldType.changeFocus(focus);
        save.changeFocus(focus);
        reset.changeFocus(focus);
        return true;
    }
    
    @Override
    public boolean isMouseOver(double mouseX, double mouseY){
        fieldType.isMouseOver(mouseX, mouseY);
        save.isMouseOver(mouseX, mouseY);
        reset.isMouseOver(mouseX, mouseY);
        return true;
    }
}