package com.vincentmet.customquests.standardcontent.buttonshapes;

import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.hierarchy.quest.IButtonShape;
import net.minecraft.util.ResourceLocation;

public enum Shape implements IButtonShape{
    ROUND(new ResourceLocation(Ref.MODID, "round"), new ResourceLocation(Ref.MODID, "textures/gui/button_round.png")),
    TRIANGLE(new ResourceLocation(Ref.MODID, "triangle"), new ResourceLocation(Ref.MODID, "textures/gui/button_triangle.png")),
    TRIANGLE_INVERTED(new ResourceLocation(Ref.MODID, "triangle_inverted"), new ResourceLocation(Ref.MODID, "textures/gui/button_triangle_inverted.png")),
    SQUARE(new ResourceLocation(Ref.MODID, "square"), new ResourceLocation(Ref.MODID, "textures/gui/button_square.png")),
    PENTAGON(new ResourceLocation(Ref.MODID, "pentagon"), new ResourceLocation(Ref.MODID, "textures/gui/button_pentagon.png")),
    HEXAGON(new ResourceLocation(Ref.MODID, "hexagon"), new ResourceLocation(Ref.MODID, "textures/gui/button_hexagon.png")),
    HEPTAGON(new ResourceLocation(Ref.MODID, "heptagon"), new ResourceLocation(Ref.MODID, "textures/gui/button_heptagon.png")),
    OCTAGON(new ResourceLocation(Ref.MODID, "octagon"), new ResourceLocation(Ref.MODID, "textures/gui/button_octagon.png")),
    HEX_STAR(new ResourceLocation(Ref.MODID, "hex_star"), new ResourceLocation(Ref.MODID, "textures/gui/button_hex_star.png")),
    DIAMOND(new ResourceLocation(Ref.MODID, "diamond"), new ResourceLocation(Ref.MODID, "textures/gui/button_diamond.png")),
    HEART(new ResourceLocation(Ref.MODID, "heart"), new ResourceLocation(Ref.MODID, "textures/gui/button_heart.png")),
    TRAPEZIUM(new ResourceLocation(Ref.MODID, "trapezium"), new ResourceLocation(Ref.MODID, "textures/gui/button_trapezium.png")),
    TRAPEZIUM_INVERTED(new ResourceLocation(Ref.MODID, "trapezium_inverted"), new ResourceLocation(Ref.MODID, "textures/gui/button_trapezium_inverted.png")),
    PARALLELOGRAM(new ResourceLocation(Ref.MODID, "parallelogram"), new ResourceLocation(Ref.MODID, "textures/gui/button_parallelogram.png")),
    PARALLELOGRAM_INVERTED(new ResourceLocation(Ref.MODID, "parallelogram_inverted"), new ResourceLocation(Ref.MODID, "textures/gui/button_parallelogram_inverted.png")),
    PARALLELOGRAM_ROTATED(new ResourceLocation(Ref.MODID, "parallelogram_rotated"), new ResourceLocation(Ref.MODID, "textures/gui/button_parallelogram_rotated.png")),
    PARALLELOGRAM_ROTATED_INVERTED(new ResourceLocation(Ref.MODID, "parallelogram_rotated_inverted"), new ResourceLocation(Ref.MODID, "textures/gui/button_parallelogram_rotated_inverted.png")),
    ROUNDED_SQUARE(new ResourceLocation(Ref.MODID, "rounded_square"), new ResourceLocation(Ref.MODID, "textures/gui/button_rounded_square.png")),
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
