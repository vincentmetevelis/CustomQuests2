package com.vincentmet.customquests.gui.elements.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.gui.*;
import com.vincentmet.customquests.gui.elements.*;
import com.vincentmet.customquests.helpers.rendering.GLScissorStack;
import com.vincentmet.customquests.hierarchy.quest.Quest;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.*;
import org.lwjgl.glfw.GLFW;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

@OnlyIn(Dist.CLIENT)
public class QuestingCanvas implements IHoverRenderable, GuiEventListener{
	private static final Player PLAYER = Minecraft.getInstance().player;
	private final ScreenManager screenManager;
	private int x, y, width, height;
	private final List<MovableScalableCanvasEntry> entries = new ArrayList<>();
	private int offsetX, offsetY;
	
	public QuestingCanvas(ScreenManager screenManager){
		this.screenManager = screenManager;
		reInit(0, 0, 1, 1);
	}
	
	public void reInit(int x, int y, int width, int height){
		entries.clear();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		//Add all lines first so that they appear behind the quest buttons
		QuestingStorage.getSidedQuestsMap().entrySet()
		        .stream()
		        .filter(entry -> {
		        	if(entry.getValue().hasChapter()){
		        		return entry.getValue().getChapter().getId() == screenManager.getCurrentlySelectedChapterId();
			        }
		        	return false;
		        })
		        .forEach(entry -> {
			        entry.getValue().getDependencyList().forEach(depID -> {
				        Quest q1 = QuestHelper.getQuestFromId(entry.getKey());
				        Quest q2 = QuestHelper.getQuestFromId(depID);
				        if(ChapterHelper.areQuestsInSameChapter(entry.getKey(), depID)){
				            if(q1 != null && q2 != null){
					            int color = 0xFFFF0000;
					            if(CombinedProgressHelper.isQuestCompleted(PLAYER.getUUID(), q2.getQuestId()) && !CombinedProgressHelper.isQuestCompleted(PLAYER.getUUID(), q1.getQuestId()))color = 0xFFFFFF00;
					            if(CombinedProgressHelper.isQuestCompleted(PLAYER.getUUID(), q2.getQuestId()) && CombinedProgressHelper.isQuestCompleted(PLAYER.getUUID(), q1.getQuestId()))color = 0xFF00FF00;
					            entries.add(new Line(x, y, q1.getPosition().clone().add(offsetX, offsetY), q2.getPosition().clone().add(offsetX, offsetY), q1.getButton().getScale(), q2.getButton().getScale(), color, 2));
				            }
				        }
			        });
		        })
		;
		//Add all Quest buttons
		QuestingStorage.getSidedQuestsMap().entrySet()
		        .stream()
		        .filter(entry -> {
		        	if(entry.getValue().hasChapter()){
		        		return entry.getValue().getChapter().getId() == screenManager.getCurrentlySelectedChapterId();
			        }
		        	return false;
		        })
		        .forEach(entry -> {
					List<Component> questInfoList = new ArrayList<>();
					questInfoList.add(new TextComponent(ClientUtils.colorify(entry.getValue().getTitle().getText()) + ChatFormatting.RESET + " #" + entry.getKey()));
					questInfoList.add(new TextComponent(ClientUtils.colorify(entry.getValue().getSubtitle().getText())));
					if(!CombinedProgressHelper.isQuestUnlocked(PLAYER.getUUID(), entry.getValue().getQuestId())){
						questInfoList.add(new TextComponent(""));
						if(entry.getValue().getDependencyList().getLogicType() == LogicType.OR){
							questInfoList.add(new TranslatableComponent("customquests.screens.complete_one", entry.getValue().getDependencyList().getLogicType()));
						}else{
							questInfoList.add(new TranslatableComponent("customquests.screens.complete_all", entry.getValue().getDependencyList().getLogicType()));
						}
						entry.getValue().getDependencyList().asQuestList().forEach(quest -> {
							questInfoList.add(new TextComponent("  - " + ClientUtils.colorify(quest.getTitle().getText()) + ChatFormatting.RESET + " #" + quest.getQuestId()));
						});
					}
					QuestButton.State buttonState = QuestButton.State.NORMAL;
					if(!CombinedProgressHelper.isQuestUnlocked(PLAYER.getUUID(), entry.getValue().getQuestId())) buttonState = QuestButton.State.DISABLED;
					if(CombinedProgressHelper.isQuestCompleted(PLAYER.getUUID(), entry.getValue().getQuestId())) buttonState = QuestButton.State.GREEN;
					if(CombinedProgressHelper.isQuestCompleted(PLAYER.getUUID(), entry.getValue().getQuestId()) && !CombinedProgressHelper.isQuestClaimed(PLAYER.getUUID(), entry.getValue().getQuestId())) buttonState = QuestButton.State.BLUE;
			
					entries.add(new QuestButton(x, y, entry.getValue().getPosition().getX() + offsetX, entry.getValue().getPosition().getY() + offsetY, entry.getKey(), entry.getValue().getButton().getIcon(), entry.getValue().getButton().getShape(), buttonState, (float)entry.getValue().getButton().getScale(), (mouseButton) -> {
						screenManager.setCurrentlySelectedQuestId(entry.getValue().getQuestId());
						Screen currentScreen = Minecraft.getInstance().screen;
						if(currentScreen instanceof QuestingScreen) ((QuestingScreen)currentScreen).questDetails.reInit();
						PLAYER.playSound(QuestingStorage.SOUNDS.get("quest" + entry.getKey()), 1, 1);
					}, questInfoList));
				})
		;
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
		GLScissorStack.push(matrixStack, x, y, width, height);
		entries.forEach(entry ->{
			entry.render(matrixStack, mouseX, mouseY, partialTicks);
		});
		GLScissorStack.pop(matrixStack);
	}
	
	@Override
	public void renderHover(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
		entries.stream()
			   .filter(entry->ApiUtils.isMouseInBounds(mouseX, mouseY, x, y, x + width, y + height))
			   .forEach(entry ->entry.renderHover(matrixStack, mouseX, mouseY, partialTicks));
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy){
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, x, y, x + width, y + height)){
			offsetX += dx;
			offsetY += dy;
			applyDraggingLimits();
			reInit(x, y, width, height);
		}
		return false;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton){
		entries.stream()
			   .filter(movableScalableCanvasEntry -> ApiUtils.isMouseInBounds(mouseX, mouseY, x, y, x + width, y + height))
			   .filter(entry -> entry instanceof QuestButton)
			   .map(entry -> (QuestButton)entry)
			   .forEach(entry -> {
						if(CombinedProgressHelper.isQuestCompleted(PLAYER.getUUID(), entry.getQuestId()) || CombinedProgressHelper.isQuestUnlocked(PLAYER.getUUID(), entry.getQuestId())){
							entry.mouseClicked(mouseX, mouseY, mouseButton);
						}
				})
		;
		return true;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int mods){
		if(keyCode == GLFW.GLFW_KEY_LEFT) offsetX += 5;
		if(keyCode == GLFW.GLFW_KEY_RIGHT) offsetX -= 5;
		if(keyCode == GLFW.GLFW_KEY_UP) offsetY += 5;
		if(keyCode == GLFW.GLFW_KEY_DOWN) offsetY -= 5;
		if(keyCode == GLFW.GLFW_KEY_HOME) setOffsetToOrigin();
		if(keyCode == GLFW.GLFW_KEY_END) setOffsetToEnd();
		if(keyCode == GLFW.GLFW_KEY_PAGE_UP) offsetY = 0;
		if(keyCode == GLFW.GLFW_KEY_PAGE_DOWN) offsetY = getMaxYOffset();
		applyDraggingLimits();
		reInit(x, y, width, height);
		return true;
	}
	
	public int getContentWidth(){
		return QuestingStorage.getSidedQuestsMap().entrySet()
							  .stream()
							  .filter(entry ->{if(entry.getValue().hasChapter()) return entry.getValue().getChapter().getId() == screenManager.getCurrentlySelectedChapterId(); return false;})
							  .reduce((integerQuestEntry, integerQuestEntry2) -> {
										if(integerQuestEntry.getValue().getPosition().getX() + (integerQuestEntry.getValue().getButton().getScale() * IButtonShape.WIDTH) > integerQuestEntry2.getValue().getPosition().getX() + (integerQuestEntry2.getValue().getButton().getScale() * IButtonShape.WIDTH)){
											return integerQuestEntry;
										}
										return integerQuestEntry2;
							  })
							  .map(integerQuestEntry -> integerQuestEntry.getValue().getPosition().getX() + (integerQuestEntry.getValue().getButton().getScale() * IButtonShape.WIDTH)).orElse(0D).intValue()//buttonpos * zoomscale + buttonsize * buttonscale // todo add zoom scale later on
		;
	}
	
	public int getContentHeight(){
		return QuestingStorage.getSidedQuestsMap().entrySet()
							  .stream()
							  .filter(entry ->{if(entry.getValue().hasChapter()) return entry.getValue().getChapter().getId() == screenManager.getCurrentlySelectedChapterId(); return false;})
							  .reduce((integerQuestEntry, integerQuestEntry2) -> {
										if(integerQuestEntry.getValue().getPosition().getY() + (integerQuestEntry.getValue().getButton().getScale() * IButtonShape.HEIGHT) > integerQuestEntry2.getValue().getPosition().getY() + (integerQuestEntry2.getValue().getButton().getScale() * IButtonShape.HEIGHT)){
											return integerQuestEntry;
										}
										return integerQuestEntry2;
							  })
							  .map(integerQuestEntry -> integerQuestEntry.getValue().getPosition().getY() + (integerQuestEntry.getValue().getButton().getScale() * IButtonShape.HEIGHT)).orElse(0D).intValue()//buttonpos * zoomscale + buttonsize * buttonscale // todo add zoom scale later on
		;
	}
	
	public int getMaxXOffset(){
		int i = -getContentWidth() + width;
		if(i>0)i=0;
		return i;
	}
	
	public int getMaxYOffset(){
		int i = -getContentHeight() + height;
		if(i>0)i=0;
		return i;
	}
	
	public void applyDraggingLimits(){
		if(offsetX > 0) offsetX = 0;
		if(offsetX < getMaxXOffset()) offsetX = getMaxXOffset();
		
		if(offsetY > 0) offsetY = 0;
		if(offsetY < getMaxYOffset()) offsetY = getMaxYOffset();
	}
	
	public void setOffsetToEnd(){
		offsetX = getMaxXOffset();
		offsetY = getMaxYOffset();
		applyDraggingLimits();
	}
	
	public void setOffsetToOrigin(){
		offsetX = 0;
		offsetY = 0;
		applyDraggingLimits();
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
	
	public int getOffsetX(){
		return offsetX;
	}
	
	public int getOffsetY(){
		return offsetY;
	}
}
