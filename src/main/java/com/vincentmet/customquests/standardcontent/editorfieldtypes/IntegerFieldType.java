package com.vincentmet.customquests.standardcontent.editorfieldtypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.gui.editor.IEditorEntry;
import com.vincentmet.customquests.gui.editor.IEditorEntryDataType;
import com.vincentmet.customquests.gui.elements.SingleLineTextField;
import net.minecraft.resources.ResourceLocation;

import java.util.regex.Pattern;

public class IntegerFieldType implements IEditorEntryDataType {
    private final ResourceLocation ID = new ResourceLocation(Ref.MODID, "integer");
    private SingleLineTextField rlValue;
    private Object initialValue;
    
    @Override
    public ResourceLocation getId(){
        return ID;
    }

    @Override
    public void init(int x, int y, int width, int height, Object initialValue){
        this.initialValue = initialValue;
        this.rlValue = new SingleLineTextField(x, y, width, height, 0xFF000000, 0xFFAAAAAA, 0xFFFFFFFF, 0xFFFFFFFF, initialValue.toString(), Pattern.compile("[0-9]+"));
    }

    @Override
    public boolean check(Object value) {
        try {
            Integer.parseInt(value.toString());
        }catch (Exception ignored){
            return false;
        }
        return true;
    }

    @Override
    public Object correct(Object value) {
        if(check(value))return value;
        return 0;
    }

    @Override
    public void reset(IEditorEntry editorEntry) {
        rlValue.setText(initialValue.toString());
    }

    @Override
    public void save(IEditorEntry editorEntry) {
        initialValue = rlValue.getText();
        editorEntry.setEditorValue(initialValue);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        rlValue.render(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        rlValue.renderHover(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        return rlValue.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public void mouseMoved(double xPos, double mouseY){
        rlValue.mouseMoved(xPos, mouseY);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        return rlValue.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button){
        return rlValue.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY){
        return rlValue.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta){
        return rlValue.mouseScrolled(mouseX, mouseY, delta);
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers){
        return rlValue.keyReleased(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers){
        return rlValue.charTyped(codePoint, modifiers);
    }
    
    @Override
    public boolean changeFocus(boolean focus){
        return rlValue.changeFocus(focus);
    }
    
    @Override
    public boolean isMouseOver(double mouseX, double mouseY){
        return rlValue.isMouseOver(mouseX, mouseY);
    }
}