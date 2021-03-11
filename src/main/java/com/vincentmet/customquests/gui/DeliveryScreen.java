package com.vincentmet.customquests.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.gui.elements.*;
import com.vincentmet.customquests.helpers.*;
import com.vincentmet.customquests.helpers.math.Vec2i;
import com.vincentmet.customquests.helpers.rendering.*;
import com.vincentmet.customquests.network.messages.*;
import com.vincentmet.customquests.tileentity.DeliveryBlockTileEntity;
import java.util.*;
import java.util.function.IntSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.*;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class DeliveryScreen extends Screen{
	public static final int MENUS_BUTTON_WIDTH = 50;
	public static final int MENUS_BUTTON_HEIGHT = 20;
	public final IntSupplier SELECT_BUTTON_WIDTH = ()->((width>>2)-30-40);
	public static final int SELECT_BUTTON_HEIGHT = 20;
	public static final int CONTENT_MARGIN = 5;
	public static final int NEWLINE_MARGIN = 1;
	public static final int SLOT_TO_ITEM_OFFSET = 1;
	public static final int REWARDS_TOP_MARGIN = 15;
	public static final int SLOT_SIZE = 18;
	private static final ClientPlayerEntity clientPlayer = Minecraft.getInstance().player;
	
	private static final ITextComponent TRANSLATION_SELECT_SUBTASK = new TranslationTextComponent(Ref.MODID + ".standardcontent.delivery.subtask_select");
	private static final ITextComponent TRANSLATION_ITEMS_TO_HAND_IN = new TranslationTextComponent(Ref.MODID + ".standardcontent.tasks.item_submit");
	private static final ITextComponent TRANSLATION_QUEST = new TranslationTextComponent(Ref.MODID + ".general.quest");
	private static final ITextComponent TRANSLATION_TASK = new TranslationTextComponent(Ref.MODID + ".general.task");
	private static final ITextComponent TRANSLATION_SUBTASK = new TranslationTextComponent(Ref.MODID + ".general.subtask");
	
	private final BlockPos tilePos;
	
	private final IntSupplier questListX = ()->20;
	private final IntSupplier questListY = ()->20;
	private final IntSupplier questListWidth = ()->(width>>2)-30;
	private final IntSupplier questListHeight = ()->height-40;
	
	private final IntSupplier taskListX = ()->(width>>2)+10;
	private final IntSupplier taskListY = ()->20;
	private final IntSupplier taskListWidth = ()->(width>>2)-20;
	private final IntSupplier taskListHeight = ()->(height>>1)-30;
	
	private final IntSupplier subtaskListX = ()->(width>>2)+10;
	private final IntSupplier subtaskListY = ()->(height>>1)+10;
	private final IntSupplier subtaskListWidth = ()->(width>>2)-20;
	private final IntSupplier subtaskListHeight = ()->(height>>1)-30;
	
	private final IntSupplier contentAreaX = ()->(width>>1)+10;
	private final IntSupplier contentAreaY = ()->20;
	private final IntSupplier contentAreaWidth = ()->(width>>1)-30;
	private final IntSupplier contentAreaHeight = ()->height-40;
	
	private ScrollableList questsList;
	private ScrollableList taskList;
	private ScrollableList subtaskList;
	
	private final Triple<Integer, Integer, Integer> currentSubtask = new Triple<>(-1, -1, -1);
	
	private VariableButton selectButton = new VariableButton(0, 80, 0, 20, VariableButton.ButtonTexture.DEFAULT_NORMAL, TRANSLATION_SELECT_SUBTASK.getString(), new Vec2i(0, 0), (mouseButton) -> {}, new ArrayList<>());
	private VariableSlot selectSlot = new VariableSlot(0, 55, SLOT_SIZE, SLOT_SIZE, VariableSlot.SlotTexture.DEFAULT, (mouseButton)->{}, new ArrayList<>());
	
	
	public DeliveryScreen(BlockPos pos){
		super(new TranslationTextComponent("block.customquests.questing_block"));
		tilePos = pos;
	}
	
	@Override
	protected void init(){
		questsList = new ScrollableList(questListX.getAsInt(), questListY.getAsInt(), questListWidth.getAsInt(), questListHeight.getAsInt());
		taskList = new ScrollableList(taskListX.getAsInt(), taskListY.getAsInt(), taskListWidth.getAsInt(), taskListHeight.getAsInt());
		subtaskList = new ScrollableList(subtaskListX.getAsInt(), subtaskListY.getAsInt(), subtaskListWidth.getAsInt(), subtaskListHeight.getAsInt());
		reInit();
	}
	
	private void reInit(){
		questsList.clear();
		currentSubtask.setL(-1);
		currentSubtask.setM(-1);
		currentSubtask.setR(-1);
		
		IntCounter cumulativeHeight = new IntCounter(20, MENUS_BUTTON_HEIGHT);
		QuestingStorage.getSidedQuestsMap().entrySet()
		                                 .stream()
		                                 .filter(entry->entry.getValue().hasChapter())
		                                 .filter(entry -> entry.getValue().getTasks().entrySet().stream().anyMatch(entry1 -> entry1.getValue().getTaskType().toString().equals(new ResourceLocation(Ref.MODID, "item_submit").toString())))
		                                 .filter(entry -> CombinedProgressHelper.isQuestUnlocked(clientPlayer.getUniqueID(), entry.getKey()) && !CombinedProgressHelper.isQuestCompleted(clientPlayer.getUniqueID(), entry.getKey()))
		                                 .forEach(entry -> {
			                                 List<ITextComponent> tooltips = new ArrayList<>();
			                                 tooltips.add(new StringTextComponent(entry.getValue().getTitle() + " #" + entry.getKey()));
			                                 tooltips.add(new StringTextComponent(entry.getValue().getSubtitle().toString()));
			                                 questsList.add(new TextButton(questListX.getAsInt(), cumulativeHeight.getValue(), (width>>2) - 30, MENUS_BUTTON_HEIGHT, entry.getValue().getTitle().toString(), ButtonState.NORMAL, (mouseButton) -> {
				                                 currentSubtask.setL(entry.getKey());
			                                 }, tooltips));
			                                 cumulativeHeight.count();
		                                 });
		reInitTasks();
	}
	
	private void reInitTasks(){
		taskList.clear();
		currentSubtask.setM(-1);
		currentSubtask.setR(-1);
		if(currentSubtask.getLeft()>=0){
			IntCounter cumulativeHeight = new IntCounter(20, MENUS_BUTTON_HEIGHT);
			QuestingStorage.getSidedQuestsMap().get(currentSubtask.getLeft()).getTasks().entrySet()
			               .stream()
			               .filter(entry->QuestHelper.getQuestFromId(currentSubtask.getLeft()).hasChapter())
			               .filter(entry -> !CombinedProgressHelper.isTaskCompleted(clientPlayer.getUniqueID(), currentSubtask.getLeft(), entry.getKey()))
			               .filter(entry -> entry.getValue().getTaskType().toString().equals(new ResourceLocation(Ref.MODID, "item_submit").toString()))
					       .forEach(entry->{
						       taskList.add(new TextButton(taskListX.getAsInt(), cumulativeHeight.getValue(), (width>>2) - 20, MENUS_BUTTON_HEIGHT, entry.getKey().toString(), ButtonState.NORMAL, (mouseButton) -> {
							       currentSubtask.setM(entry.getKey());
					           }, new ArrayList<>()));
						       cumulativeHeight.count();
					       });
		}
		reInitSubtaskItems();
	}
	
	private void reInitSubtaskItems(){
		subtaskList.clear();
		currentSubtask.setR(-1);
		if(currentSubtask.getLeft()>=0 && currentSubtask.getMiddle()>=0){
			IntCounter cumulativeHeight = new IntCounter(0, MENUS_BUTTON_HEIGHT);
			QuestingStorage.getSidedQuestsMap().get(currentSubtask.getLeft()).getTasks().get(currentSubtask.getMiddle()).getSubtasks().entrySet()
			               .stream()
			               .filter(entry->!CombinedProgressHelper.isSubtaskCompleted(clientPlayer.getUniqueID(), currentSubtask.getLeft(), currentSubtask.getMiddle(), entry.getKey()))
			               .forEach(entry -> {
			                   subtaskList.add(new TextButton(subtaskListX.getAsInt(), subtaskListY.getAsInt() + cumulativeHeight.getValue(), (width>>2) - 20, MENUS_BUTTON_HEIGHT, entry.getValue().getSubtask().getText(clientPlayer), ButtonState.NORMAL, (mouseButton1)->{
				                   currentSubtask.setR(entry.getKey());
			                   	   selectButton.setOnClickCallback((mouseButton) -> {
				                       TileEntity te = clientPlayer.world.getTileEntity(tilePos);
			                   	       if(te instanceof DeliveryBlockTileEntity){
			                   	       	   DeliveryBlockTileEntity dbte = ((DeliveryBlockTileEntity)te);
			                   	       	   if(dbte.getItemHandler() != null){
				                               dbte.getItemHandler().setActiveSubtask(currentSubtask.getLeft(), currentSubtask.getMiddle(), currentSubtask.getRight());
				                               PacketHandler.CHANNEL.sendToServer(new MessageUpdateDelivery(tilePos, currentSubtask.getLeft(), currentSubtask.getMiddle(), currentSubtask.getRight()));
			                               }
			                   	       }
			                       });
			                   }, new ArrayList<>()));
				               cumulativeHeight.count();
			               });
		}
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
		TooltipBuffer.tooltipBuffer.clear();
		renderBackgrounds(matrixStack);
		font.drawStringWithShadow(matrixStack, TRANSLATION_QUEST.getString() + ":", questListX.getAsInt(), questListY.getAsInt() - font.FONT_HEIGHT, 0xFFFFFF);
		questsList.render(matrixStack, mouseX, mouseY, partialTicks);
		font.drawStringWithShadow(matrixStack, TRANSLATION_TASK.getString() + ":", (Minecraft.getInstance().getMainWindow().getScaledWidth()>>2)+10, 20 - font.FONT_HEIGHT, 0xFFFFFF);
		taskList.render(matrixStack, mouseX, mouseY, partialTicks);
		
		font.drawStringWithShadow(matrixStack, TRANSLATION_SUBTASK.getString() + ":", (width>>2)+10, height / 2 + 10 - font.FONT_HEIGHT, 0xFFFFFF);
		subtaskList.render(matrixStack, mouseX, mouseY, partialTicks);
		
		if(currentSubtask.getLeft()>=0 && currentSubtask.getMiddle()>=0 && currentSubtask.getRight()>=0){
			selectButton.render(matrixStack, mouseX, mouseY, partialTicks);
			String title = QuestingStorage.getSidedQuestsMap().get(currentSubtask.getLeft()) != null ? QuestingStorage.getSidedQuestsMap().get(currentSubtask.getLeft()).getTitle().toString() : "";
			font.drawStringWithShadow(matrixStack, title, (int)(width * 0.75) - (font.getStringWidth(title) >> 1), 30, 0xFFFFFF);
			font.drawStringWithShadow(matrixStack, TRANSLATION_ITEMS_TO_HAND_IN.getString(), (width >> 1) + 25, 40, 0xFFFFFF);
			selectSlot.render(matrixStack, mouseX, mouseY, partialTicks);
			
			boolean isCompleted = QuestingStorage.getSidedPlayersMap().get(clientPlayer.getUniqueID().toString()).getIndividualProgress().get(currentSubtask.getLeft()).get(currentSubtask.getMiddle()).get(currentSubtask.getRight()).getValue() == QuestingStorage.getSidedQuestsMap().get(currentSubtask.getLeft()).getTasks().get(currentSubtask.getMiddle()).getSubtasks().get(currentSubtask.getRight()).getSubtask().getCompletionAmount();
			String checkmarkText = TextFormatting.DARK_GRAY + " [" + QuestingStorage.getSidedPlayersMap().get(clientPlayer.getUniqueID().toString()).getIndividualProgress().get(currentSubtask.getLeft()).get(currentSubtask.getMiddle()).get(currentSubtask.getRight()).getValue() + "/" + QuestingStorage.getSidedQuestsMap().get(currentSubtask.getLeft()).getTasks().get(currentSubtask.getMiddle()).getSubtasks().get(currentSubtask.getRight()).getSubtask().getCompletionAmount() + "]";
			if(isCompleted)
				checkmarkText = TextFormatting.GREEN + " \u2713";
			String subtaskText = QuestingStorage.getSidedQuestsMap().get(currentSubtask.getLeft()).getTasks().get(currentSubtask.getMiddle()).getSubtasks().get(currentSubtask.getRight()).getSubtask().getText(clientPlayer) + checkmarkText;
			if(Minecraft.getInstance().fontRenderer.getStringWidth(subtaskText) + 5 >= (Minecraft.getInstance().getMainWindow().getScaledWidth()>>1) - 90){
				subtaskText = Minecraft.getInstance().fontRenderer.trimStringToWidth(new StringTextComponent(subtaskText), (Minecraft.getInstance().getMainWindow().getScaledWidth() >> 1) - 90) + "...";
			}
			Minecraft.getInstance().fontRenderer.drawStringWithShadow(matrixStack, subtaskText, 27 + (Minecraft.getInstance().getMainWindow().getScaledWidth() >> 1) + 25, 55 + 10 - Minecraft.getInstance().fontRenderer.FONT_HEIGHT / 2, 0xFFFFFF);
			QuestingStorage.getSidedQuestsMap().get(currentSubtask.getLeft()).getTasks().get(currentSubtask.getMiddle()).getSubtasks().get(currentSubtask.getRight()).getSubtask().getIcon(clientPlayer).render(matrixStack, 1, (Minecraft.getInstance().getMainWindow().getScaledWidth() >> 1) + 25 + 1, 55 + 1, mouseX, mouseY);
		}
		TooltipBuffer.tooltipBuffer.forEach(Runnable::run);
	}
	
	private void renderBackgrounds(MatrixStack matrixStack){
		AbstractGui.fill(matrixStack, 0, 0, width, height, 0x88000000);
		AbstractGui.fill(matrixStack, questListX.getAsInt(), questListY.getAsInt(), questListX.getAsInt() + questListWidth.getAsInt(), questListY.getAsInt() + questListHeight.getAsInt(), 0x88000000);
		AbstractGui.fill(matrixStack, taskListX.getAsInt(), taskListY.getAsInt(), taskListX.getAsInt() + taskListWidth.getAsInt(), taskListY.getAsInt() + taskListHeight.getAsInt(), 0x88000000);
		AbstractGui.fill(matrixStack, subtaskListX.getAsInt(), subtaskListY.getAsInt(), subtaskListX.getAsInt() + subtaskListWidth.getAsInt(), subtaskListY.getAsInt() + subtaskListHeight.getAsInt(), 0x88000000);
		AbstractGui.fill(matrixStack, contentAreaX.getAsInt(), contentAreaY.getAsInt(), contentAreaX.getAsInt() + contentAreaWidth.getAsInt(), contentAreaY.getAsInt() + contentAreaHeight.getAsInt(), 0x88000000);
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton){
		questsList.mouseClicked(mouseX, mouseY, mouseButton);
		taskList.mouseClicked(mouseX, mouseY, mouseButton);
		subtaskList.mouseClicked(mouseX, mouseY, mouseButton);
		selectButton.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
		return true;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double dyScroll){
		questsList.mouseScrolled(mouseX, mouseY, dyScroll);
		taskList.mouseScrolled(mouseX, mouseY, dyScroll);
		subtaskList.mouseScrolled(mouseX, mouseY, dyScroll);
		super.mouseScrolled(mouseX, mouseY, dyScroll);
		return true;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy){
		questsList.mouseDragged(mouseX, mouseY, button, dx, dy);
		taskList.mouseDragged(mouseX, mouseY, button, dx, dy);
		subtaskList.mouseDragged(mouseX, mouseY, button, dx, dy);
		super.mouseDragged(mouseX, mouseY, button, dx, dy);
		return true;
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int mods){
		if(keyCode == GLFW.GLFW_KEY_ESCAPE){
			this.onClose();
			return true;
		}
		questsList.keyPressed(keyCode, scanCode, mods);
		taskList.keyPressed(keyCode, scanCode, mods);
		subtaskList.keyPressed(keyCode, scanCode, mods);
		super.keyPressed(keyCode, scanCode, mods);
		return true;
	}
	
	@Override
	public void mouseMoved(double mouseX, double mouseY){
		questsList.mouseMoved(mouseX, mouseY);
		taskList.mouseMoved(mouseX, mouseY);
		subtaskList.mouseMoved(mouseX, mouseY);
		super.mouseMoved(mouseX, mouseY);
	}
	
	@Override
	public boolean isPauseScreen(){
		return false;
	}
}