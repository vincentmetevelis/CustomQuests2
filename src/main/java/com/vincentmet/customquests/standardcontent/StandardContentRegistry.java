package com.vincentmet.customquests.standardcontent;

import com.vincentmet.customquests.api.CQRegistry;
import com.vincentmet.customquests.standardcontent.buttonshapes.Shape;
import com.vincentmet.customquests.standardcontent.rewardtypes.*;
import com.vincentmet.customquests.standardcontent.tasktypes.*;
import com.vincentmet.customquests.standardcontent.texttypes.*;

public class StandardContentRegistry{
	public static void registerTaskTypes(){
		CQRegistry.registerTaskType(ItemDetectTaskType::new);
		CQRegistry.registerTaskType(ItemCraftTaskType::new);
		CQRegistry.registerTaskType(ItemSubmitTaskType::new);
		CQRegistry.registerTaskType(TravelTaskType::new);
		CQRegistry.registerTaskType(HuntTaskType::new);
		CQRegistry.registerTaskType(BiomeDetectTaskType::new);
		CQRegistry.registerTaskType(XpDetectTaskType::new);
		CQRegistry.registerTaskType(XpSubmitTaskType::new);
		CQRegistry.registerTaskType(BlockMinedTaskType::new);
		CQRegistry.registerTaskType(BlockPlacedTaskType::new);
		CQRegistry.registerTaskType(CheckboxTaskType::new);
	}
	
	public static void registerRewardTypes(){
		CQRegistry.registerRewardType(ItemsRewardType::new);
		CQRegistry.registerRewardType(CommandRewardType::new);
		CQRegistry.registerRewardType(SummonRewardType::new);
		CQRegistry.registerRewardType(XpRewardType::new);
	}
	
	public static void registerButtonShapes(){
		CQRegistry.registerButtonShapes(()->Shape.ROUND);
		CQRegistry.registerButtonShapes(()->Shape.TRIANGLE_UP);
		CQRegistry.registerButtonShapes(()->Shape.TRIANGLE_DOWN);
		CQRegistry.registerButtonShapes(()->Shape.TRIANGLE_LEFT);
		CQRegistry.registerButtonShapes(()->Shape.TRIANGLE_RIGHT);
		CQRegistry.registerButtonShapes(()->Shape.DIAMOND);
		CQRegistry.registerButtonShapes(()->Shape.SQUARE);
		CQRegistry.registerButtonShapes(()->Shape.PENTAGON);
		CQRegistry.registerButtonShapes(()->Shape.HEXAGON);
		CQRegistry.registerButtonShapes(()->Shape.OCTAGON);
		CQRegistry.registerButtonShapes(()->Shape.HEX_STAR);
		CQRegistry.registerButtonShapes(()->Shape.HEART);
		CQRegistry.registerButtonShapes(()->Shape.TRAPEZIUM_UP);
		CQRegistry.registerButtonShapes(()->Shape.TRAPEZIUM_DOWN);
		CQRegistry.registerButtonShapes(()->Shape.TRAPEZIUM_LEFT);
		CQRegistry.registerButtonShapes(()->Shape.TRAPEZIUM_RIGHT);
		CQRegistry.registerButtonShapes(()->Shape.PARALLELOGRAM);
		CQRegistry.registerButtonShapes(()->Shape.PARALLELOGRAM_INVERTED);
		CQRegistry.registerButtonShapes(()->Shape.PARALLELOGRAM_ROTATED);
		CQRegistry.registerButtonShapes(()->Shape.PARALLELOGRAM_ROTATED_INVERTED);
		CQRegistry.registerButtonShapes(()->Shape.SPIKED_SQUARE);
		CQRegistry.registerButtonShapes(()->Shape.ROUNDED_SQUARE);
		CQRegistry.registerButtonShapes(()->Shape.ROUNDED_SQUARE_EXTRA);
		CQRegistry.registerButtonShapes(()->Shape.ROUNDED_HEXAGON);
		CQRegistry.registerButtonShapes(()->Shape.GEAR);
	}
	
	public static void registerTextTypes(){
		CQRegistry.registerTextTypes(TranslationTextType::new);
		CQRegistry.registerTextTypes(PlainTextTextType::new);
	}
}