package com.vincentmet.customquests.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.vincentmet.customquests.api.IButtonShape;
import com.vincentmet.customquests.hierarchy.quest.Position;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    private final double angle;
    private final int length;

    public Line(int parentX, int parentY, int posX1, int posY1, int posX2, int posY2, int color, int thickness){
        this.parentX = parentX;
        this.parentY = parentY;
        
        this.color = color;
        this.thickness = thickness;
        
        this.pos1X = posX1;
        this.pos1Y = posY1;
        this.pos2X = posX2;
        this.pos2Y = posY2;
        int dx = pos2X - pos1X;
        int dy = pos2Y - pos1Y;
        this.length = (int)Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        this.angle = Math.toDegrees(Math.atan2(dy,dx));
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
        int dx = pos2X - pos1X;
        int dy = pos2Y - pos1Y;
        this.length = (int)Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        this.angle = Math.toDegrees(Math.atan2(dy,dx));
    }
    
    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        int posX = parentX + this.pos1X;
        int posY = parentY + this.pos1Y;

        matrixStack.pushPose();
        matrixStack.translate(posX+(thickness>>1), posY+(thickness>>1), 0);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees((float)angle));
        matrixStack.translate(-(posX+(thickness>>1)), -(posY+(thickness>>1)), 0);
        GuiComponent.fill(matrixStack, posX, posY, posX + this.length, posY + this.thickness, color);
        matrixStack.popPose();
    }
    
    @Override
    public void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){}
}