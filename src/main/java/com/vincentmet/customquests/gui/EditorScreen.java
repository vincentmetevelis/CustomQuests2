package com.vincentmet.customquests.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.QuestingStorage;
import com.vincentmet.customquests.gui.editor.*;
import com.vincentmet.customquests.gui.elements.ScrollableList;
import com.vincentmet.customquests.helpers.IntCounter;
import com.vincentmet.customquests.helpers.TooltipBuffer;
import com.vincentmet.customquests.helpers.math.Vec2i;
import com.vincentmet.customquests.helpers.rendering.VariableButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.function.IntSupplier;

@OnlyIn(Dist.CLIENT)
public class EditorScreen extends Screen {
    private final EditorScreenManager screenManager = new EditorScreenManager();
    public final ActionQueue actionQueue = new ActionQueue();
    
    private static final IntSupplier BORDER_MARGIN = ()->20;
    private static final IntSupplier SELECTOR_BUTTON_HEIGHT = ()->20;
    
    private final IntSupplier SELECTION_BUTTONS_X = ()->BORDER_MARGIN.getAsInt();
    private final IntSupplier SELECTION_BUTTONS_Y = ()->BORDER_MARGIN.getAsInt();
    private final IntSupplier SELECTION_BUTTONS_WIDTH = ()->(int)((width>>2) - 1.5*BORDER_MARGIN.getAsInt());
    private final IntSupplier SELECTION_BUTTONS_HEIGHT = ()->height - 2*BORDER_MARGIN.getAsInt();
    
    private final IntSupplier EDITOR_AREA_X = ()->(int)((width>>2) + 0.5*BORDER_MARGIN.getAsInt());
    private final IntSupplier EDITOR_AREA_Y = ()->BORDER_MARGIN.getAsInt();
    private final IntSupplier EDITOR_AREA_WIDTH = ()->(int)((width>>2)*3 - 1.5*BORDER_MARGIN.getAsInt());
    private final IntSupplier EDITOR_AREA_HEIGHT = ()->height - 2*BORDER_MARGIN.getAsInt();
    
    private ScrollableList selectorList;
    private EditorPropertiesSubScreen propertiesSubScreen;
    
    private final VariableButton parentButton = new VariableButton(()->0, BORDER_MARGIN, ()->18, ()->20, VariableButton.ButtonTexture.DEFAULT_NORMAL, "\u2190", new Vec2i(0, 0), mouseButton -> {
        screenManager.setToParent();
        actionQueue.push(this::reInit);
        actionQueue.push(() -> selectorList.setScrollDistance(0));
    }, new ArrayList<>());
    private final VariableButton chaptersButton = new VariableButton(()->0, ()->2*BORDER_MARGIN.getAsInt(), ()->18, ()->20, VariableButton.ButtonTexture.DEFAULT_NORMAL, "C", new Vec2i(), mouseButton -> {
        screenManager.set(MenuSelection.CHAPTERS);
        actionQueue.push(this::reInit);
        actionQueue.push(() -> selectorList.setScrollDistance(0));
    }, new ArrayList<>());
    private final VariableButton questsButton = new VariableButton(()->0, ()->3*BORDER_MARGIN.getAsInt(), ()->18, ()->20, VariableButton.ButtonTexture.DEFAULT_NORMAL, "Q", new Vec2i(), mouseButton -> {
        screenManager.set(MenuSelection.QUESTS);
        actionQueue.push(this::reInit);
        actionQueue.push(() -> selectorList.setScrollDistance(0));
    }, new ArrayList<>());
    
    private final VariableButton addChapterButton = new VariableButton(BORDER_MARGIN, ()->0, ()->20, ()->18, VariableButton.ButtonTexture.DEFAULT_NORMAL, "+", new Vec2i(), mouseButton -> ClientUtils.sendEditorCreateChapter(), new ArrayList<>());
    private final VariableButton addQuestButton = new VariableButton(BORDER_MARGIN, ()->0, ()->20, ()->18, VariableButton.ButtonTexture.DEFAULT_NORMAL, "+", new Vec2i(), mouseButton -> ClientUtils.sendEditorCreateQuest(), new ArrayList<>());
    //todo quest, task subtask, reward and subreward button (+logic)
    public EditorScreen(){
        super(new TranslatableComponent(Ref.MODID + ".screens.editor"));
    }

    @Override
    public void init(){
        selectorList = new ScrollableList(SELECTION_BUTTONS_X, SELECTION_BUTTONS_Y, SELECTION_BUTTONS_WIDTH, SELECTION_BUTTONS_HEIGHT);
        propertiesSubScreen = new EditorPropertiesSubScreen(this, screenManager, EDITOR_AREA_X, EDITOR_AREA_Y, EDITOR_AREA_WIDTH, EDITOR_AREA_HEIGHT);
        reInit();
    }
    
    public void reInit(){
        reInitSelectorList();
        propertiesSubScreen.reInit();
        Minecraft.getInstance().setScreen(new QuestSelector());//todo remove once done testing
    }
    
    public void reInitSelectorList(){
        IntCounter cumulativeHeight = new IntCounter(SELECTION_BUTTONS_Y.getAsInt(), SELECTOR_BUTTON_HEIGHT.getAsInt());
        selectorList.clear();
        switch(screenManager.getSelection()){
            case CHAPTERS:
                QuestingStorage.getSidedChaptersMap().values().forEach(chapter -> {
                    selectorList.add(new EditorChapterButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, chapter, mouseButton -> {
                        screenManager.setSelectedChapterId(chapter.getId());
                        screenManager.set(MenuSelection.CHAPTER);
                        actionQueue.push(this::reInit);
                        actionQueue.push(() -> selectorList.setScrollDistance(0));
                    }));
                    cumulativeHeight.count();
                });
                break;
            case CHAPTER:
                selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, "Title", mouseButton -> {
                    screenManager.set(MenuSelection.CHAPTER_TITLE);
                    actionQueue.push(this::reInit);
                    actionQueue.push(() -> selectorList.setScrollDistance(0));
                }));
                cumulativeHeight.count();
                selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, "Text", mouseButton -> {
                    screenManager.set(MenuSelection.CHAPTER_TEXT);
                    actionQueue.push(this::reInit);
                    actionQueue.push(() -> selectorList.setScrollDistance(0));
                }));
                cumulativeHeight.count();
                selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, "Quests", mouseButton -> {
                    screenManager.set(MenuSelection.CHAPTER_QUESTLIST);
                    actionQueue.push(this::reInit);
                    actionQueue.push(() -> selectorList.setScrollDistance(0));
                }));
                cumulativeHeight.count();
                break;
            case QUESTS:
                QuestingStorage.getSidedQuestsMap().values().forEach(quest -> {
                    selectorList.add(new EditorQuestButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, quest, mouseButton -> {
                        screenManager.setSelectedQuestId(quest.getQuestId());
                        screenManager.set(MenuSelection.QUEST);
                        actionQueue.push(this::reInit);
                        actionQueue.push(() -> selectorList.setScrollDistance(0));
                    }));
                    cumulativeHeight.count();
                });
                break;
            case QUEST:
                selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, "Button", mouseButton -> {
                    screenManager.set(MenuSelection.QUEST_BUTTON);
                    actionQueue.push(this::reInit);
                    actionQueue.push(() -> selectorList.setScrollDistance(0));
                }));
                cumulativeHeight.count();
                selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, "Title", mouseButton -> {
                    screenManager.set(MenuSelection.QUEST_TITLE);
                    actionQueue.push(this::reInit);
                    actionQueue.push(() -> selectorList.setScrollDistance(0));
                }));
                cumulativeHeight.count();
                selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, "Subtitle", mouseButton -> {
                    screenManager.set(MenuSelection.QUEST_SUBTITLE);
                    actionQueue.push(this::reInit);
                    actionQueue.push(() -> selectorList.setScrollDistance(0));
                }));
                cumulativeHeight.count();
                selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, "Text", mouseButton -> {
                    screenManager.set(MenuSelection.QUEST_TEXT);
                    actionQueue.push(this::reInit);
                    actionQueue.push(() -> selectorList.setScrollDistance(0));
                }));
                cumulativeHeight.count();
                selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, "Dependencies", mouseButton -> {
                    screenManager.set(MenuSelection.QUEST_DEPENDENCIES);
                    actionQueue.push(this::reInit);
                    actionQueue.push(() -> selectorList.setScrollDistance(0));
                }));
                cumulativeHeight.count();
                selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, "Tasks", mouseButton -> {
                    screenManager.set(MenuSelection.QUEST_TASKS);
                    actionQueue.push(this::reInit);
                    actionQueue.push(() -> selectorList.setScrollDistance(0));
                }));
                cumulativeHeight.count();
                selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, "Rewards", mouseButton -> {
                    screenManager.set(MenuSelection.QUEST_REWARDS);
                    actionQueue.push(this::reInit);
                    actionQueue.push(() -> selectorList.setScrollDistance(0));
                }));
                cumulativeHeight.count();
                selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, "Position", mouseButton -> {
                    screenManager.set(MenuSelection.QUEST_POSITION);
                    actionQueue.push(this::reInit);
                    actionQueue.push(() -> selectorList.setScrollDistance(0));
                }));
                cumulativeHeight.count();
                break;
            case QUEST_DEPENDENCIES:
                selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, "Quests", mouseButton -> {
                    screenManager.set(MenuSelection.QUEST_DEPENDENCIES_LIST);
                    actionQueue.push(this::reInit);
                    actionQueue.push(() -> selectorList.setScrollDistance(0));
                }));
                cumulativeHeight.count();
                break;
            case QUEST_TASKS:
                QuestingStorage.getSidedQuestsMap().get(screenManager.getSelectedQuestId()).getTasks().values().forEach(task -> {
                    selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, String.valueOf(task.getId()), mouseButton -> {
                        screenManager.setSelectedTaskId(task.getId());
                        screenManager.set(MenuSelection.QUEST_TASK);
                        actionQueue.push(this::reInit);
                        actionQueue.push(() -> selectorList.setScrollDistance(0));
                    }));
                    cumulativeHeight.count();
                });
                break;
            case QUEST_TASK:
                selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, "Subtasks", mouseButton -> {
                    screenManager.set(MenuSelection.QUEST_TASK_SUBTASKS);
                    actionQueue.push(this::reInit);
                    actionQueue.push(() -> selectorList.setScrollDistance(0));
                }));
                cumulativeHeight.count();
                break;
            case QUEST_TASK_SUBTASKS:
                QuestingStorage.getSidedQuestsMap().get(screenManager.getSelectedQuestId()).getTasks().get(screenManager.getSelectedTaskId()).getSubtasks().values().forEach(subtask -> {
                    selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, String.valueOf(subtask.getSubtaskId()), mouseButton -> {
                        screenManager.setSelectedSubtaskId(subtask.getSubtaskId());
                        screenManager.set(MenuSelection.QUEST_TASK_SUBTASK);
                        actionQueue.push(this::reInit);
                        actionQueue.push(() -> selectorList.setScrollDistance(0));
                    }));
                    cumulativeHeight.count();
                });
                break;
            case QUEST_REWARDS:
                QuestingStorage.getSidedQuestsMap().get(screenManager.getSelectedQuestId()).getRewards().values().forEach(reward -> {
                    selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, String.valueOf(reward.getRewardId()), mouseButton -> {
                        screenManager.setSelectedRewardId(reward.getRewardId());
                        screenManager.set(MenuSelection.QUEST_REWARD);
                        actionQueue.push(this::reInit);
                        actionQueue.push(() -> selectorList.setScrollDistance(0));
                    }));
                    cumulativeHeight.count();
                });
                break;
            case QUEST_REWARD:
                selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, "Subrewards", mouseButton -> {
                    screenManager.set(MenuSelection.QUEST_REWARD_SUBREWARDS);
                    actionQueue.push(this::reInit);
                    actionQueue.push(() -> selectorList.setScrollDistance(0));
                }));
                cumulativeHeight.count();
                break;
            case QUEST_REWARD_SUBREWARDS:
                QuestingStorage.getSidedQuestsMap().get(screenManager.getSelectedQuestId()).getRewards().get(screenManager.getSelectedRewardId()).getSubRewards().values().forEach(subreward -> {
                    selectorList.add(new EditorBlancButton(SELECTION_BUTTONS_X, ()->cumulativeHeight.getValue(), SELECTION_BUTTONS_WIDTH, String.valueOf(subreward.getSubRewardId()), mouseButton -> {
                        screenManager.setSelectedSubrewardId(subreward.getSubRewardId());
                        screenManager.set(MenuSelection.QUEST_REWARD_SUBREWARD);
                        actionQueue.push(this::reInit);
                        actionQueue.push(() -> selectorList.setScrollDistance(0));
                    }));
                    cumulativeHeight.count();
                });
                break;
            default:
                //NOOP
                break;
        }
    }
    
    public void resetSelectorScroll(){
        selectorList.setScrollDistance(0);
    }
    
    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        TooltipBuffer.tooltipBuffer.clear();
        GuiComponent.fill(matrixStack, 0, 0, width, height, 0x88000000);
        GuiComponent.fill(matrixStack, SELECTION_BUTTONS_X.getAsInt(), SELECTION_BUTTONS_Y.getAsInt(), SELECTION_BUTTONS_X.getAsInt() + SELECTION_BUTTONS_WIDTH.getAsInt(), SELECTION_BUTTONS_Y.getAsInt() + SELECTION_BUTTONS_HEIGHT.getAsInt(), 0x88000000);
        GuiComponent.fill(matrixStack, EDITOR_AREA_X.getAsInt(), EDITOR_AREA_Y.getAsInt(), EDITOR_AREA_X.getAsInt() + EDITOR_AREA_WIDTH.getAsInt(), EDITOR_AREA_Y.getAsInt() + EDITOR_AREA_HEIGHT.getAsInt(), 0x88000000);
        
        //Non-hover
        if(selectorList.getWidth() != SELECTION_BUTTONS_WIDTH){
            selectorList.setWidth(SELECTION_BUTTONS_WIDTH);
        }
        if(selectorList.getEntries().size() >= 1 && selectorList.getEntries().get(0).getY().getAsInt() != 20-selectorList.getScrollDistance()){
            IntCounter chapterListCounter = new IntCounter(SELECTION_BUTTONS_Y.getAsInt()-selectorList.getScrollDistance(), SELECTOR_BUTTON_HEIGHT.getAsInt());
            selectorList.getEntries().forEach(entry -> {
                entry.setY(()->chapterListCounter.getValue());
                chapterListCounter.count();
            });
        }
        parentButton.render(matrixStack, mouseX, mouseY, partialTicks);
        chaptersButton.render(matrixStack, mouseX, mouseY, partialTicks);
        questsButton.render(matrixStack, mouseX, mouseY, partialTicks);
        selectorList.render(matrixStack, mouseX, mouseY, partialTicks);
        propertiesSubScreen.render(matrixStack, mouseX, mouseY, partialTicks);
        if(screenManager.getSelection() == MenuSelection.CHAPTERS){
            addChapterButton.render(matrixStack, mouseX, mouseY, partialTicks);
        }else if(screenManager.getSelection() == MenuSelection.QUESTS){
            addQuestButton.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        
        //Hover
        parentButton.renderHover(matrixStack, mouseX, mouseY, partialTicks);
        chaptersButton.renderHover(matrixStack, mouseX, mouseY, partialTicks);
        questsButton.renderHover(matrixStack, mouseX, mouseY, partialTicks);
        selectorList.renderHover(matrixStack, mouseX, mouseY, partialTicks);
        propertiesSubScreen.renderHover(matrixStack, mouseX, mouseY, partialTicks);
        if(screenManager.getSelection() == MenuSelection.CHAPTERS){
            addChapterButton.renderHover(matrixStack, mouseX, mouseY, partialTicks);
        }else if(screenManager.getSelection() == MenuSelection.QUESTS){
            addQuestButton.renderHover(matrixStack, mouseX, mouseY, partialTicks);
        }
        TooltipBuffer.tooltipBuffer.forEach(Runnable::run);
        actionQueue.execute();
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int mods){
        if(keyCode == GLFW.GLFW_KEY_ESCAPE){
            if(Minecraft.getInstance().screen != null){
                Minecraft.getInstance().screen.onClose();
            }
        }
        selectorList.keyPressed(keyCode, scanCode, mods);
        parentButton.keyPressed(keyCode, scanCode, mods);
        chaptersButton.keyPressed(keyCode, scanCode, mods);
        questsButton.keyPressed(keyCode, scanCode, mods);
        propertiesSubScreen.keyPressed(keyCode, scanCode, mods);
        if(screenManager.getSelection() == MenuSelection.CHAPTERS){
            addChapterButton.keyPressed(keyCode, scanCode, mods);
        }else if(screenManager.getSelection() == MenuSelection.QUESTS){
            addQuestButton.keyPressed(keyCode, scanCode, mods);
        }
        return true;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button){
        selectorList.mouseClicked(mouseX, mouseY, button);
        parentButton.mouseClicked(mouseX, mouseY, button);
        chaptersButton.mouseClicked(mouseX, mouseY, button);
        questsButton.mouseClicked(mouseX, mouseY, button);
        propertiesSubScreen.mouseClicked(mouseX, mouseY, button);
        if(screenManager.getSelection() == MenuSelection.CHAPTERS){
            addChapterButton.mouseClicked(mouseX, mouseY, button);
        }else if(screenManager.getSelection() == MenuSelection.QUESTS){
            addQuestButton.mouseClicked(mouseX, mouseY, button);
        }
        return true;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dyScroll){
        selectorList.mouseScrolled(mouseX, mouseY, dyScroll);
        parentButton.mouseScrolled(mouseX, mouseY, dyScroll);
        chaptersButton.mouseScrolled(mouseX, mouseY, dyScroll);
        questsButton.mouseScrolled(mouseX, mouseY, dyScroll);
        propertiesSubScreen.mouseScrolled(mouseX, mouseY, dyScroll);
        if(screenManager.getSelection() == MenuSelection.CHAPTERS){
            addChapterButton.mouseScrolled(mouseX, mouseY, dyScroll);
        }else if(screenManager.getSelection() == MenuSelection.QUESTS){
            addQuestButton.mouseScrolled(mouseX, mouseY, dyScroll);
        }
        return true;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy){
        selectorList.mouseDragged(mouseX, mouseY, button, dx, dy);
        parentButton.mouseDragged(mouseX, mouseY, button, dx, dy);
        chaptersButton.mouseDragged(mouseX, mouseY, button, dx, dy);
        questsButton.mouseDragged(mouseX, mouseY, button, dx, dy);
        propertiesSubScreen.mouseDragged(mouseX, mouseY, button, dx, dy);
        if(screenManager.getSelection() == MenuSelection.CHAPTERS){
            addChapterButton.mouseDragged(mouseX, mouseY, button, dx, dy);
        }else if(screenManager.getSelection() == MenuSelection.QUESTS){
            addQuestButton.mouseDragged(mouseX, mouseY, button, dx, dy);
        }
        return true;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button){
        return propertiesSubScreen.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers){
        return propertiesSubScreen.keyReleased(keyCode, scanCode, modifiers);
    }
    
    @Override
    public boolean charTyped(char codePoint, int modifiers){
        return propertiesSubScreen.charTyped(codePoint, modifiers);
    }
    
    @Override
    public boolean changeFocus(boolean focus){
        return propertiesSubScreen.changeFocus(focus);
    }
    
    @Override
    public void mouseMoved(double xPos, double mouseY){
        super.mouseMoved(xPos, mouseY);
    }
    
    @Override
    public boolean isPauseScreen(){
        return false;
    }
}
