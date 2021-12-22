package com.vincentmet.customquests.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.api.IRenderable;
import com.vincentmet.customquests.helpers.rendering.GLScissorStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class ScrollingLabel implements IRenderable{
    private static final Font FONT = Minecraft.getInstance().font;
    
    private int x;
    private int y;
    private final String text;
    private final int width;
    private final int beginEndPauseDuration;
    private final int scrollingSpeed;
    
    private final int textWidth;
    private final int maxOffset;
    
    public ScrollingLabel(int x, int y, String text, int width, int beginEndPauseDuration, int scrollingSpeed){//beginEndPauseDuration in ticks // scrollingSpeed calculated as: 1/x
        this.x = x;
        this.y = y;
        this.text = text;
        this.width = width;
        this.beginEndPauseDuration = beginEndPauseDuration;
        this.scrollingSpeed = scrollingSpeed;
    
        this.textWidth = FONT.width(text);
        this.maxOffset = this.textWidth - this.width;
    }
    
    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        GLScissorStack.push(matrixStack, x, y, width, FONT.lineHeight);
        if(this.maxOffset >= 0){
            int currentOffset = Math.min((int)((System.currentTimeMillis()/50/scrollingSpeed)%(textWidth+beginEndPauseDuration*2)), maxOffset + 2*this.beginEndPauseDuration);
            int localOffsetPause;
            if(currentOffset < beginEndPauseDuration){
                localOffsetPause = 0;
            }else{
                localOffsetPause = Math.min(currentOffset - beginEndPauseDuration, maxOffset);
            }
            FONT.drawShadow(matrixStack, text, x-localOffsetPause, y, 0xFFFFFF);//stack, text, x, y, color
        }else{
            FONT.drawShadow(matrixStack, text, x, y, 0xFFFFFF);//stack, text, x, y, color
        }
        GLScissorStack.pop(matrixStack);
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public void setX(int x){
        this.x = x;
    }
    
    public void setY(int y){
        this.y = y;
    }
    
    public String getText(){
        return text;
    }
    
    public int getWidth(){
        return width;
    }
    
    public int getBeginEndPauseDuration(){
        return beginEndPauseDuration;
    }
    
    public int getScrollingSpeed(){
        return scrollingSpeed;
    }
}
