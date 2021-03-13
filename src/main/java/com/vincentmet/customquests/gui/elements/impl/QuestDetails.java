package com.vincentmet.customquests.gui.elements.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.gui.ScreenManager;
import com.vincentmet.customquests.gui.elements.*;
import com.vincentmet.customquests.helpers.*;
import com.vincentmet.customquests.helpers.math.Vec2i;
import com.vincentmet.customquests.helpers.rendering.Color;
import com.vincentmet.customquests.helpers.rendering.*;
import com.vincentmet.customquests.network.messages.*;
import java.util.*;
import java.util.function.IntSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.*;

@OnlyIn(Dist.CLIENT)
public class QuestDetails implements IHoverRenderable, IGuiEventListener{
	public static final int CLAIM_BUTTON_WIDTH = 50;
	public static final int CLAIM_BUTTON_HEIGHT = 20;
	public static final int TASK_BUTTON_WIDTH = 50;
	public static final int TASK_BUTTON_HEIGHT = 20;
	public static final int CONTENT_MARGIN = 5;
	public static final int NEWLINE_MARGIN = 1;
	public static final int SLOT_TO_ITEM_OFFSET = 1;
	public static final int REWARDS_TOP_MARGIN = 15;
	public static final int SLOT_SIZE = 18;
	
	private ScreenManager screenManager;
	private int x;
	private int y;
	private int width;
	private int height;
	private int textScrollDistance;
	private int taskScrollDistance;
	private int rewardScrollDistance;
	
	private final List<Triple<IReorderingProcessor, Integer, Integer>> textToRender = new ArrayList<>();
	
	private ScrollingLabel taskLogicText;
	private final List<ScrollingLabel> taskTitles = new ArrayList<>();
	private final List<VariableButton> taskButtons = new ArrayList<>();
	
	private final List<VariableSlot> subtaskSlotsToRender = new ArrayList<>();
	private final List<ScrollingLabel> subtaskTextsToRender = new ArrayList<>();
	private final List<Triple<ITaskType, Integer, Integer>> subtaskIconsToRender = new ArrayList<>();
	
	private final List<VariableSlot> rewardSlotsToRender = new ArrayList<>();
	private final List<Triple<IRewardType, Integer, Integer>> rewardItemsToRender = new ArrayList<>();
	private final List<ScrollingLabel> rewardTextToRender = new ArrayList<>();
	
	private final List<QuadOutline> rewardSelectionOutlinesToRender = new ArrayList<>();
	private final List<Quintuple<Integer, Integer, Integer, Integer, Runnable>> rewardBoxesClickAreas = new ArrayList<>();
	
	private static final ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
	private static final FontRenderer FONT = Minecraft.getInstance().fontRenderer;
	
	
	private final IntSupplier tasksX = ()->x + (width >> 1) + 10;
	private final IntSupplier tasksY = ()->y;
	private final IntSupplier tasksWidth = ()->(width >> 1) - 10;
	private final IntSupplier tasksHeight = ()->(height >> 1) - 10;
	
	private final IntSupplier taskContentX = ()->tasksX.getAsInt() + CONTENT_MARGIN;
	private final IntSupplier taskContentY = ()->tasksY.getAsInt() + CONTENT_MARGIN;
	private final IntSupplier taskContentWidth = ()->tasksWidth.getAsInt() - (2 * CONTENT_MARGIN);
	private final IntSupplier taskContentHeight = ()->tasksHeight.getAsInt() - (2 * CONTENT_MARGIN);
	
	private final IntSupplier rewardsX = ()->x + (width >> 1) + 10;
	private final IntSupplier rewardsY = ()->y + (height >> 1) + 10;
	private final IntSupplier rewardsWidth = ()->(width >> 1) - 10;
	private final IntSupplier rewardsHeight = ()->(height >> 1) - 10;
	
	private final IntSupplier rewardContentX = ()->rewardsX.getAsInt() + CONTENT_MARGIN;
	private final IntSupplier rewardContentY = ()->rewardsY.getAsInt() + CONTENT_MARGIN;
	private final IntSupplier rewardContentWidth = ()->rewardsWidth.getAsInt() - (2 * CONTENT_MARGIN);
	private final IntSupplier rewardContentHeight = ()->rewardsHeight.getAsInt() - (2 * CONTENT_MARGIN);
	
	private final IntSupplier textX = ()->x;
	private final IntSupplier textY = ()->y;
	private final IntSupplier textWidth = ()->(width >> 1) - 10;
	private final IntSupplier textHeight = ()->height;
	
	private final IntSupplier textContentX = ()->textX.getAsInt() + CONTENT_MARGIN;
	private final IntSupplier textContentY = ()->textY.getAsInt() + CONTENT_MARGIN;
	private final IntSupplier textContentWidth = ()->textWidth.getAsInt() - (2 * CONTENT_MARGIN);
	private final IntSupplier textContentHeight = ()->textHeight.getAsInt() - (2 * CONTENT_MARGIN);
	
	private final Container<Integer> selectedRewardId = new Container<>(-1);
	
	private final VariableButton claimRewardButton = new VariableButton(0, 0, CLAIM_BUTTON_WIDTH, CLAIM_BUTTON_HEIGHT, ButtonState.DISABLED.getButtonTexture(), new TranslationTextComponent(Ref.MODID + ".screens.claim").getString(), new Vec2i(0, 0), (mouseButton)->{PacketHandler.CHANNEL.sendToServer(new MessageRewardClaim(screenManager.getCurrentlySelectedQuestId(), selectedRewardId.get()));}, new ArrayList<>());;
	
	public QuestDetails(ScreenManager screenManager, int x, int y, int width, int height){
		this.screenManager = screenManager;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		reInit();
	}
	
	public void reInit(){
		reInitText();
		reInitTasks();
		reInitRewards();
		applyScrollLimits();
	}
	
	public void reInitText(){
		if(screenManager.getCurrentlySelectedQuestId()>=0){
			textToRender.clear();
			IntCounter cumulativeHeight = new IntCounter(textContentY.getAsInt() - textScrollDistance, FONT.FONT_HEIGHT + NEWLINE_MARGIN);
			ITextProperties questText = new StringTextComponent(ClientUtils.colorify(QuestingStorage.getSidedQuestsMap().get(screenManager.getCurrentlySelectedQuestId()).getText().getText()));
			for(IReorderingProcessor line : FONT.trimStringToWidth(questText, textContentWidth.getAsInt())){
				textToRender.add(new Triple<>(line, textContentX.getAsInt(), cumulativeHeight.getValue()));
				cumulativeHeight.count();
			}
		}
	}
	
	public void reInitTasks(){
		if(screenManager.getCurrentlySelectedQuestId()>=0){
			String taskLogic = TextFormatting.GRAY + new TranslationTextComponent(Ref.MODID + ".general.type").getString() + ": [" + QuestingStorage.getSidedQuestsMap().get(screenManager.getCurrentlySelectedQuestId()).getTasks().getLogicType() + "]";
			int strWidth = FONT.getStringWidth(taskLogic);
			taskLogicText = new ScrollingLabel(taskContentX.getAsInt() + taskContentWidth.getAsInt() - CONTENT_MARGIN - Math.min(strWidth, taskContentWidth.getAsInt() - (2*CONTENT_MARGIN)), taskContentY.getAsInt() - CONTENT_MARGIN - FONT.FONT_HEIGHT, taskLogic, Math.min(strWidth, taskContentWidth.getAsInt() - (2*CONTENT_MARGIN)), 30, 1);
			
			IntCounter cumulativeHeight = new IntCounter(taskContentY.getAsInt() - taskScrollDistance, 20);
			taskButtons.clear();
			taskTitles.clear();
			subtaskSlotsToRender.clear();
			subtaskTextsToRender.clear();
			subtaskIconsToRender.clear();
			
			QuestingStorage.getSidedQuestsMap().get(screenManager.getCurrentlySelectedQuestId()).getTasks().forEach((taskID, task) -> {
				//Task descriptor, and some width adjustment for first subtask
				ButtonContext buttonContext = new ButtonContext();
				boolean hasButton = CQRegistry.getTaskTypes().get(task.getTaskType()).getSecond().get().hasButton(buttonContext);
				String labelText = CQRegistry.getTaskTypes().get(task.getTaskType()).getFirst().getString() + TextFormatting.GRAY + " [" + task.getSubtasks().getLogicType() + "]";
				int labelWidth = taskContentWidth.getAsInt();
				if(hasButton){
					labelWidth = taskContentWidth.getAsInt() - (2*CONTENT_MARGIN) - CLAIM_BUTTON_WIDTH;
				}
				taskTitles.add(new ScrollingLabel(taskContentX.getAsInt(), cumulativeHeight.getValue() + CONTENT_MARGIN, labelText, labelWidth, 30, 1));
				
				//If task has a button, Render it
				if(hasButton){
					Color.color(0xFFFFFFFF);
					VariableButton.ButtonTexture tex = VariableButton.ButtonTexture.DEFAULT_NORMAL;
					if(CombinedProgressHelper.isTaskCompleted(clientPlayer.getUniqueID(), screenManager.getCurrentlySelectedQuestId(), taskID)){
						tex = VariableButton.ButtonTexture.DEFAULT_DISABLED;
					}
					VariableButton taskVb = new VariableButton(taskContentX.getAsInt() + taskContentWidth.getAsInt() - (2*CONTENT_MARGIN) - TASK_BUTTON_WIDTH, cumulativeHeight.getValue(), TASK_BUTTON_WIDTH, TASK_BUTTON_HEIGHT, tex, buttonContext.getText().getString(), new Vec2i(0, 0), mouseButton->buttonContext.onClick().accept(mouseButton, clientPlayer.getUniqueID(), screenManager.getCurrentlySelectedQuestId(), taskID), new ArrayList<>());
					taskButtons.add(taskVb);
				}
				cumulativeHeight.add(20);
				
				//All the subtasks stuff
				task.getSubtasks().forEach((subtaskID, subtask) -> {
					subtaskSlotsToRender.add(new VariableSlot(CONTENT_MARGIN + taskContentX.getAsInt(), cumulativeHeight.getValue(), SLOT_SIZE, SLOT_SIZE, VariableSlot.SlotTexture.DEFAULT, (mouseButton)->{
						subtask.getSubtask().onSlotClick(clientPlayer).accept(mouseButton);
					}, new ArrayList<>()));
					
					boolean isCompleted = (CombinedProgressHelper.getValue(clientPlayer.getUniqueID(), screenManager.getCurrentlySelectedQuestId(), taskID, subtaskID) >= subtask.getSubtask().getCompletionAmount()) || (CombinedProgressHelper.isSubtaskCompleted(clientPlayer.getUniqueID(), screenManager.getCurrentlySelectedQuestId(), taskID, subtaskID)) || (CombinedProgressHelper.isTaskCompleted(clientPlayer.getUniqueID(), screenManager.getCurrentlySelectedQuestId(), taskID)) || (CombinedProgressHelper.isQuestCompleted(clientPlayer.getUniqueID(), screenManager.getCurrentlySelectedQuestId()) || (QuestingStorage.getSidedPlayersMap().get(clientPlayer.getUniqueID().toString()).getIndividualProgress().getIndividuallyCompletedQuests().contains(screenManager.getCurrentlySelectedQuestId())));
					String checkmarkText = TextFormatting.DARK_GRAY + " [" + QuestingStorage.getSidedPlayersMap().get(clientPlayer.getUniqueID().toString()).getIndividualProgress().get(screenManager.getCurrentlySelectedQuestId()).get(taskID).get(subtaskID).getValue() + "/" + QuestingStorage.getSidedQuestsMap().get(screenManager.getCurrentlySelectedQuestId()).getTasks().get(taskID).getSubtasks().get(subtaskID).getSubtask().getCompletionAmount() + "]";
					if(isCompleted)checkmarkText = TextFormatting.GREEN + " \u2713";
					String subtaskText = subtask.getSubtask().getText(clientPlayer) + checkmarkText;
					int subtaskTextWidth = taskContentWidth.getAsInt() - (2*CONTENT_MARGIN) - SLOT_SIZE;
					if(subtaskID == 0 && subtask.getSubtask().hasButton(new ButtonContext())){
						subtaskTextWidth = taskContentWidth.getAsInt() - (4*CONTENT_MARGIN) - SLOT_SIZE - TASK_BUTTON_WIDTH;
					}
					subtaskTextsToRender.add(new ScrollingLabel(SLOT_SIZE + (2*CONTENT_MARGIN) + taskContentX.getAsInt(), cumulativeHeight.getValue() + (SLOT_SIZE>>1) - (FONT.FONT_HEIGHT>>1), subtaskText, subtaskTextWidth, 30, 1));
					subtaskIconsToRender.add(new Triple<>(subtask.getSubtask(), CONTENT_MARGIN + taskContentX.getAsInt() + SLOT_TO_ITEM_OFFSET, cumulativeHeight.getValue() + SLOT_TO_ITEM_OFFSET));
					cumulativeHeight.count();
				});
			});
		}
	}
	
	public void reInitRewards(){
		if(QuestHelper.doesQuestExist(screenManager.getCurrentlySelectedQuestId())){
			IntCounter cumulativeHeight = new IntCounter(rewardContentY.getAsInt() + REWARDS_TOP_MARGIN - rewardScrollDistance, 20);
			rewardSlotsToRender.clear();
			rewardItemsToRender.clear();
			rewardTextToRender.clear();
			rewardSelectionOutlinesToRender.clear();
			rewardBoxesClickAreas.clear();
			
			//Set claim button tex
			Color.color(0xFFFFFFFF);
			if(((QuestHelper.doesRewardExist(screenManager.getCurrentlySelectedQuestId(), selectedRewardId.get()) && QuestingStorage.getSidedQuestsMap().get(screenManager.getCurrentlySelectedQuestId()).getRewards().getLogicType().equals(LogicType.OR)) || QuestingStorage.getSidedQuestsMap().get(screenManager.getCurrentlySelectedQuestId()).getRewards().getLogicType().equals(LogicType.AND)) && !CombinedProgressHelper.isQuestClaimed(clientPlayer.getUniqueID(), screenManager.getCurrentlySelectedQuestId()) && CombinedProgressHelper.isQuestCompleted(clientPlayer.getUniqueID(), screenManager.getCurrentlySelectedQuestId())){
				claimRewardButton.setTexture(ButtonState.NORMAL.getButtonTexture());
				claimRewardButton.setButtonText(new TranslationTextComponent(Ref.MODID + ".screens.claim").getString());
			}else if(CombinedProgressHelper.isQuestClaimed(clientPlayer.getUniqueID(), screenManager.getCurrentlySelectedQuestId())){
				claimRewardButton.setTexture(ButtonState.DISABLED.getButtonTexture());
				claimRewardButton.setButtonText(new TranslationTextComponent(Ref.MODID + ".screens.claimed").getString());
			}else{
				claimRewardButton.setTexture(ButtonState.DISABLED.getButtonTexture());
				claimRewardButton.setButtonText(new TranslationTextComponent(Ref.MODID + ".screens.claim").getString());
			}
			//Update reward button X/Y if needed
			if(claimRewardButton.getX() != rewardContentX.getAsInt() + rewardContentWidth.getAsInt() - (2*CONTENT_MARGIN) - CLAIM_BUTTON_WIDTH){
				claimRewardButton.setX(rewardContentX.getAsInt() + rewardContentWidth.getAsInt() - (2*CONTENT_MARGIN) - CLAIM_BUTTON_WIDTH);
			}
			if(claimRewardButton.getY() != rewardContentY.getAsInt() - rewardScrollDistance){
				claimRewardButton.setY(rewardContentY.getAsInt() - rewardScrollDistance);
			}
			QuestingStorage.getSidedQuestsMap().get(screenManager.getCurrentlySelectedQuestId()).getRewards().forEach((rewardID, reward) ->{
				if(QuestingStorage.getSidedQuestsMap().get(screenManager.getCurrentlySelectedQuestId()).getRewards().getLogicType().equals(LogicType.OR)){
					rewardBoxesClickAreas.add(new Quintuple<>(
							rewardContentX.getAsInt(),
							cumulativeHeight.getValue() - 1,
							rewardContentX.getAsInt() + rewardContentWidth.getAsInt() - 2,
							cumulativeHeight.getValue() + reward.getSubRewards().size()*(SLOT_SIZE+1) + 1,
							()->selectedRewardId.set(rewardID)
					));
					if(!CombinedProgressHelper.isQuestClaimed(clientPlayer.getUniqueID(), screenManager.getCurrentlySelectedQuestId())){
						if(selectedRewardId.get().equals(rewardID)){
							rewardSelectionOutlinesToRender.add(new QuadOutline(0xFFFFFFFF, new Octuple<>(
									rewardContentX.getAsInt() + 1,
									cumulativeHeight.getValue() - 1,
									rewardContentX.getAsInt() + rewardContentWidth.getAsInt() - 2,
									cumulativeHeight.getValue() - 1,
									rewardContentX.getAsInt() + rewardContentWidth.getAsInt() - 2,
									cumulativeHeight.getValue() + reward.getSubRewards().size()*(SLOT_SIZE+1),
									rewardContentX.getAsInt() + 1,
									cumulativeHeight.getValue() + reward.getSubRewards().size()*(SLOT_SIZE+1)
							)));
						}else if(selectedRewardId.get().equals(-1) && reward.getSubRewards().size() >= 1 && rewardID == 0){
							rewardSelectionOutlinesToRender.add(new QuadOutline(0x88FFFFFF, new Octuple<>(
									rewardContentX.getAsInt() + 1,
									cumulativeHeight.getValue() - 1,
									rewardContentX.getAsInt() + rewardContentWidth.getAsInt() - 2,
									cumulativeHeight.getValue() - 1,
									rewardContentX.getAsInt() + rewardContentWidth.getAsInt() - 2,
									cumulativeHeight.getValue() + reward.getSubRewards().size()*(SLOT_SIZE+1),
									rewardContentX.getAsInt() + 1,
									cumulativeHeight.getValue() + reward.getSubRewards().size()*(SLOT_SIZE+1)
							)));
						}
					}
				}
				reward.getSubRewards().forEach((subRewardID, subReward) -> {
					String rewardText = subReward.getSubreward().getText();
					int rewardTextWidth = rewardContentWidth.getAsInt() - (2*CONTENT_MARGIN) - SLOT_SIZE;
					if(rewardID == 0){
						rewardTextWidth = rewardContentWidth.getAsInt() - (4*CONTENT_MARGIN) - SLOT_SIZE - CLAIM_BUTTON_WIDTH;
					}
					rewardSlotsToRender.add(new VariableSlot(rewardContentX.getAsInt() + CONTENT_MARGIN, cumulativeHeight.getValue(), SLOT_SIZE, SLOT_SIZE, VariableSlot.SlotTexture.DEFAULT, (mouseButton)->{
						subReward.getSubreward().onSlotClick().accept(mouseButton);
					}, new ArrayList<>()));
					rewardItemsToRender.add(new Triple<>(subReward.getSubreward(),  + CONTENT_MARGIN + rewardContentX.getAsInt()+SLOT_TO_ITEM_OFFSET, cumulativeHeight.getValue()+SLOT_TO_ITEM_OFFSET));
					rewardTextToRender.add(new ScrollingLabel(SLOT_SIZE + (2*CONTENT_MARGIN) + rewardContentX.getAsInt(), cumulativeHeight.getValue() + (SLOT_SIZE>>1) - (FONT.FONT_HEIGHT>>1), rewardText, rewardTextWidth, 30, 1));
					cumulativeHeight.count();
				});
			});
		}
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		renderBackground(matrixStack, mouseX, mouseY, partialTicks);
		renderText(matrixStack, mouseX, mouseY, partialTicks);
		renderTasks(matrixStack, mouseX, mouseY, partialTicks);
		renderRewards(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void renderHover(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		renderHoverBackground(matrixStack, mouseX, mouseY, partialTicks);
		renderHoverText(matrixStack, mouseX, mouseY, partialTicks);
		renderHoverTasks(matrixStack, mouseX, mouseY, partialTicks);
		renderHoverRewards(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	private void renderText(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		GLScissorStack.push(textContentX.getAsInt(), textContentY.getAsInt(), textContentWidth.getAsInt(), textContentHeight.getAsInt());
		textToRender.forEach(triple->{
			FONT.func_238407_a_(matrixStack, triple.getLeft(), triple.getMiddle(), triple.getRight(), 0xFFFFFF);//renderString
		});
		GLScissorStack.pop();
	}
	
	private void renderHoverBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){/*NOOP*/}
	private void renderHoverText(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){/*NOOP*/}
	
	private void renderTasks(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		//Render logic type outside of the grey rect
		taskLogicText.render(matrixStack, mouseX, mouseY, partialTicks);
		
		//Render stuff in the box
		GLScissorStack.push(taskContentX.getAsInt(), taskContentY.getAsInt(), taskContentWidth.getAsInt(), taskContentHeight.getAsInt());
		taskTitles.forEach(scrollingLabel -> scrollingLabel.render(matrixStack, mouseX, mouseY, partialTicks));
		taskButtons.forEach(button->button.render(matrixStack, mouseX, mouseY, partialTicks));
		subtaskSlotsToRender.forEach(slot->slot.render(matrixStack, mouseX, mouseY, partialTicks));
		subtaskTextsToRender.forEach(scrollingLabel->scrollingLabel.render(matrixStack, mouseX, mouseY, partialTicks));
		subtaskIconsToRender.forEach(subtask->subtask.getLeft().getIcon(clientPlayer).render(matrixStack, 1, subtask.getMiddle(), subtask.getRight(), mouseX, mouseY));
		GLScissorStack.pop();
	}
	
	private void renderHoverTasks(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		GLScissorStack.push(taskContentX.getAsInt(), taskContentY.getAsInt(), taskContentWidth.getAsInt(), taskContentHeight.getAsInt());
		taskButtons.stream()
				.filter(button -> ApiUtils.isMouseInBounds(mouseX, mouseY, taskContentX.getAsInt(), taskContentY.getAsInt(), taskContentX.getAsInt() + taskContentWidth.getAsInt(), taskContentY.getAsInt() + taskContentHeight.getAsInt()))
				.filter(button -> ApiUtils.isMouseInBounds(mouseX, mouseY, button.getX(), button.getY(), button.getX() + button.getWidth(), button.getY() + button.getHeight()))
				.forEach(button->button.renderHover(matrixStack, mouseX, mouseY, partialTicks));
		subtaskSlotsToRender.stream()
				.filter(triple -> ApiUtils.isMouseInBounds(mouseX, mouseY, taskContentX.getAsInt(), taskContentY.getAsInt(), taskContentX.getAsInt() + taskContentWidth.getAsInt(), taskContentY.getAsInt() + taskContentHeight.getAsInt()))
				.forEach(slot->slot.renderHover(matrixStack, mouseX, mouseY, partialTicks));
		GLScissorStack.pop();
		
		subtaskIconsToRender.stream()
							.filter(triple -> triple.getLeft().getIcon(clientPlayer) instanceof ICurrentItemStackProvider)
							.filter(triple -> ApiUtils.isMouseInBounds(mouseX, mouseY, taskContentX.getAsInt(), taskContentY.getAsInt(), taskContentX.getAsInt() + taskContentWidth.getAsInt(), taskContentY.getAsInt() + taskContentHeight.getAsInt()))
							.filter(triple -> ApiUtils.isMouseInBounds(mouseX, mouseY, triple.getMiddle(), triple.getRight(), triple.getMiddle() + 16, triple.getRight() + 16))
							.forEach(triple -> triple.getLeft().onSlotHover(matrixStack, mouseX, mouseY, partialTicks, clientPlayer).run());
	}
	
	private void renderRewards(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		GLScissorStack.push(rewardContentX.getAsInt(), rewardContentY.getAsInt(), rewardContentWidth.getAsInt(), rewardContentHeight.getAsInt());
		rewardSlotsToRender.forEach(variableSlot -> variableSlot.render(matrixStack, mouseX, mouseY, partialTicks));
		RenderHelper.setupGui3DDiffuseLighting();
		rewardItemsToRender.forEach(quad ->{
			if(quad.getLeft() instanceof IItemStacksProvider){
				Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(((IItemStacksProvider)quad.getLeft()).getItemStacks().get(0), quad.getMiddle(), quad.getRight());
			}else{
				Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(quad.getLeft().getIcon()), quad.getMiddle(), quad.getRight());
			}
		});
		rewardTextToRender.forEach(scrollingLabel -> scrollingLabel.render(matrixStack, mouseX, mouseY, partialTicks));
		FONT.drawStringWithShadow(matrixStack, new TranslationTextComponent(Ref.MODID + ".screens.rewards").getString(), rewardContentX.getAsInt(), rewardContentY.getAsInt()-rewardScrollDistance, 0xFFFFFF);
		rewardSelectionOutlinesToRender.forEach(quadOutline -> quadOutline.render(matrixStack, mouseX, mouseY, partialTicks));
		claimRewardButton.render(matrixStack, mouseX, mouseY, partialTicks);
		GLScissorStack.pop();
	}
	
	private void renderHoverRewards(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		GLScissorStack.push(rewardContentX.getAsInt(), rewardContentY.getAsInt(), rewardContentWidth.getAsInt(), rewardContentHeight.getAsInt());
		rewardSlotsToRender.stream()
				.filter(triple -> ApiUtils.isMouseInBounds(mouseX, mouseY, rewardContentX.getAsInt(), rewardContentY.getAsInt(), rewardContentX.getAsInt() + rewardContentWidth.getAsInt(), rewardContentY.getAsInt() + rewardContentHeight.getAsInt()))
				.forEach(variableSlot -> variableSlot.renderHover(matrixStack, mouseX, mouseY, partialTicks));
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, rewardContentX.getAsInt(), rewardContentY.getAsInt(), rewardContentX.getAsInt() + rewardContentWidth.getAsInt(), rewardContentY.getAsInt() + rewardContentHeight.getAsInt())){
			claimRewardButton.renderHover(matrixStack, mouseX, mouseY, partialTicks);
		}
		GLScissorStack.pop();
		
		rewardItemsToRender.stream()
						   .filter(triple -> ApiUtils.isMouseInBounds(mouseX, mouseY, triple.getMiddle(), triple.getRight(), triple.getMiddle() + 16, triple.getRight() + 16))
						   .filter(triple -> ApiUtils.isMouseInBounds(mouseX, mouseY, rewardContentX.getAsInt(), rewardContentY.getAsInt(), rewardContentX.getAsInt() + rewardContentWidth.getAsInt(), rewardContentY.getAsInt() + rewardContentHeight.getAsInt()))
						   .forEach(triple -> triple.getLeft().onSlotHover(matrixStack, mouseX, mouseY, partialTicks).run());
	}
	
	private void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		GLScissorStack.push(textX.getAsInt(), textY.getAsInt(), textWidth.getAsInt(), textHeight.getAsInt());
		AbstractGui.fill(matrixStack, textX.getAsInt(), textY.getAsInt(), textX.getAsInt() + textWidth.getAsInt(), textY.getAsInt() + textHeight.getAsInt(), 0x88000000);
		GLScissorStack.pop();
		
		GLScissorStack.push(tasksX.getAsInt(), tasksY.getAsInt(), tasksWidth.getAsInt(), tasksHeight.getAsInt());
		AbstractGui.fill(matrixStack, tasksX.getAsInt(), tasksY.getAsInt(), tasksX.getAsInt() + tasksWidth.getAsInt(), tasksY.getAsInt() + tasksHeight.getAsInt(), 0x88000000);
		GLScissorStack.pop();
		
		GLScissorStack.push(rewardsX.getAsInt(), rewardsY.getAsInt(), rewardsWidth.getAsInt(), rewardsHeight.getAsInt());
		AbstractGui.fill(matrixStack, rewardsX.getAsInt(), rewardsY.getAsInt(), rewardsX.getAsInt() + rewardsWidth.getAsInt(), rewardsY.getAsInt() + rewardsHeight.getAsInt(), 0x88000000);
		GLScissorStack.pop();
	}
	
	private void applyScrollLimits(){
		if(this.textScrollDistance < 0){
			this.textScrollDistance = 0;
		}
		
		if(this.textScrollDistance > getMaxTextScroll()){
			this.textScrollDistance = getMaxTextScroll();
		}
		
		if(this.taskScrollDistance < 0){
			this.taskScrollDistance = 0;
		}
		
		if(this.taskScrollDistance > getMaxTaskScroll()){
			this.taskScrollDistance = getMaxTaskScroll();
		}
		
		if(this.rewardScrollDistance < 0){
			this.rewardScrollDistance = 0;
		}
		
		if(this.rewardScrollDistance > getMaxRewardScroll()){
			this.rewardScrollDistance = getMaxRewardScroll();
		}
	}
	
	private int getMaxTextScroll(){
		return Math.max(this.getTextContentHeight() - textContentHeight.getAsInt(), 0);
	}
	
	private int getMaxTaskScroll(){
		return Math.max(this.getTaskContentHeight() - taskContentHeight.getAsInt(), 0);
	}
	
	private int getMaxRewardScroll(){
		return Math.max(this.getRewardContentHeight() - rewardContentHeight.getAsInt(), 0);
	}
	
	private int getScrollAmount(){
		return 10;
	}
	
	private int getTextContentHeight(){
		if(screenManager.getCurrentlySelectedQuestId()>=0){
			return FONT.trimStringToWidth(new StringTextComponent(ClientUtils.colorify(QuestingStorage.getSidedQuestsMap().get(screenManager.getCurrentlySelectedQuestId()).getText().getText())), textContentWidth.getAsInt()).size() * (FONT.FONT_HEIGHT + NEWLINE_MARGIN);
		}
		return 0;
	}
	
	private int getTaskContentHeight(){
		if(screenManager.getCurrentlySelectedQuestId()>=0){
			IntCounter cumulativeHeight = new IntCounter(0, 20);
			QuestingStorage.getSidedQuestsMap().get(screenManager.getCurrentlySelectedQuestId()).getTasks().forEach((taskID, task)->{
				cumulativeHeight.count();
				task.getSubtasks().forEach((subtaskId, subtask)->{
					cumulativeHeight.count();
				});
			});
			return cumulativeHeight.getValue();
		}
		return 0;
	}
	
	private int getRewardContentHeight(){ //height = 20*subrewards + 14
		if(screenManager.getCurrentlySelectedQuestId()>=0){
			return 14 + getTotalAmountOfSubrewards() * 20; //14 + 20*reward_count
		}
		return 0;
	}
	
	private int getTotalAmountOfSubrewards(){
		IntCounter counter = new IntCounter();
		QuestingStorage.getSidedQuestsMap().get(screenManager.getCurrentlySelectedQuestId()).getRewards().forEach((rewardID, reward) -> counter.add(reward.getSubRewards().size()));
		return counter.getValue();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button){
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, rewardContentX.getAsInt(), rewardContentY.getAsInt(), rewardContentX.getAsInt() + rewardContentWidth.getAsInt(), rewardContentY.getAsInt() + rewardContentHeight.getAsInt())){
			claimRewardButton.mouseClicked(mouseX, mouseY, button);
		}
		taskButtons
				.stream()
				.filter(triple -> ApiUtils.isMouseInBounds(mouseX, mouseY, taskContentX.getAsInt(), taskContentY.getAsInt(), taskContentX.getAsInt() + taskContentWidth.getAsInt(), taskContentY.getAsInt() + taskContentHeight.getAsInt()))
				.forEach(variableButton -> variableButton.mouseClicked(mouseX, mouseY, button));
		subtaskSlotsToRender
				.stream()
				.filter(triple -> ApiUtils.isMouseInBounds(mouseX, mouseY, taskContentX.getAsInt(), taskContentY.getAsInt(), taskContentX.getAsInt() + taskContentWidth.getAsInt(), taskContentY.getAsInt() + taskContentHeight.getAsInt()))
				.forEach(variableSlot -> variableSlot.onClick(mouseX, mouseY, button));
		rewardSlotsToRender
				.stream()
				.filter(triple -> ApiUtils.isMouseInBounds(mouseX, mouseY, rewardContentX.getAsInt(), rewardContentY.getAsInt(), rewardContentX.getAsInt() + rewardContentWidth.getAsInt(), rewardContentY.getAsInt() + rewardContentHeight.getAsInt()))
				.forEach(variableSlot -> variableSlot.onClick(mouseX, mouseY, button));
		rewardBoxesClickAreas
				.stream()
				.filter(quintuple -> ApiUtils.isMouseInBounds(mouseX, mouseY, quintuple.getFirst(), quintuple.getSecond(), quintuple.getThird(), quintuple.getFourth()))
				.filter(quintuple -> !ApiUtils.isMouseInBounds(mouseX, mouseY, claimRewardButton.getX(), claimRewardButton.getY(), claimRewardButton.getX() + claimRewardButton.getWidth(), claimRewardButton.getY() + claimRewardButton.getHeight()))
				.filter(quintuple -> !ApiUtils.isMouseInBounds(mouseX, mouseY, rewardContentX.getAsInt() + CONTENT_MARGIN, rewardContentY.getAsInt(), rewardContentX.getAsInt() + 20, rewardContentY.getAsInt() + rewardContentHeight.getAsInt()))
				.filter(triple -> ApiUtils.isMouseInBounds(mouseX, mouseY, rewardContentX.getAsInt(), rewardContentY.getAsInt(), rewardContentX.getAsInt() + rewardContentWidth.getAsInt(), rewardContentY.getAsInt() + rewardContentHeight.getAsInt()))
				.forEach(quintuple -> quintuple.getFifth().run());
		reInitRewards();
		return true;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double dyScroll){
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, textX.getAsInt(), textY.getAsInt(), textX.getAsInt() + textWidth.getAsInt(), textY.getAsInt() + textHeight.getAsInt())){
			textScrollDistance -= dyScroll * getScrollAmount();
		}
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, tasksX.getAsInt(), tasksY.getAsInt(), tasksX.getAsInt() + tasksWidth.getAsInt(), tasksY.getAsInt() + tasksHeight.getAsInt())){
			taskScrollDistance -= dyScroll * getScrollAmount();
		}
		if(ApiUtils.isMouseInBounds(mouseX, mouseY, rewardsX.getAsInt(), rewardsY.getAsInt(), rewardsX.getAsInt() + rewardsWidth.getAsInt(), rewardsY.getAsInt() + rewardsHeight.getAsInt())){
			rewardScrollDistance -= dyScroll * getScrollAmount();
		}
		applyScrollLimits();
		reInit();
		return true;
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
	
	public void setWidth(int width){
		this.width = width;
		applyScrollLimits();
		reInit();
	}
	
	public void setHeight(int height){
		this.height = height;
		applyScrollLimits();
		reInit();
	}
}