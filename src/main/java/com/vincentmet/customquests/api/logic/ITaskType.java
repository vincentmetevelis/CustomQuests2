package com.vincentmet.customquests.api.logic;

import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.helpers.MouseButton;
import com.vincentmet.customquests.hierarchy.quest.IQuestingTexture;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public interface ITaskType extends IJsonObjectProcessor, IJsonObjectProvider{//The mod registering should also handle the progress, to allow for "completing on event"
	ResourceLocation getId();
	ITextComponent getTranslation();
	boolean hasButton(ButtonContext context);//whether the completion checking happens on button-click or periodically; use the context(text i.e.) to set the button's properties
	void executeSubtaskCheck(PlayerEntity player, Object object);
	ITaskButtonClick executeSubtaskButton();
	void onLoad(UUID uuid, int questId, int taskId, int subtaskId); //todo end up not using this ugly onLoad func? maybe add something the the supplier/constructor/processJson or idk
	IQuestingTexture getIcon();
	String getText();
	int getCompletionAmount();
	Consumer<MouseButton> onSlotClick();
}