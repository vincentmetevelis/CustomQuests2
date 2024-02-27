package com.vincentmet.customquests.gui.editor;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.api.IQuestingTexture;
import com.vincentmet.customquests.api.TextUtils;
import com.vincentmet.customquests.gui.elements.ButtonState;
import com.vincentmet.customquests.gui.elements.ScrollableListEntry;
import com.vincentmet.customquests.helpers.MouseButton;
import com.vincentmet.customquests.helpers.math.Vec2i;
import com.vincentmet.customquests.helpers.rendering.Color;
import com.vincentmet.customquests.helpers.rendering.VariableButton;
import com.vincentmet.customquests.hierarchy.chapter.Chapter;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.IntSupplier;

public class EditorChapterButton implements ScrollableListEntry{
    private final VariableButton button;
    private static final IntSupplier HEIGHT = ()->20;
    private IntSupplier x, y, width;
    private final IQuestingTexture icon;
    
    public EditorChapterButton(IntSupplier x, IntSupplier y, IntSupplier width, Chapter chapter, Consumer<MouseButton> onClickCallback){
        this.x = x;
        this.y = y;
        this.width = width;
        this.icon = chapter.getIcon();
        this.button = new VariableButton(0, 0, x, y, width, HEIGHT, VariableButton.ButtonTexture.DEFAULT_NORMAL, chapter.getId() + " - " + chapter.getTitle().getStyledText(), new Vec2i(10, 0), onClickCallback, new ArrayList<>());
    }
    
    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        matrixStack.pushPose();
        Color.color(0xFFFFFFFF);
        this.button.render(matrixStack, mouseX, mouseY, partialTicks);
        Lighting.setupFor3DItems();
        icon.render(matrixStack, 1, x.getAsInt() + 2, y.getAsInt() + 2, 0, 0, mouseX, mouseY);
        matrixStack.popPose();
    }
    
    @Override
    public void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        matrixStack.pushPose();
        Color.color(0xFFFFFFFF);
        this.button.renderHover(matrixStack, mouseX, mouseY, partialTicks);
        Lighting.setupFor3DItems();
        icon.render(matrixStack, 1, x.getAsInt() + 2, y.getAsInt() + 2, 0, 0, mouseX, mouseY);
        matrixStack.popPose();
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        return this.button.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public void setX(IntSupplier x){
        this.x = x;
        this.button.setX(x);
    }
    
    @Override
    public void setY(IntSupplier y){
        this.y = y;
        this.button.setY(y);
    }
    
    @Override
    public IntSupplier getX(){
        return x;
    }
    
    @Override
    public IntSupplier getY(){
        return y;
    }
    
    @Override
    public IntSupplier getWidth(){
        return width;
    }
    
    @Override
    public IntSupplier getHeight(){
        return HEIGHT;
    }
    
    @Override
    public void setWidth(IntSupplier width){
        this.width = width;
        this.button.setWidth(width);
    }
    
    @Override
    public void setHeight(IntSupplier height){/*NOOP*/}
    
    public IQuestingTexture getIcon(){
        return icon;
    }
    
    public void setButtonState(ButtonState buttonState){
        this.button.setTexture(buttonState.getButtonTexture());
    }
}
