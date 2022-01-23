package com.vincentmet.customquests.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.helpers.Octuple;
import com.vincentmet.customquests.helpers.math.Vec2i;

public class QuadOutline{
    private final Line line1;
    private final Line line2;
    private final Line line3;
    private final Line line4;
    
    public QuadOutline(int color, Octuple<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> aabbccdd){
        Vec2i point1 = new Vec2i(aabbccdd.getFirst(), aabbccdd.getSecond());
        Vec2i point2 = new Vec2i(aabbccdd.getThird(), aabbccdd.getFourth());
        Vec2i point3 = new Vec2i(aabbccdd.getFifth(), aabbccdd.getSixth());
        Vec2i point4 = new Vec2i(aabbccdd.getSeventh(), aabbccdd.getEighth());
        
        line1 = new Line(0, 0, point1.getX(), point1.getY(), point2.getX(), point2.getY(), color, 1);
        line2 = new Line(0, 0, point2.getX(), point2.getY(), point3.getX(), point3.getY(), color, 1);
        line3 = new Line(0, 0, point3.getX(), point3.getY(), point4.getX(), point4.getY(), color, 1);
        line4 = new Line(0, 0, point4.getX(), point4.getY(), point1.getX(), point1.getY(), color, 1);
    }
    
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        line1.render(matrixStack, mouseX, mouseY, partialTicks);
        line2.render(matrixStack, mouseX, mouseY, partialTicks);
        line3.render(matrixStack, mouseX, mouseY, partialTicks);
        line4.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}