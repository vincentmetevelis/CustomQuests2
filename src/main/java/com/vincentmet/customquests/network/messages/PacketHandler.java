package com.vincentmet.customquests.network.messages;

import com.vincentmet.customquests.Ref;
import com.vincentmet.customquests.network.messages.editor.MessageEditorAddChapter;
import com.vincentmet.customquests.network.messages.editor.MessageEditorAddQuest;
import com.vincentmet.customquests.network.messages.editor.MessageEditorRemoveChapter;
import com.vincentmet.customquests.network.messages.editor.MessageEditorRemoveQuest;
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
	
	public static void init() {
		//Main
		CHANNEL.registerMessage(nextID(), MessageDiscord.class, MessageDiscord::encode, MessageDiscord::decode, MessageDiscord::handle);
		CHANNEL.registerMessage(nextID(), MessageTaskButton.class, MessageTaskButton::encode, MessageTaskButton::decode, MessageTaskButton::handle);
		CHANNEL.registerMessage(nextID(), MessageRewardClaim.class, MessageRewardClaim::encode, MessageRewardClaim::decode, MessageRewardClaim::handle);
		CHANNEL.registerMessage(nextID(), MessageUpdateSingleQuest.class, MessageUpdateSingleQuest::encode, MessageUpdateSingleQuest::decode, MessageUpdateSingleQuest::handle);
		CHANNEL.registerMessage(nextID(), MessageUpdateSingleChapter.class, MessageUpdateSingleChapter::encode, MessageUpdateSingleChapter::decode, MessageUpdateSingleChapter::handle);
		CHANNEL.registerMessage(nextID(), MessageUpdateSinglePlayer.class, MessageUpdateSinglePlayer::encode, MessageUpdateSinglePlayer::decode, MessageUpdateSinglePlayer::handle);
		CHANNEL.registerMessage(nextID(), MessageUpdateSinglePlayerQuestProgress.class, MessageUpdateSinglePlayerQuestProgress::encode, MessageUpdateSinglePlayerQuestProgress::decode, MessageUpdateSinglePlayerQuestProgress::handle);
		CHANNEL.registerMessage(nextID(), MessageUpdateSingleParty.class, MessageUpdateSingleParty::encode, MessageUpdateSingleParty::decode, MessageUpdateSingleParty::handle);
		CHANNEL.registerMessage(nextID(), MessageUpdateDelivery.class, MessageUpdateDelivery::encode, MessageUpdateDelivery::decode, MessageUpdateDelivery::handle);
		CHANNEL.registerMessage(nextID(), MessageClearChapters.class, MessageClearChapters::encode, MessageClearChapters::decode, MessageClearChapters::handle);
		CHANNEL.registerMessage(nextID(), MessageClearSingleChapter.class, MessageClearSingleChapter::encode, MessageClearSingleChapter::decode, MessageClearSingleChapter::handle);
		CHANNEL.registerMessage(nextID(), MessageClearQuests.class, MessageClearQuests::encode, MessageClearQuests::decode, MessageClearQuests::handle);
		CHANNEL.registerMessage(nextID(), MessageClearSingleQuest.class, MessageClearSingleQuest::encode, MessageClearSingleQuest::decode, MessageClearSingleQuest::handle);
		CHANNEL.registerMessage(nextID(), MessageClearPlayers.class, MessageClearPlayers::encode, MessageClearPlayers::decode, MessageClearPlayers::handle);
		CHANNEL.registerMessage(nextID(), MessageClearParties.class, MessageClearParties::encode, MessageClearParties::decode, MessageClearParties::handle);
		CHANNEL.registerMessage(nextID(), MessageUpdateServerSettings.class, MessageUpdateServerSettings::encode, MessageUpdateServerSettings::decode, MessageUpdateServerSettings::handle);
		CHANNEL.registerMessage(nextID(), MessageReinitQuestingCanvas.class, MessageReinitQuestingCanvas::encode, MessageReinitQuestingCanvas::decode, MessageReinitQuestingCanvas::handle);
		CHANNEL.registerMessage(nextID(), MessageOpenEditor.class, MessageOpenEditor::encode, MessageOpenEditor::decode, MessageOpenEditor::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorAddChapter.class, MessageEditorAddChapter::encode, MessageEditorAddChapter::decode, MessageEditorAddChapter::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorAddQuest.class, MessageEditorAddQuest::encode, MessageEditorAddQuest::decode, MessageEditorAddQuest::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRemoveChapter.class, MessageEditorRemoveChapter::encode, MessageEditorRemoveChapter::decode, MessageEditorRemoveChapter::handle);
		CHANNEL.registerMessage(nextID(), MessageEditorRemoveQuest.class, MessageEditorRemoveQuest::encode, MessageEditorRemoveQuest::decode, MessageEditorRemoveQuest::handle);
		
		//Standard Content
		CHANNEL.registerMessage(nextID(), MessageCheckboxClick.class, MessageCheckboxClick::encode, MessageCheckboxClick::decode, MessageCheckboxClick::handle);
	}
}
