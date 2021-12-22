package com.vincentmet.customquests.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.gui.elements.*;
import com.vincentmet.customquests.gui.elements.impl.*;
import com.vincentmet.customquests.helpers.*;
import com.vincentmet.customquests.helpers.math.Vec2i;
import com.vincentmet.customquests.helpers.rendering.VariableButton;
import java.util.*;
import java.util.function.IntSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.*;
import org.lwjgl.glfw.GLFW;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

@OnlyIn(Dist.CLIENT)
public class QuestingScreen extends Screen{
	public static final int BUTTON_HEIGHT = 20;
	public final ScreenManager screenManager = new ScreenManager();
	private VariableButton buttonToChapters;
	private ScrollableList chapterList;
	private QuestingCanvas questingCanvas;
	public QuestDetails questDetails;
	private final Player localPlayer = Minecraft.getInstance().player;
	private BooleanContainer queueReInit = new BooleanContainer(true);
	
	private final IntSupplier questingCanvasX = ()->(width>>2) + 10;
	private final IntSupplier questingCanvasY = ()->20;
	private final IntSupplier questingCanvasWidth = ()->width - questingCanvasX.getAsInt() - 20;
	private final IntSupplier questingCanvasHeight = ()->height - 40;
	
	private final IntSupplier toChaptersX = ()->0;
	private final IntSupplier toChaptersY = ()->0;
	private final IntSupplier toChaptersWidth = ()->20;
	private final IntSupplier toChaptersHeight = ()->20;
	
	private final IntSupplier chapterListX = ()->20;
	private final IntSupplier chapterListY = ()->20;
	private final IntSupplier chapterListWidth = ()->(width>>2) - 30;
	private final IntSupplier chapterListHeight = ()->height - 40;
	
	private final IntSupplier questDetailsX = ()->20;
	private final IntSupplier questDetailsY = ()->20;
	private final IntSupplier questDetailsWidth = ()->width-40;
	private final IntSupplier questDetailsHeight = ()->height-40;
	
	private static final Component localization_noQuests = new TranslatableComponent(Ref.MODID + ".screens.no_quests");
	private static final Component localization_backToChapters = new TranslatableComponent(Ref.MODID + ".screens.back_to_chapters");
	
	private final List<Component> tooltip_backToChapters = new ArrayList<>();
	
	public QuestingScreen(OptionalInt questId){
		super(new TranslatableComponent("item." + Ref.MODID + ".questing_device"));
		if(questId.isPresent()){
			screenManager.setCurrentlySelectedQuestId(questId.getAsInt());
		}
		tooltip_backToChapters.add(localization_backToChapters);
	}
	
	@Override
	protected void init(){
		buttonToChapters = new VariableButton(toChaptersX.getAsInt(), toChaptersY.getAsInt(), toChaptersWidth.getAsInt(), toChaptersHeight.getAsInt(), ButtonState.NORMAL.getButtonTexture(), "C", new Vec2i(0, 0), (mouseButton)->screenManager.resetCurrentQuestId(), tooltip_backToChapters);
		chapterList = new ScrollableList(chapterListX.getAsInt(), chapterListY.getAsInt(), chapterListWidth.getAsInt(), chapterListHeight.getAsInt());
		questingCanvas = new QuestingCanvas(screenManager);
		questDetails = new QuestDetails(screenManager, questDetailsX.getAsInt(), questDetailsY.getAsInt(), questDetailsWidth.getAsInt(), questDetailsHeight.getAsInt());
		reInit();
	}
	
	public QuestingScreen(){
		this(OptionalInt.empty());
	}
	
	public void reInit(){
		initChapterList();
		questingCanvas.reInit(questingCanvasX.getAsInt(), questingCanvasY.getAsInt(), questingCanvasWidth.getAsInt(), questingCanvasHeight.getAsInt());
		questDetails.reInit();
	}
	
	public void initChapterList(){
		IntCounter cumulativeHeight = new IntCounter(chapterListY.getAsInt(), BUTTON_HEIGHT);
		chapterList.clear();
		QuestingStorage.getSidedChaptersMap().forEach((chapterID, chapter) -> {
			List<Component> chapterInfoList = new ArrayList<>();
			chapterInfoList.add(new TextComponent(ClientUtils.colorify(chapter.getTitle().getText()) + ChatFormatting.RESET + " #" + chapterID));
			chapterInfoList.add(new TextComponent(ClientUtils.colorify(chapter.getText().getText())));
			
			ButtonState buttonState = ButtonState.DISABLED;
			if(ChapterHelper.isChapterUnlocked(localPlayer.getUUID(), chapter)){
				buttonState = ButtonState.NORMAL;
			}
			
			chapterList.add(new ChapterButton(chapterListX.getAsInt(), cumulativeHeight.getValue(), chapterListWidth.getAsInt(), BUTTON_HEIGHT, chapter.getIcon(), ClientUtils.colorify(chapter.getTitle().getText()), buttonState, (mouseButton) -> {
				screenManager.setCurrentlySelectedChapterId(chapter.getId());
				questingCanvas.applyDraggingLimits();
				questingCanvas.reInit(questingCanvasX.getAsInt(), questingCanvasY.getAsInt(), questingCanvasWidth.getAsInt(), questingCanvasHeight.getAsInt());
				localPlayer.playSound(QuestingStorage.SOUNDS.get("chapter" + chapterID), 1, 1);
			}, chapterInfoList));
			cumulativeHeight.count();
		});
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
		TooltipBuffer.tooltipBuffer.clear();
		renderBackgrounds(matrixStack);
		
		if(screenManager.shouldShowQuestDetails()){
			//Non-hover
			buttonToChapters.render(matrixStack, mouseX, mouseY, partialTicks);
			if(questDetails.getWidth() != questDetailsWidth.getAsInt())questDetails.setWidth(questDetailsWidth.getAsInt());
			if(questDetails.getHeight() != questDetailsHeight.getAsInt())questDetails.setHeight(questDetailsHeight.getAsInt());
			questDetails.render(matrixStack, mouseX, mouseY, partialTicks);
			//Hover
			buttonToChapters.renderHover(matrixStack, mouseX, mouseY, partialTicks);
			questDetails.renderHover(matrixStack, mouseX, mouseY, partialTicks);
		}else{
			//Non-hover
			if(chapterList.getWidth() != chapterListWidth.getAsInt()){
				chapterList.setWidth(chapterListWidth.getAsInt());
			}
			if(chapterList.getHeight() != chapterListHeight.getAsInt()){
				chapterList.setHeight(chapterListHeight.getAsInt());
			}
			if(chapterList.getEntries().size() >=1 && chapterList.getEntries().get(0).getY() != 20-chapterList.getScrollDistance()){
				IntCounter chapterListCounter = new IntCounter(20-chapterList.getScrollDistance(), BUTTON_HEIGHT);
				chapterList.getEntries().forEach(entry -> {
					entry.setY(chapterListCounter.getValue());
					chapterListCounter.count();
				});
			}
			chapterList.render(matrixStack, mouseX, mouseY, partialTicks);
			//Check if window got resized
			if((questingCanvas.getX() != questingCanvasX.getAsInt()) || (questingCanvas.getY() != questingCanvasY.getAsInt()) || (questingCanvas.getWidth() != questingCanvasWidth.getAsInt()) || (questingCanvas.getHeight() != questingCanvasHeight.getAsInt())){
				questingCanvas.reInit(questingCanvasX.getAsInt(), questingCanvasY.getAsInt(), questingCanvasWidth.getAsInt(), questingCanvasHeight.getAsInt());
				questingCanvas.applyDraggingLimits();
			}
			questingCanvas.render(matrixStack, mouseX, mouseY, partialTicks);
			
			//Hover
			chapterList.renderHover(matrixStack, mouseX, mouseY, partialTicks);
			questingCanvas.renderHover(matrixStack, mouseX, mouseY, partialTicks);
			
			//Show message if there are no quests loaded
			if(QuestingStorage.getSidedQuestsMap().isEmpty()){
				Minecraft.getInstance().font.drawShadow(matrixStack, localization_noQuests.getString(), questingCanvas.getX() + 5, questingCanvas.getY() + 5, 0xFFFFFF);
			}
		}
		TooltipBuffer.tooltipBuffer.forEach(Runnable::run);
	}
	
	private void renderBackgrounds(PoseStack matrixStack){
		GuiComponent.fill(matrixStack, 0, 0, width, height, 0x88000000);
		if(!screenManager.shouldShowQuestDetails()){
			GuiComponent.fill(matrixStack, chapterListX.getAsInt(), chapterListY.getAsInt(), chapterListX.getAsInt() + chapterListWidth.getAsInt(), chapterListY.getAsInt() + chapterListHeight.getAsInt(), 0x88000000);
			GuiComponent.fill(matrixStack, questingCanvasX.getAsInt(), questingCanvasY.getAsInt(), questingCanvasX.getAsInt() + questingCanvasWidth.getAsInt(), questingCanvasY.getAsInt() + questingCanvasHeight.getAsInt(), 0x88000000);
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton){
		if(screenManager.shouldShowQuestDetails()){
			buttonToChapters.mouseClicked(mouseX, mouseY, mouseButton);
			questDetails.mouseClicked(mouseX, mouseY, mouseButton);
		}else{
			chapterList.mouseClicked(mouseX, mouseY, mouseButton);
			questingCanvas.mouseClicked(mouseX, mouseY, mouseButton);
		}
		return true;
	}
	
	@Override
	public boolean isPauseScreen(){
		return false;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double dyScroll){
		if(screenManager.shouldShowQuestDetails()){
			questDetails.mouseScrolled(mouseX, mouseY, dyScroll);
		}else{
			chapterList.mouseScrolled(mouseX, mouseY, dyScroll);
			questingCanvas.mouseScrolled(mouseX, mouseY, dyScroll);
		}
		return true;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy){
		if(screenManager.shouldShowQuestDetails()){
			questDetails.mouseDragged(mouseX, mouseY, button, dx, dy);
		}else{
			chapterList.mouseDragged(mouseX, mouseY, button, dx, dy);
			questingCanvas.mouseDragged(mouseX, mouseY, button, dx, dy);
		}
		return true;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int mods){
		if(keyCode == GLFW.GLFW_KEY_ESCAPE){
			if(Minecraft.getInstance().screen != null){
				Minecraft.getInstance().screen.onClose();
			}
			return true;
		}
		chapterList.keyPressed(keyCode, scanCode, mods);
		questingCanvas.keyPressed(keyCode, scanCode, mods);
		questDetails.keyPressed(keyCode, scanCode, mods);
		return true;
	}
	
	public void requestPosRecalc(){
		questingCanvas.applyDraggingLimits();
	}
}