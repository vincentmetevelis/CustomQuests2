package com.vincentmet.customquests.gui.editor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vincentmet.customquests.gui.elements.ScrollableList;
import com.vincentmet.customquests.helpers.TooltipBuffer;
import com.vincentmet.customquests.helpers.math.Vec2i;
import com.vincentmet.customquests.helpers.rendering.VariableButton;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.function.IntSupplier;

public class QuestSelector extends Screen {
    public final ActionQueue actionQueue = new ActionQueue();
    private static final IntSupplier BORDER_MARGIN = ()->20;

    private final IntSupplier FILTER_BOX_X = BORDER_MARGIN;
    private final IntSupplier FILTER_BOX_Y = BORDER_MARGIN;
    private final IntSupplier FILTER_BOX_WIDTH = ()->(int)((width>>2) - 1.5*BORDER_MARGIN.getAsInt());
    private final IntSupplier FILTER_BOX_HEIGHT = ()->height - 2*BORDER_MARGIN.getAsInt();

    private final IntSupplier DISABLED_BOX_X = ()->FILTER_BOX_X.getAsInt() + FILTER_BOX_WIDTH.getAsInt() + BORDER_MARGIN.getAsInt();
    private final IntSupplier DISABLED_BOX_Y = BORDER_MARGIN;
    private final IntSupplier DISABLED_BOX_WIDTH = ()->((width>>3) * 3 - BORDER_MARGIN.getAsInt());
    private final IntSupplier DISABLED_BOX_HEIGHT = ()->height - 2*BORDER_MARGIN.getAsInt();

    private final IntSupplier ENABLED_BOX_X = ()->DISABLED_BOX_X.getAsInt() + DISABLED_BOX_WIDTH.getAsInt() + BORDER_MARGIN.getAsInt();
    private final IntSupplier ENABLED_BOX_Y = BORDER_MARGIN;
    private final IntSupplier ENABLED_BOX_WIDTH = ()->(int)((width>>3) * 3 - 1.5*BORDER_MARGIN.getAsInt());
    private final IntSupplier ENABLED_BOX_HEIGHT = ()->height - 2*BORDER_MARGIN.getAsInt();

    private final IntSupplier ENABLE_BUTTON_X = ()->DISABLED_BOX_X.getAsInt() + DISABLED_BOX_WIDTH.getAsInt();
    private final IntSupplier ENABLE_BUTTON_Y = DISABLED_BOX_Y;
    private final IntSupplier ENABLE_BUTTON_WIDTH = BORDER_MARGIN;
    private final IntSupplier ENABLE_BUTTON_HEIGHT = BORDER_MARGIN;

    private final IntSupplier DISABLE_BUTTON_X = ENABLE_BUTTON_X;
    private final IntSupplier DISABLE_BUTTON_Y = ()->ENABLE_BUTTON_Y.getAsInt() + BORDER_MARGIN.getAsInt();
    private final IntSupplier DISABLE_BUTTON_WIDTH = ENABLE_BUTTON_WIDTH;
    private final IntSupplier DISABLE_BUTTON_HEIGHT = ENABLE_BUTTON_HEIGHT;

    private VariableButton enableButton;
    private VariableButton disableButton;
    private VariableButton applyFiltersButton;
    private ScrollableList disabledList;
    private ScrollableList enabledList;

    public QuestSelector() {
        super(new TextComponent("title :3"));
        reInit();
    }

    public void reInit(){
        enableButton = new VariableButton(ENABLE_BUTTON_X, ENABLE_BUTTON_Y, ENABLE_BUTTON_WIDTH, ENABLE_BUTTON_HEIGHT, VariableButton.ButtonTexture.DEFAULT_NORMAL, ">", new Vec2i(), mouseButton -> {}, new ArrayList<>());
        disableButton = new VariableButton(DISABLE_BUTTON_X, DISABLE_BUTTON_Y, DISABLE_BUTTON_WIDTH, DISABLE_BUTTON_HEIGHT, VariableButton.ButtonTexture.DEFAULT_NORMAL, "<", new Vec2i(), mouseButton -> {}, new ArrayList<>());
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        drawBackgrounds(stack);
        enableButton.render(stack, mouseX, mouseY, partialTicks);
        disableButton.render(stack, mouseX, mouseY, partialTicks);

        renderHover(stack, mouseX, mouseY, partialTicks);
    }

    public void renderHover(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        enableButton.renderHover(stack, mouseX, mouseY, partialTicks);
        disableButton.renderHover(stack, mouseX, mouseY, partialTicks);
    }

    private void drawBackgrounds(PoseStack stack){
        TooltipBuffer.tooltipBuffer.clear();
        GuiComponent.fill(stack, 0, 0, width, height, 0x88000000);
        GuiComponent.fill(stack, FILTER_BOX_X.getAsInt(), FILTER_BOX_Y.getAsInt(), FILTER_BOX_X.getAsInt() + FILTER_BOX_WIDTH.getAsInt(), FILTER_BOX_Y.getAsInt() + FILTER_BOX_HEIGHT.getAsInt(), 0x88000000);
        GuiComponent.fill(stack, DISABLED_BOX_X.getAsInt(), DISABLED_BOX_Y.getAsInt(), DISABLED_BOX_X.getAsInt() + DISABLED_BOX_WIDTH.getAsInt(), DISABLED_BOX_Y.getAsInt() + DISABLED_BOX_HEIGHT.getAsInt(), 0x88000000);
        GuiComponent.fill(stack, ENABLED_BOX_X.getAsInt(), ENABLED_BOX_Y.getAsInt(), ENABLED_BOX_X.getAsInt() + ENABLED_BOX_WIDTH.getAsInt(), ENABLED_BOX_Y.getAsInt() + ENABLED_BOX_HEIGHT.getAsInt(), 0x88000000);

        TooltipBuffer.tooltipBuffer.forEach(Runnable::run);
        actionQueue.execute();
    }

    @Override
    public boolean isPauseScreen(){
        return false;
    }
}