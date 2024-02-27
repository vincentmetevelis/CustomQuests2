package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.network.messages.button.MessageRewardClaim;
import com.vincentmet.customquests.network.messages.button.MessageTaskButton;
import com.vincentmet.customquests.network.messages.command.MessageDiscord;
import com.vincentmet.customquests.network.messages.command.MessageHand;
import com.vincentmet.customquests.network.messages.command.MessageOpenEditor;
import com.vincentmet.customquests.network.messages.editor.cts.requests.create.*;
import com.vincentmet.customquests.network.messages.editor.cts.requests.delete.*;
import com.vincentmet.customquests.network.messages.editor.cts.requests.update.chapter.*;
import com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest.*;
import com.vincentmet.customquests.network.messages.sync.*;
import com.vincentmet.customquests.network.messages.sync.stc.clear.*;
import com.vincentmet.customquests.network.messages.sync.stc.delete.*;
import com.vincentmet.customquests.network.messages.sync.stc.update.*;
import com.vincentmet.customquests.standardcontent.messages.MessageCheckboxClick;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler{
	private static int messageID = 0;
	private static final String PROTOCOL_VERSION = Integer.toString(1);
	
	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(Ref.MODID, "network"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();
	
	private static int nextID() {
		return messageID++;
	}
	
	public static void init() {//todo add A LOT A LOT A LOT of new packets here
		//Main
		//button
		CHANNEL.registerMessage(nextID(), MessageRewardClaim.class, MessageRewardClaim::encode, MessageRewardClaim::decode, MessageRewardClaim::handle);
		CHANNEL.registerMessage(nextID(), MessageTaskButton.class, MessageTaskButton::encode, MessageTaskButton::decode, MessageTaskButton::handle);
		//command
		CHANNEL.registerMessage(nextID(), MessageDiscord.class, MessageDiscord::encode, MessageDiscord::decode, MessageDiscord::handle);
		CHANNEL.registerMessage(nextID(), MessageHand.class, MessageHand::encode, MessageHand::decode, MessageHand::handle);
		CHANNEL.registerMessage(nextID(), MessageOpenEditor.class, MessageOpenEditor::encode, MessageOpenEditor::decode, MessageOpenEditor::handle);
		//editor
		//editor/cts
		//editor/cts/requests
		//editor/cts/requests/create
		CHANNEL.registerMessage(nextID(), MessageEditorRequestCreateChapter.class, MessageEditorRequestCreateChapter::encode, MessageEditorRequestCreateChapter::decode, MessageEditorRequestCreateChapter::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestCreateQuest.class, MessageEditorRequestCreateQuest::encode, MessageEditorRequestCreateQuest::decode, MessageEditorRequestCreateQuest::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestCreateReward.class, MessageEditorRequestCreateReward::encode, MessageEditorRequestCreateReward::decode, MessageEditorRequestCreateReward::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestCreateSubreward.class, MessageEditorRequestCreateSubreward::encode, MessageEditorRequestCreateSubreward::decode, MessageEditorRequestCreateSubreward::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestCreateSubtask.class, MessageEditorRequestCreateSubtask::encode, MessageEditorRequestCreateSubtask::decode, MessageEditorRequestCreateSubtask::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestCreateTask.class, MessageEditorRequestCreateTask::encode, MessageEditorRequestCreateTask::decode, MessageEditorRequestCreateTask::handle);
		//editor/cts/requests/delete
		CHANNEL.registerMessage(nextID(), MessageEditorRequestDeleteChapter.class, MessageEditorRequestDeleteChapter::encode, MessageEditorRequestDeleteChapter::decode, MessageEditorRequestDeleteChapter::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestDeleteQuest.class, MessageEditorRequestDeleteQuest::encode, MessageEditorRequestDeleteQuest::decode, MessageEditorRequestDeleteQuest::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestDeleteReward.class, MessageEditorRequestDeleteReward::encode, MessageEditorRequestDeleteReward::decode, MessageEditorRequestDeleteReward::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestDeleteSubreward.class, MessageEditorRequestDeleteSubreward::encode, MessageEditorRequestDeleteSubreward::decode, MessageEditorRequestDeleteSubreward::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestDeleteSubtask.class, MessageEditorRequestDeleteSubtask::encode, MessageEditorRequestDeleteSubtask::decode, MessageEditorRequestDeleteSubtask::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestDeleteTask.class, MessageEditorRequestDeleteTask::encode, MessageEditorRequestDeleteTask::decode, MessageEditorRequestDeleteTask::handle);
		//editor/cts/requests/update
		//editor/cts/requests/update/chapter
		CHANNEL.registerMessage(nextID(), MessageEditorRequestChapterQuestlistAddQuestId.class, MessageEditorRequestChapterQuestlistAddQuestId::encode, MessageEditorRequestChapterQuestlistAddQuestId::decode, MessageEditorRequestChapterQuestlistAddQuestId::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestChapterQuestlistRemoveQuestId.class, MessageEditorRequestChapterQuestlistRemoveQuestId::encode, MessageEditorRequestChapterQuestlistRemoveQuestId::decode, MessageEditorRequestChapterQuestlistRemoveQuestId::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateChapterIcon.class, MessageEditorRequestUpdateChapterIcon::encode, MessageEditorRequestUpdateChapterIcon::decode, MessageEditorRequestUpdateChapterIcon::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateChapterTextText.class, MessageEditorRequestUpdateChapterTextText::encode, MessageEditorRequestUpdateChapterTextText::decode, MessageEditorRequestUpdateChapterTextText::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateChapterTextType.class, MessageEditorRequestUpdateChapterTextType::encode, MessageEditorRequestUpdateChapterTextType::decode, MessageEditorRequestUpdateChapterTextType::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateChapterTitleText.class, MessageEditorRequestUpdateChapterTitleText::encode, MessageEditorRequestUpdateChapterTitleText::decode, MessageEditorRequestUpdateChapterTitleText::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateChapterTitleType.class, MessageEditorRequestUpdateChapterTitleType::encode, MessageEditorRequestUpdateChapterTitleType::decode, MessageEditorRequestUpdateChapterTitleType::handle);
		//editor/cts/requests/update/quest
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestButtonIcon.class, MessageEditorRequestUpdateQuestButtonIcon::encode, MessageEditorRequestUpdateQuestButtonIcon::decode, MessageEditorRequestUpdateQuestButtonIcon::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestButtonScale.class, MessageEditorRequestUpdateQuestButtonScale::encode, MessageEditorRequestUpdateQuestButtonScale::decode, MessageEditorRequestUpdateQuestButtonScale::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestButtonShape.class, MessageEditorRequestUpdateQuestButtonShape::encode, MessageEditorRequestUpdateQuestButtonShape::decode, MessageEditorRequestUpdateQuestButtonShape::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestDependenciesAddQuestId.class, MessageEditorRequestUpdateQuestDependenciesAddQuestId::encode, MessageEditorRequestUpdateQuestDependenciesAddQuestId::decode, MessageEditorRequestUpdateQuestDependenciesAddQuestId::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestDependenciesLogic.class, MessageEditorRequestUpdateQuestDependenciesLogic::encode, MessageEditorRequestUpdateQuestDependenciesLogic::decode, MessageEditorRequestUpdateQuestDependenciesLogic::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestDependenciesRemoveQuestId.class, MessageEditorRequestUpdateQuestDependenciesRemoveQuestId::encode, MessageEditorRequestUpdateQuestDependenciesRemoveQuestId::decode, MessageEditorRequestUpdateQuestDependenciesRemoveQuestId::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestPositionX.class, MessageEditorRequestUpdateQuestPositionX::encode, MessageEditorRequestUpdateQuestPositionX::decode, MessageEditorRequestUpdateQuestPositionX::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestPositionY.class, MessageEditorRequestUpdateQuestPositionY::encode, MessageEditorRequestUpdateQuestPositionY::decode, MessageEditorRequestUpdateQuestPositionY::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestRewardsLogic.class, MessageEditorRequestUpdateQuestRewardsLogic::encode, MessageEditorRequestUpdateQuestRewardsLogic::decode, MessageEditorRequestUpdateQuestRewardsLogic::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestSubrewardContent.class, MessageEditorRequestUpdateQuestSubrewardContent::encode, MessageEditorRequestUpdateQuestSubrewardContent::decode, MessageEditorRequestUpdateQuestSubrewardContent::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestSubrewardType.class, MessageEditorRequestUpdateQuestSubrewardType::encode, MessageEditorRequestUpdateQuestSubrewardType::decode, MessageEditorRequestUpdateQuestSubrewardType::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestSubtaskContent.class, MessageEditorRequestUpdateQuestSubtaskContent::encode, MessageEditorRequestUpdateQuestSubtaskContent::decode, MessageEditorRequestUpdateQuestSubtaskContent::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestSubtasksLogic.class, MessageEditorRequestUpdateQuestSubtasksLogic::encode, MessageEditorRequestUpdateQuestSubtasksLogic::decode, MessageEditorRequestUpdateQuestSubtasksLogic::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestSubtitleText.class, MessageEditorRequestUpdateQuestSubtitleText::encode, MessageEditorRequestUpdateQuestSubtitleText::decode, MessageEditorRequestUpdateQuestSubtitleText::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestSubtitleType.class, MessageEditorRequestUpdateQuestSubtitleType::encode, MessageEditorRequestUpdateQuestSubtitleType::decode, MessageEditorRequestUpdateQuestSubtitleType::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestTasksLogic.class, MessageEditorRequestUpdateQuestTasksLogic::encode, MessageEditorRequestUpdateQuestTasksLogic::decode, MessageEditorRequestUpdateQuestTasksLogic::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestTaskType.class, MessageEditorRequestUpdateQuestTaskType::encode, MessageEditorRequestUpdateQuestTaskType::decode, MessageEditorRequestUpdateQuestTaskType::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestTextText.class, MessageEditorRequestUpdateQuestTextText::encode, MessageEditorRequestUpdateQuestTextText::decode, MessageEditorRequestUpdateQuestTextText::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestTextType.class, MessageEditorRequestUpdateQuestTextType::encode, MessageEditorRequestUpdateQuestTextType::decode, MessageEditorRequestUpdateQuestTextType::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestTitleText.class, MessageEditorRequestUpdateQuestTitleText::encode, MessageEditorRequestUpdateQuestTitleText::decode, MessageEditorRequestUpdateQuestTitleText::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRequestUpdateQuestTitleType.class, MessageEditorRequestUpdateQuestTitleType::encode, MessageEditorRequestUpdateQuestTitleType::decode, MessageEditorRequestUpdateQuestTitleType::handle);
		//sync
		CHANNEL.registerMessage(nextID(), MessageUpdateDelivery.class, MessageUpdateDelivery::encode, MessageUpdateDelivery::decode, MessageUpdateDelivery::handle);
		CHANNEL.registerMessage(nextID(), MessageUpdateServerSettings.class, MessageUpdateServerSettings::encode, MessageUpdateServerSettings::decode, MessageUpdateServerSettings::handle);
		//sync/stc
		//sync/stc/clear
		CHANNEL.registerMessage(nextID(), MessageStcSyncTempClearAllChapters.class, MessageStcSyncTempClearAllChapters::encode, MessageStcSyncTempClearAllChapters::decode, MessageStcSyncTempClearAllChapters::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncTempClearAllParties.class, MessageStcSyncTempClearAllParties::encode, MessageStcSyncTempClearAllParties::decode, MessageStcSyncTempClearAllParties::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncTempClearAllPlayers.class, MessageStcSyncTempClearAllPlayers::encode, MessageStcSyncTempClearAllPlayers::decode, MessageStcSyncTempClearAllPlayers::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncTempClearAllQuests.class, MessageStcSyncTempClearAllQuests::encode, MessageStcSyncTempClearAllQuests::decode, MessageStcSyncTempClearAllQuests::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncTempClearSingleQuest.class, MessageStcSyncTempClearSingleQuest::encode, MessageStcSyncTempClearSingleQuest::decode, MessageStcSyncTempClearSingleQuest::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncTempClearSingleReward.class, MessageStcSyncTempClearSingleReward::encode, MessageStcSyncTempClearSingleReward::decode, MessageStcSyncTempClearSingleReward::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncTempClearSingleSubreward.class, MessageStcSyncTempClearSingleSubreward::encode, MessageStcSyncTempClearSingleSubreward::decode, MessageStcSyncTempClearSingleSubreward::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncTempClearSingleSubtask.class, MessageStcSyncTempClearSingleSubtask::encode, MessageStcSyncTempClearSingleSubtask::decode, MessageStcSyncTempClearSingleSubtask::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncTempClearSingleTask.class, MessageStcSyncTempClearSingleTask::encode, MessageStcSyncTempClearSingleTask::decode, MessageStcSyncTempClearSingleTask::handle);
		//sync/stc/delete
		CHANNEL.registerMessage(nextID(), MessageStcSyncDeleteAllChapters.class, MessageStcSyncDeleteAllChapters::encode, MessageStcSyncDeleteAllChapters::decode, MessageStcSyncDeleteAllChapters::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncDeleteAllQuests.class, MessageStcSyncDeleteAllQuests::encode, MessageStcSyncDeleteAllQuests::decode, MessageStcSyncDeleteAllQuests::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncDeleteSingleChapter.class, MessageStcSyncDeleteSingleChapter::encode, MessageStcSyncDeleteSingleChapter::decode, MessageStcSyncDeleteSingleChapter::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncDeleteSingleParty.class, MessageStcSyncDeleteSingleParty::encode, MessageStcSyncDeleteSingleParty::decode, MessageStcSyncDeleteSingleParty::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncDeleteSinglePlayer.class, MessageStcSyncDeleteSinglePlayer::encode, MessageStcSyncDeleteSinglePlayer::decode, MessageStcSyncDeleteSinglePlayer::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncDeleteSingleQuest.class, MessageStcSyncDeleteSingleQuest::encode, MessageStcSyncDeleteSingleQuest::decode, MessageStcSyncDeleteSingleQuest::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncDeleteSingleReward.class, MessageStcSyncDeleteSingleReward::encode, MessageStcSyncDeleteSingleReward::decode, MessageStcSyncDeleteSingleReward::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncDeleteSingleSubreward.class, MessageStcSyncDeleteSingleSubreward::encode, MessageStcSyncDeleteSingleSubreward::decode, MessageStcSyncDeleteSingleSubreward::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncDeleteSingleSubtask.class, MessageStcSyncDeleteSingleSubtask::encode, MessageStcSyncDeleteSingleSubtask::decode, MessageStcSyncDeleteSingleSubtask::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncDeleteSingleTask.class, MessageStcSyncDeleteSingleTask::encode, MessageStcSyncDeleteSingleTask::decode, MessageStcSyncDeleteSingleTask::handle);
		//sync/stc/update
		CHANNEL.registerMessage(nextID(), MessageStcSyncUpdateSingleChapter.class, MessageStcSyncUpdateSingleChapter::encode, MessageStcSyncUpdateSingleChapter::decode, MessageStcSyncUpdateSingleChapter::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncUpdateSingleParty.class, MessageStcSyncUpdateSingleParty::encode, MessageStcSyncUpdateSingleParty::decode, MessageStcSyncUpdateSingleParty::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncUpdateSinglePlayer.class, MessageStcSyncUpdateSinglePlayer::encode, MessageStcSyncUpdateSinglePlayer::decode, MessageStcSyncUpdateSinglePlayer::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncUpdateSingleQuest.class, MessageStcSyncUpdateSingleQuest::encode, MessageStcSyncUpdateSingleQuest::decode, MessageStcSyncUpdateSingleQuest::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncUpdateSingleReward.class, MessageStcSyncUpdateSingleReward::encode, MessageStcSyncUpdateSingleReward::decode, MessageStcSyncUpdateSingleReward::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncUpdateSingleSubreward.class, MessageStcSyncUpdateSingleSubreward::encode, MessageStcSyncUpdateSingleSubreward::decode, MessageStcSyncUpdateSingleSubreward::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncUpdateSingleSubtask.class, MessageStcSyncUpdateSingleSubtask::encode, MessageStcSyncUpdateSingleSubtask::decode, MessageStcSyncUpdateSingleSubtask::handle);
		CHANNEL.registerMessage(nextID(), MessageStcSyncUpdateSingleTask.class, MessageStcSyncUpdateSingleTask::encode, MessageStcSyncUpdateSingleTask::decode, MessageStcSyncUpdateSingleTask::handle);



		CHANNEL.registerMessage(nextID(), MessageUpdateDelivery.class, MessageUpdateDelivery::encode, MessageUpdateDelivery::decode, MessageUpdateDelivery::handle);
		CHANNEL.registerMessage(nextID(), MessageUpdateSinglePlayer.class, MessageUpdateSinglePlayer::encode, MessageUpdateSinglePlayer::decode, MessageUpdateSinglePlayer::handle);
		CHANNEL.registerMessage(nextID(), MessageUpdateSinglePlayerQuestProgress.class, MessageUpdateSinglePlayerQuestProgress::encode, MessageUpdateSinglePlayerQuestProgress::decode, MessageUpdateSinglePlayerQuestProgress::handle);
		CHANNEL.registerMessage(nextID(), MessageReinitQuestingCanvas.class, MessageReinitQuestingCanvas::encode, MessageReinitQuestingCanvas::decode, MessageReinitQuestingCanvas::handle);

		//Standard Content
		CHANNEL.registerMessage(nextID(), MessageCheckboxClick.class, MessageCheckboxClick::encode, MessageCheckboxClick::decode, MessageCheckboxClick::handle);
	}
}
