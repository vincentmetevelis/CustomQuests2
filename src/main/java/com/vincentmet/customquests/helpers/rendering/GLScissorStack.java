package com.vincentmet.customquests.helpers.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.helpers.Quadruple;
import java.util.LinkedList;

public class GLScissorStack{
    private static final LinkedList<StackEntry> STACK = new LinkedList<>();
    
    public static void push(PoseStack matrixStack, int x, int y, int width, int height){
        if(!STACK.isEmpty()){
            if(STACK.peekFirst() != null) STACK.peekFirst().disable(matrixStack);
        }
        STACK.push(new StackEntry(x, y, width, height));
        if(STACK.peekFirst() != null) STACK.peekFirst().enable(matrixStack, getIntersectionArea());
    }
    
    public static void pop(PoseStack matrixStack){
        if(STACK.peekFirst() != null){
            STACK.peekFirst().disable(matrixStack);
            STACK.pop();
            if(STACK.peekFirst() != null){
                STACK.peekFirst().enable(matrixStack, getIntersectionArea());
            }
        }
    }
    
    private static Quadruple<Integer, Integer, Integer, Integer> getIntersectionArea(){
        int newX = STACK.stream().map(StackEntry::getX).reduce(Math::max).orElse(0);
        int newY = STACK.stream().map(StackEntry::getY).reduce(Math::max).orElse(0);
        return new Quadruple<>(
                newX,
                newY,
                STACK.stream().map(se->se.x+se.width).reduce(Math::min).map(e->e-newX).orElse(0),
                STACK.stream().map(se->se.y+se.height).reduce(Math::min).map(e->e-newY).orElse(0)
        );
    }
    
    private static class StackEntry{
        private final int x, y, width, height;
        
        public StackEntry(int x, int y, int width, int height){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        public void enable(PoseStack matrixStack, Quadruple<Integer, Integer, Integer, Integer> area){
            GLScissor.enable(matrixStack, area.getFirst(), area.getSecond(), area.getThird(), area.getFourth());
        }
        
        public void disable(PoseStack matrixStack){
            GLScissor.disable(matrixStack);
        }
    
        public int getX(){
            return x;
        }
    
        public int getY(){
            return y;
        }
    
        public int getWidth(){
            return width;
        }
    
        public int getHeight(){
            return height;
        }
    }
}