package com.vincentmet.customquests.gui.elements;

import com.vincentmet.customquests.helpers.Octuple;
import com.vincentmet.customquests.helpers.math.Vec2i;

public class QuadOutline{
    private Vec2i point1;
    private Vec2i point2;
    private Vec2i point3;
    private Vec2i point4;
    
    private Line line1;
    private Line line2;
    private Line line3;
    private Line line4;
    
    public QuadOutline(int color, Octuple<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> aabbccdd){
        point1 = new Vec2i(aabbccdd.getFirst(), aabbccdd.getSecond());
        point2 = new Vec2i(aabbccdd.getThird(), aabbccdd.getFourth());
        point3 = new Vec2i(aabbccdd.getFifth(), aabbccdd.getSixth());
        point4 = new Vec2i(aabbccdd.getSeventh(), aabbccdd.getEighth());
        
        line1 = new Line(0, 0, point1.getX(), point1.getY(), point2.getX(), point2.getY(), color, 2);
        line2 = new Line(0, 0, point2.getX(), point2.getY(), point3.getX(), point3.getY(), color, 2);
        line3 = new Line(0, 0, point3.getX(), point3.getY(), point4.getX(), point4.getY(), color, 2);
        line4 = new Line(0, 0, point4.getX(), point4.getY(), point1.getX(), point1.getY(), color, 2);
    }
    
    public void render(int mouseX, int mouseY, float partialTicks){
        line1.render(mouseX, mouseY, partialTicks);
        line2.render(mouseX, mouseY, partialTicks);
        line3.render(mouseX, mouseY, partialTicks);
        line4.render(mouseX, mouseY, partialTicks);
    }
}