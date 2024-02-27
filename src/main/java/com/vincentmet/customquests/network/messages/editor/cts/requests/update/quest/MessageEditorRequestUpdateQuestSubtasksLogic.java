package com.vincentmet.customquests.network.messages.editor.cts.requests.update.quest;

import com.vincentmet.customquests.api.ApiUtils;
import com.vincentmet.customquests.api.EditorServerProcessor;
import com.vincentmet.customquests.api.LogicType;
import com.vincentmet.customquests.api.QuestHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageEditorRequestUpdateQuestSubtasksLogic {
	private final int questId;
	private final int taskId;
	private final LogicType logicType;

	public MessageEditorRequestUpdateQuestSubtasksLogic(int questId, int taskId, LogicType logicType){
		this.questId = questId;
		this.taskId = taskId;
		this.logicType = logicType;
	}

	public static void encode(MessageEditorRequestUpdateQuestSubtasksLogic packet, FriendlyByteBuf buffer){
		buffer.writeInt(packet.questId);
		buffer.writeInt(packet.taskId);
		buffer.writeUtf(packet.logicType.name());
	}
	
	public static MessageEditorRequestUpdateQuestSubtasksLogic decode(FriendlyByteBuf buffer){
		if(buffer.isReadable(10)){//2x4 for int, 2+ for logic type
			return new MessageEditorRequestUpdateQuestSubtasksLogic(buffer.readInt(), buffer.readInt(), buffer.readUtf().equals("OR")?LogicType.OR:LogicType.AND);
		}
		return null;
	}
	
	public static void handle(final MessageEditorRequestUpdateQuestSubtasksLogic message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if(message != null && sender != null){
				if(ApiUtils.hasPlayerEditorAccess(sender) && QuestHelper.doesTaskExist(message.questId, message.taskId)){
					EditorServerProcessor.Update.Quest.Tasks.Task.Subtasks.updateLogic(message.questId, message.taskId, message.logicType);
				}
			}
		}).thenRun(() -> ctx.get().setPacketHandled(true));
	}
}