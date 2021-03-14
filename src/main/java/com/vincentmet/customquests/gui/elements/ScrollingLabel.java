package com.vincentmet.customquests.gui.elements;

import com.vincentmet.customquests.api.IRenderable;
import com.vincentmet.customquests.helpers.rendering.GLScissorStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class ScrollingLabel implements IRenderable{
    private static final FontRenderer FONT = Minecraft.getInstance().fontRenderer;
    
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
    
        this.textWidth = FONT.getStringWidth(text);
        this.maxOffset = this.textWidth - this.width;
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks){
        GLScissorStack.push(x, y, width, FONT.FONT_HEIGHT);
        if(this.maxOffset >= 0){
            int currentOffset = Math.min((int)((System.currentTimeMillis()/50/scrollingSpeed)%(textWidth+beginEndPauseDuration*2)), maxOffset + 2*this.beginEndPauseDuration);
            int localOffsetPause;
            if(currentOffset < beginEndPauseDuration){
                localOffsetPause = 0;
            }else{
                localOffsetPause = Math.min(currentOffset - beginEndPauseDuration, maxOffset);
            }
            FONT.drawStringWithShadow(text, x-localOffsetPause, y, 0xFFFFFF);//stack, text, x, y, color
        }else{
            FONT.drawStringWithShadow(text, x, y, 0xFFFFFF);//stack, text, x, y, color
        }
        GLScissorStack.pop();
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
