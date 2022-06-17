package com.vincentmet.customquests.helpers;

public interface CQGuiEventListener {
    default void mouseMoved(double dx, double dy){}

    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    default boolean mouseDragged(double p_94740_, double p_94741_, int p_94742_, double p_94743_, double p_94744_) {
        return false;
    }

    default boolean mouseScrolled(double p_94734_, double p_94735_, double p_94736_) {
        return false;
    }

    default boolean keyPressed(int p_94745_, int p_94746_, int p_94747_) {
        return false;
    }

    default boolean keyReleased(int p_94750_, int p_94751_, int p_94752_) {
        return false;
    }

    default boolean charTyped(char p_94732_, int p_94733_) {
        return false;
    }

    default boolean changeFocus(boolean p_94756_) {
        return false;
    }

    default boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }
}