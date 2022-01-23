package com.vincentmet.customquests.standardcontent.buttonshapes;

import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.IButtonShape;
import net.minecraft.resources.ResourceLocation;

public enum Shape implements IButtonShape{
    ROUND(new ResourceLocation(Ref.MODID, "round"), new ResourceLocation(Ref.MODID, "textures/gui/button_round.png")),
    TRIANGLE_UP(new ResourceLocation(Ref.MODID, "triangle_up"), new ResourceLocation(Ref.MODID, "textures/gui/button_triangle_up.png")),
    TRIANGLE_DOWN(new ResourceLocation(Ref.MODID, "triangle_down"), new ResourceLocation(Ref.MODID, "textures/gui/button_triangle_down.png")),
    TRIANGLE_LEFT(new ResourceLocation(Ref.MODID, "triangle_left"), new ResourceLocation(Ref.MODID, "textures/gui/button_triangle_left.png")),
    TRIANGLE_RIGHT(new ResourceLocation(Ref.MODID, "triangle_right"), new ResourceLocation(Ref.MODID, "textures/gui/button_triangle_right.png")),
    SQUARE(new ResourceLocation(Ref.MODID, "square"), new ResourceLocation(Ref.MODID, "textures/gui/button_square.png")),
    PENTAGON(new ResourceLocation(Ref.MODID, "pentagon"), new ResourceLocation(Ref.MODID, "textures/gui/button_pentagon.png")),
    HEXAGON(new ResourceLocation(Ref.MODID, "hexagon"), new ResourceLocation(Ref.MODID, "textures/gui/button_hexagon.png")),
    OCTAGON(new ResourceLocation(Ref.MODID, "octagon"), new ResourceLocation(Ref.MODID, "textures/gui/button_octagon.png")),
    HEX_STAR(new ResourceLocation(Ref.MODID, "hexagon_star"), new ResourceLocation(Ref.MODID, "textures/gui/button_hexagon_star.png")),
    DIAMOND(new ResourceLocation(Ref.MODID, "diamond"), new ResourceLocation(Ref.MODID, "textures/gui/button_diamond.png")),
    HEART(new ResourceLocation(Ref.MODID, "heart"), new ResourceLocation(Ref.MODID, "textures/gui/button_heart.png")),
    TRAPEZIUM_UP(new ResourceLocation(Ref.MODID, "trapezium_up"), new ResourceLocation(Ref.MODID, "textures/gui/button_trapezium_up.png")),
    TRAPEZIUM_DOWN(new ResourceLocation(Ref.MODID, "trapezium_down"), new ResourceLocation(Ref.MODID, "textures/gui/button_trapezium_down.png")),
    TRAPEZIUM_LEFT(new ResourceLocation(Ref.MODID, "trapezium_left"), new ResourceLocation(Ref.MODID, "textures/gui/button_trapezium_left.png")),
    TRAPEZIUM_RIGHT(new ResourceLocation(Ref.MODID, "trapezium_right"), new ResourceLocation(Ref.MODID, "textures/gui/button_trapezium_right.png")),
    PARALLELOGRAM(new ResourceLocation(Ref.MODID, "parallelogram"), new ResourceLocation(Ref.MODID, "textures/gui/button_parallelogram.png")),
    PARALLELOGRAM_INVERTED(new ResourceLocation(Ref.MODID, "parallelogram_inverted"), new ResourceLocation(Ref.MODID, "textures/gui/button_parallelogram_inverted.png")),
    PARALLELOGRAM_ROTATED(new ResourceLocation(Ref.MODID, "parallelogram_rotated"), new ResourceLocation(Ref.MODID, "textures/gui/button_parallelogram_rotated.png")),
    PARALLELOGRAM_ROTATED_INVERTED(new ResourceLocation(Ref.MODID, "parallelogram_rotated_inverted"), new ResourceLocation(Ref.MODID, "textures/gui/button_parallelogram_rotated_inverted.png")),
    SPIKED_SQUARE(new ResourceLocation(Ref.MODID, "spiked_square"), new ResourceLocation(Ref.MODID, "textures/gui/button_spiked_square.png")),
    ROUNDED_SQUARE(new ResourceLocation(Ref.MODID, "rounded_square"), new ResourceLocation(Ref.MODID, "textures/gui/button_rounded_square.png")),
    ROUNDED_SQUARE_EXTRA(new ResourceLocation(Ref.MODID, "rounded_square_extra"), new ResourceLocation(Ref.MODID, "textures/gui/button_rounded_square_extra.png")),
    ROUNDED_HEXAGON(new ResourceLocation(Ref.MODID, "rounded_hexagon"), new ResourceLocation(Ref.MODID, "textures/gui/button_rounded_hexagon.png")),
    GEAR(new ResourceLocation(Ref.MODID, "gear"), new ResourceLocation(Ref.MODID, "textures/gui/button_gear.png")),
    ;
    
    final ResourceLocation ID;
    final ResourceLocation TEXTURE;
    
    Shape(ResourceLocation id, ResourceLocation texture){
        this.ID = id;
        this.TEXTURE = texture;
    }
    
    @Override
    public ResourceLocation getId(){
        return ID;
    }
    
    @Override
    public ResourceLocation getTexture(){
        return TEXTURE;
    }
}