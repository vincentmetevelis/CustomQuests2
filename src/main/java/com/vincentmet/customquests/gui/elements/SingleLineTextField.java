package com.vincentmet.customquests.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.IHoverRenderable;
import com.vincentmet.customquests.helpers.CQGuiEventListener;
import com.vincentmet.customquests.helpers.rendering.GLScissorStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import org.lwjgl.glfw.GLFW;

import java.util.function.IntSupplier;
import java.util.regex.Pattern;

public class SingleLineTextField implements IHoverRenderable, CQGuiEventListener {
    private int x, y, width, height, backgroundColor, borderColor, cursorColor, textColor;
    
    //Textarea including border
    private final IntSupplier TEXTBOX_X = () -> x;
    private final IntSupplier TEXTBOX_Y = () -> y;
    private final IntSupplier TEXTBOX_WIDTH = () -> width;
    private final IntSupplier TEXTBOX_HEIGHT = () -> height;
    
    //Textarea excluding border
    private final IntSupplier INSIDE_BOX_X = () -> x+1;
    private final IntSupplier INSIDE_BOX_Y = () -> y+1;
    private final IntSupplier INSIDE_BOX_WIDTH = () -> width-2;
    private final IntSupplier INSIDE_BOX_HEIGHT = () -> height-2;
    
    //Textarea text only
    private final IntSupplier TEXTAREA_X = () -> INSIDE_BOX_X.getAsInt() + 1;
    private final IntSupplier TEXTAREA_Y = () -> INSIDE_BOX_Y.getAsInt() + (INSIDE_BOX_HEIGHT.getAsInt()>>1) - (Minecraft.getInstance().font.lineHeight>>1);
    private final IntSupplier TEXTAREA_WIDTH = () -> INSIDE_BOX_WIDTH.getAsInt() - 2;
    private final IntSupplier TEXTAREA_HEIGHT = () -> Minecraft.getInstance().font.lineHeight;
    
    
    private String text;
    private int offset = 0;
    private int cursorPos;
    private boolean isFocused = false;
    private final Pattern regexAllowedChars;
    
    public SingleLineTextField(int x, int y, int width, int height, int backgroundColor, int borderColor, int cursorColor, int textColor, String initialText, Pattern regexAllowedChars){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        this.cursorColor = cursorColor;
        this.textColor = textColor;
        this.text = initialText;
        this.regexAllowedChars = regexAllowedChars;
        this.setToMaxCursorPos();
    }
    
    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        GuiComponent.fill(matrixStack, TEXTBOX_X.getAsInt(), TEXTBOX_Y.getAsInt(), TEXTBOX_X.getAsInt() + TEXTBOX_WIDTH.getAsInt(), TEXTBOX_Y.getAsInt()+TEXTBOX_HEIGHT.getAsInt(), borderColor);
        GuiComponent.fill(matrixStack, INSIDE_BOX_X.getAsInt(), INSIDE_BOX_Y.getAsInt(), INSIDE_BOX_X.getAsInt() + INSIDE_BOX_WIDTH.getAsInt(), INSIDE_BOX_Y.getAsInt() + INSIDE_BOX_HEIGHT.getAsInt(), backgroundColor);
        GLScissorStack.push(matrixStack, TEXTAREA_X.getAsInt(), TEXTAREA_Y.getAsInt(), TEXTAREA_WIDTH.getAsInt(), TEXTAREA_HEIGHT.getAsInt());
        Minecraft.getInstance().font.draw(matrixStack, text, TEXTAREA_X.getAsInt() - getOffset(), TEXTAREA_Y.getAsInt(), textColor);
        GLScissorStack.pop(matrixStack);
        renderCursor(matrixStack, mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        //NOOP
    }
    
    private void renderCursor(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        if(isFocused && System.currentTimeMillis()%1000>=500){
            GuiComponent.fill(matrixStack, TEXTAREA_X.getAsInt() + getCursorPosInPx() - getOffset(), TEXTAREA_Y.getAsInt()-1, TEXTAREA_X.getAsInt() + getCursorPosInPx() - getOffset() + 1, TEXTAREA_Y.getAsInt() + TEXTAREA_HEIGHT.getAsInt() + 1, cursorColor);
        }
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers){
        if(isFocused){
            switch(keyCode){
                case GLFW.GLFW_KEY_LEFT:
                    setCursorOneToLeft();
                    break;
                case GLFW.GLFW_KEY_RIGHT:
                    setCursorOneToRight();
                    break;
                case GLFW.GLFW_KEY_HOME:
                case GLFW.GLFW_KEY_PAGE_UP:
                    setCursorToOrigin();
                    break;
                case GLFW.GLFW_KEY_END:
                case GLFW.GLFW_KEY_PAGE_DOWN:
                    setToMaxCursorPos();
                    break;
                case GLFW.GLFW_KEY_BACKSPACE:
                    removeCharBeforeCursor();
                    break;
                case GLFW.GLFW_KEY_DELETE:
                    removeCharAfterCursor();
                    break;
                case GLFW.GLFW_KEY_V:
                    if(modifiers == 2){
                        String currentText = getText();
                        String clipboardText = Minecraft.getInstance().keyboardHandler.getClipboard();
                        String sb = currentText.substring(0, getCursorPos()) + clipboardText + currentText.substring(getCursorPos(), getMaxCursorPos());
                        setText(sb);
                        setCursorToRight(clipboardText.length());
                    }
                    break;
            }
        }
        return false;
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers){//todo add some external correct method impl here via lambdas
        if(isFocused){
            StringBuilder newString = new StringBuilder(text);
            newString.insert(getCursorPos(), codePoint);
            if(regexAllowedChars.matcher(newString.toString()).matches()){
                text = newString.toString();
                setCursorOneToRight();
            }
        }
        return false;
    }

    public boolean setText(String newText){
        if(regexAllowedChars.matcher(newText).matches()) {
            text = newText.toString();
        }
        return false;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        if(ApiUtils.isMouseInBounds(mouseX, mouseY, TEXTAREA_X.getAsInt(), INSIDE_BOX_Y.getAsInt(), TEXTAREA_X.getAsInt() + TEXTAREA_WIDTH.getAsInt(), INSIDE_BOX_Y.getAsInt() + INSIDE_BOX_HEIGHT.getAsInt())){
            isFocused = true;
            int localMouseX = (int)mouseX - TEXTAREA_X.getAsInt();
            setCursorPos(getCharPosFromX(localMouseX + getOffset()));
        }else{
            isFocused = false;
        }
        return true;
    }
    
    private void removeCharBeforeCursor(){
        if(getCursorPos() > 0){
            StringBuilder currentText = new StringBuilder(text);
            currentText.deleteCharAt(getCursorPos()-1);
            text = currentText.toString();
            if(getCursorPos() < getMaxCursorPos())setCursorOneToLeft();
        }
    }
    
    private void removeCharAfterCursor(){
        if(getCursorPos() < getMaxCursorPos()){
            StringBuilder currentText = new StringBuilder(text);
            currentText.deleteCharAt(getCursorPos());
            text = currentText.toString();
        }
    }
    
    private int getCharPosFromX(int x){
        if(x > getMaxCursorPosInPx()){
            return getMaxCursorPos();
        }
        for(int lastPos = 0; lastPos < text.length(); lastPos++){
            int strWidth = getStringWidth(text.substring(0, lastPos));
            if(x < strWidth){
                return lastPos;
            }
        }
        return 0;
    }
    
    private void setOffsetToOrigin(){
        setOffset(0);
    }
    
    private int getOffset(){
        if(offset < 0) setOffsetToOrigin();
        if(offset > getMaxOffset()) setOffset(getMaxOffset());
        return offset;
    }
    
    private void setOffset(int x){
        offset = x;
    }
    
    private int getMaxOffset(){
        return -Math.min(0, TEXTAREA_WIDTH.getAsInt() - getTextWidth());
    }
    
    private int getCursorPos(){
        if(cursorPos<0) setCursorToOrigin();
        if(cursorPos>getMaxCursorPos()) setCursorPos(getMaxCursorPos());
        return cursorPos;
    }
    
    private int getCursorPosInPx(){
        return getWidthForNChars(getCursorPos());
    }
    
    private void setCursorOneToLeft(){
        setCursorPos(getCursorPos()-1);
    }

    private void setCursorToLeft(int amount){
        setCursorPos(getCursorPos()-amount);
    }

    private void setCursorToRight(int amount){
        setCursorPos(getCursorPos()+amount);
    }
    
    private void setCursorOneToRight(){
        setCursorPos(getCursorPos()+1);
    }
    
    private void setOffsetSoCursorIsVisible(){
        if(getCursorPosInPx() - TEXTAREA_WIDTH.getAsInt() > getOffset()){
            setOffset(getCursorPosInPx() - TEXTAREA_WIDTH.getAsInt());
        }
        if(getCursorPosInPx() - getOffset() < 0){
            setOffset(getCursorPosInPx());
        }
    }
    
    private void setCursorPos(int x){
        if(x<0){
            setCursorToOrigin();
        }else if(x>getMaxCursorPos()){
            setCursorPos(getMaxCursorPos());
        }else{
            this.cursorPos = x;
        }
        setOffsetSoCursorIsVisible();
    }
    
    private int getMaxCursorPos(){
        return text.length();
    }
    
    private int getMaxCursorPosInPx(){
        return getStringWidth(text);
    }
    
    private void setToMaxCursorPos(){
        setCursorPos(getMaxCursorPos());
    }
    
    private void setCursorToOrigin(){
        setCursorPos(0);
    }
    
    private int getTextWidth(){
        return getStringWidth(text);
    }
    
    //Like the other method below, but uses the current text
    private int getWidthForNChars(int n){
        return getStringWidth(text.substring(0, n));
    }
    
    //Basically a parameterized substring (i.e. when n = 6, it gets the string width of the first 6 chars in the string)
    private int getWidthForNChars(String text, int n){
        return getStringWidth(text.substring(0, n));
    }
    
    //Just a wrapper for the Mojang their method
    private int getStringWidth(String text){
        return Minecraft.getInstance().font.width(text);
    }
    
    public String getText(){
        return text;
    }
}