package com.vincentmet.customquests.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.api.IButtonShape;
import com.vincentmet.customquests.helpers.rendering.Color;
import com.vincentmet.customquests.hierarchy.quest.Position;
import net.minecraftforge.api.distmarker.*;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class Line implements MovableScalableCanvasEntry {
    private final int parentX;
    private final int parentY;
    
    private final int color;
    private final int thickness;
    
    private final int pos1X;
    private final int pos1Y;
    private final int pos2X;
    private final int pos2Y;

    public Line(int parentX, int parentY, int posX1, int posY1, int posX2, int posY2, int color, int thickness){
        this.parentX = parentX;
        this.parentY = parentY;
        
        this.color = color;
        this.thickness = thickness;
        
        this.pos1X = posX1;
        this.pos1Y = posY1;
        this.pos2X = posX2;
        this.pos2Y = posY2;
    }

    public Line(int parentX, int parentY, Position quest, Position dependency, double questButtonScale, double depButtonScale, int color, int thickness){
        this.parentX = parentX;
        this.parentY = parentY;
        this.color = color;
        this.thickness = thickness;
        
        Position buttonScaleAdjustedQuestPos = new Position(quest.getX() + (((int)(IButtonShape.WIDTH * questButtonScale)) >> 1) - (int)Math.floor((float)thickness / 2), quest.getY() + (((int)(IButtonShape.HEIGHT * questButtonScale)) >> 1) - (int)Math.floor((float)thickness / 2));//todo add zoomscale in the future
        Position buttonScaleAdjustedDepPos = new Position(dependency.getX() + (((int)(IButtonShape.WIDTH * depButtonScale))>>1) - (int)Math.floor((float)thickness / 2), dependency.getY() + (((int)(IButtonShape.HEIGHT * depButtonScale))>>1) -(int)Math.floor((float)thickness / 2));//todo add zoomscale in the future
        
        this.pos1X = buttonScaleAdjustedQuestPos.getX();
        this.pos1Y = buttonScaleAdjustedQuestPos.getY();
        this.pos2X = buttonScaleAdjustedDepPos.getX();
        this.pos2Y = buttonScaleAdjustedDepPos.getY();
    }
    
    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        matrixStack.pushPose();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Color.color(color);
    
        GL11.glLineWidth(thickness);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2i(parentX + pos1X, parentY + pos1Y);
        GL11.glVertex2i(parentX + pos2X, parentY + pos2Y);
        GL11.glEnd();
    
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        Color.color(0xFFFFFFFF);

        matrixStack.popPose();
    }
    
    @Override
    public void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
    
    }
}