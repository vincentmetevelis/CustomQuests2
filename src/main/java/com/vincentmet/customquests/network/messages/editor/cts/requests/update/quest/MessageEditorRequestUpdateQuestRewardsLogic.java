package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.LogicType;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestRewardsLogic {
	private final int questId;
	private final LogicType logicType;

	public MessageEditorRequestUpdateQuestRewardsLogic(int questId, LogicType logicType){
		this.questId = questId;
		this.logicType = logicType;
	}

	public static void encode(MessageEditorRequestUpdateQuestRewardsLogic packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeUtf(packet.logicType.name());
	}
	
	public static MessageEditorRequestUpdateQuestRewardsLogic decode(FriendlyByteBuf buffer){
		if(buffer.isReadable(6)){//4 for int, 2+ for logic type
			return new MessageEditorRequestUpdateQuestRewardsLogic(buffer.readInt(), buffer.readUtf().equals("OR")?LogicType.OR:LogicType.AND);
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestRewardsLogic message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(message != null && sender != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesQuestExist(message.questId)){
					EditorServerProcessor.Update.Quest.Rewards.updateLogic(message.questId, message.logicType);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}