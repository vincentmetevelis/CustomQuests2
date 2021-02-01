package com.vincentmet.customquests.gui.elements;

import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.helpers.rendering.Color;
import com.vincentmet.customquests.hierarchy.quest.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.*;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class Line implements MovableScalableCanvasEntry {
    private int parentX;
    private int parentY;
    
    private int x;
    private int y;
    private double angle;
    private int length;
    private int color;
    private int thickness;
    private static final ResourceLocation TEX = new ResourceLocation(Ref.MODID, "textures/gui/white.png");

    public Line(int parentX, int parentY, int posX1, int posY1, int posX2, int posY2, int color, int thickness){
        this.parentX = parentX;
        this.parentY = parentY;
        
        this.x = posX1;
        this.y = posY1;
        int dx = posX1 - posX2;
        int dy = posY1 - posY2;
        this.length = (int)Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        this.angle = Math.toDegrees(Math.atan2(dy,dx))+180;
        this.color = color;
        this.thickness = thickness;
    }

    public Line(int parentX, int parentY, int posX, int posY, double angle, int length, int color, int thickness){
        this.parentX = parentX;
        this.parentY = parentY;
        
        this.x = posX;
        this.y = posY;
        this.angle = angle;
        this.length = length;
        this.color = color;
        this.thickness = thickness;
    }

    public Line(int parentX, int parentY, Position quest, Position dependency, double questButtonScale, double depButtonScale, int color, int thickness){
        this.parentX = parentX;
        this.parentY = parentY;
        
        Position buttonScaleAdjustedQuestPos = new Position(quest.getX() + (((int)(IButtonShape.WIDTH * questButtonScale)) >> 1) - (int)Math.floor((float)thickness / 2), quest.getY() + (((int)(IButtonShape.HEIGHT * questButtonScale)) >> 1) - (int)Math.floor((float)thickness / 2));//todo add zoomscale in the future
        Position buttonScaleAdjustedDepPos = new Position(dependency.getX() + (((int)(IButtonShape.WIDTH * depButtonScale))>>1) - (int)Math.floor((float)thickness / 2), dependency.getY() + (((int)(IButtonShape.HEIGHT * depButtonScale))>>1) -(int)Math.floor((float)thickness / 2));//todo add zoomscale in the future
        
        int dx = buttonScaleAdjustedQuestPos.getX() - buttonScaleAdjustedDepPos.getX();
        int dy = buttonScaleAdjustedQuestPos.getY() - buttonScaleAdjustedDepPos.getY();
        this.length = (int)Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        this.angle = Math.toDegrees(Math.atan2(dy,dx))+180;
        this.color = color;
        this.thickness = thickness;
    
        this.x = buttonScaleAdjustedQuestPos.getX();
        this.y = buttonScaleAdjustedQuestPos.getY();
    }
    
    @Override
    public void render(int mouseX, int mouseY, float partialTicks){
        int posX = parentX + this.x;
        int posY = parentY + this.y;
        GL11.glPushMatrix();
        GL11.glTranslatef(posX, posY, 0);
        GL11.glRotated(this.angle, 0, 0, 1);
        GL11.glTranslatef(-posX, -posY, 0);
        Color.color(color);
        Minecraft.getInstance().textureManager.bindTexture(TEX);
        AbstractGui.blit(posX, posY, 0, 0, this.length, this.thickness, 1,  1);
        Color.color(0xFFFFFF);
        GL11.glPopMatrix();
    }
    
    @Override
    public void renderHover(int mouseX, int mouseY, float partialTicks){
    
    }
    
    @Override
    public int getParentX(){
        return parentX;
    }
    
    @Override
    public int getParentY(){
        return parentY;
    }
    
    @Override
    public int getX(){
        return x;
    }
    
    @Override
    public int getY(){
        return y;
    }
    
    @Override
    public int getWidth(){
        return length;
    }
    
    @Override
    public int getHeight(){
        return thickness;
    }
}