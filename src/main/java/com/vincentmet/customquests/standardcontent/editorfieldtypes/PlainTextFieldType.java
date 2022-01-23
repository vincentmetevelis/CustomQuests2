package com.vincentmet.customquests.standardcontent.editorfieldtypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.gui.editor.IEditorEntry;
import com.vincentmet.customquests.gui.editor.IEditorEntryDataType;
import com.vincentmet.customquests.gui.elements.SingleLineTextField;
import net.minecraft.resources.ResourceLocation;

import java.util.regex.Pattern;

public class PlainTextFieldType implements IEditorEntryDataType {
    private final ResourceLocation ID = new ResourceLocation(Ref.MODID, "plaintext");
    private SingleLineTextField value;
    private Object initialValue;
    
    @Override
    public ResourceLocation getId(){
        return ID;
    }

    @Override
    public void init(int x, int y, int width, int height, Object initialValue){
        this.initialValue = initialValue;
        this.value = new SingleLineTextField(x, y, width, height, 0xFF000000, 0xFFAAAAAA, 0xFFFFFFFF, 0xFFFFFFFF, initialValue.toString(), Pattern.compile(".*"));
    }
    
    @Override
    public boolean check(Object value){
        return value.toString() != null;
    }
    
    @Override
    public Object correct(Object value){
        if(check(value)) return value.toString();
        return "";
    }

    @Override
    public void reset(IEditorEntry editorEntry) {
        value.setText(initialValue.toString());

    }

    @Override
    public void save(IEditorEntry editorEntry) {
        initialValue = value.getText();
        editorEntry.setEditorValue(initialValue);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        value.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        value.renderHover(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        return value.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public void mouseMoved(double xPos, double mouseY){
        value.mouseMoved(xPos, mouseY);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        return value.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button){
        return value.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY){
        return value.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta){
        return value.mouseScrolled(mouseX, mouseY, delta);
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers){
        return value.keyReleased(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers){
        return value.charTyped(codePoint, modifiers);
    }
    
    @Override
    public boolean changeFocus(boolean focus){
        return value.changeFocus(focus);
    }
    
    @Override
    public boolean isMouseOver(double mouseX, double mouseY){
        return value.isMouseOver(mouseX, mouseY);
    }
}