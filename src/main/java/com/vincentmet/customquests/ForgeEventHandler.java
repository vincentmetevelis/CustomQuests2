package com.vincentmet.customquests;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.event.*;
import com.vincentmet.customquests.helpers.PlayerBoundSubtaskReference;
import com.vincentmet.customquests.network.messages.*;
import com.vincentmet.customquests.standardcontent.tasktypes.*;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stats;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.items.ItemHandlerHelper;

@Mod.EventBusSubscriber(modid = Ref.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventHandler{
	@SubscribeEvent
	public static void onWorldStart(WorldEvent.Load event){
		//Main
		if(event.getWorld() instanceof ServerWorld && ((ServerWorld)event.getWorld()).dimension.getType().equals(DimensionType.OVERWORLD)){
			Ref.currentServerInstance = ((ServerWorld)event.getWorld()).getServer();
			Ref.currentWorldDirectory = ((ServerWorld)event.getWorld()).getSaveHandler().getWorldDirectory().toPath();
			Ref.currentProgressDirectory = Ref.currentWorldDirectory.resolve(Ref.MODID);
			Ref.progressBackupDirectory = Ref.currentProgressDirectory.resolve("backups");
			if(!event.getWorld().isRemote()){
				MinecraftForge.EVENT_BUS.post(new DataLoadingEvent.Pre());
				CQHelper.readAllFilesAndPutIntoHashmaps();
				MinecraftForge.EVENT_BUS.post(new DataLoadingEvent.Post());
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
		//Main
		CQHelper.generateMissingProgress(event.getPlayer().getUniqueID());
		CQHelper.generateMissingPartyProgress();
		ServerUtils.sendQuestsAndChapters((ServerPlayerEntity)event.getPlayer());
		ServerUtils.sendProgressAndParties((ServerPlayerEntity)event.getPlayer());
		ServerUtils.sendServerConfigToClient((ServerPlayerEntity)event.getPlayer());
		
		if(Config.SidedConfig.giveDeviceOnFirstLogin()){
			if(event.getPlayer() instanceof ServerPlayerEntity){
				ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
				if(player.getStats().getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) == 0){
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Objects.Items.QUESTING_DEVICE));
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save event){
		//Main
		if(event.getWorld() instanceof ServerWorld && ((ServerWorld)event.getWorld()).dimension.getType().equals(DimensionType.OVERWORLD)){
			CQHelper.writeQuestsAndChaptersToFile(Ref.PATH_CONFIG, Ref.FILENAME_QUESTS + Ref.FILE_EXT_JSON);
			CQHelper.writePlayersAndPartiesToFile(Ref.currentProgressDirectory, Ref.FILENAME_PARTIES + Ref.FILE_EXT_JSON);
		}
	}
	
	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event){
		//Main
		if(event.side.isServer() && event.world.getGameTime() % 100 == 0 && event.phase == TickEvent.Phase.START){
			event.world.getPlayers().forEach(playerEntity -> MinecraftForge.EVENT_BUS.post(new CheckCycleEvent(playerEntity)));
		}
	}
	
	@SubscribeEvent
	public static void onCraft(PlayerEvent.ItemCraftedEvent event){
		//Standard Content
		if(EffectiveSide.get().isServer()){
			UUID uuid = event.getPlayer().getUniqueID();
			ItemCraftTaskType.TRACKING_LIST
					.stream()
					.filter(entry -> entry.getPlayer().toString().equals(uuid.toString()))
					.forEach(entry -> {
						QuestingStorage.getSidedQuestsMap().get(entry.getQuestId())
						                                 .getTasks().get(entry.getTaskId())
						                                 .getSubtasks().get(entry.getSubtaskId())
						                                 .getSubtask().executeSubtaskCheck(event.getPlayer(), event);
					});
		}
	}
	
	@SubscribeEvent
	public static void onEntityKill(LivingDeathEvent event){
		//Standard Content
		if(EffectiveSide.get().isServer()){
			Entity source = event.getSource().getTrueSource();
			if(source instanceof PlayerEntity){
				UUID uuid = source.getUniqueID();
				HuntTaskType.TRACKING_LIST
						.stream()
						.filter(entry -> entry.getPlayer().toString().equals(uuid.toString()))
						.forEach(entry -> {
							QuestingStorage.getSidedQuestsMap().get(entry.getQuestId())
										   .getTasks().get(entry.getTaskId())
										   .getSubtasks().get(entry.getSubtaskId())
										   .getSubtask().executeSubtaskCheck((PlayerEntity)source, event);
						});
			}
		}
	}
	
	@SubscribeEvent
	public static void onBlockMined(BlockEvent.BreakEvent event){
		//Standard Content
		if(EffectiveSide.get().isServer()){
			PlayerEntity player = event.getPlayer();
			BlockMinedTaskType.TRACKING_LIST
					.stream()
					.filter(entry -> entry.getPlayer().toString().equals(player.getUniqueID().toString()))
					.forEach(entry -> {
						QuestingStorage.getSidedQuestsMap().get(entry.getQuestId())
									   .getTasks().get(entry.getTaskId())
									   .getSubtasks().get(entry.getSubtaskId())
									   .getSubtask().executeSubtaskCheck(player, event);
					});
			
		}
	}
	
	@SubscribeEvent
	public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event){
		//Standard Content
		if(EffectiveSide.get().isServer()){
			if(event.getEntity() instanceof PlayerEntity){
				PlayerEntity player = (PlayerEntity)event.getEntity();
				BlockPlacedTaskType.TRACKING_LIST
						.stream()
						.filter(entry -> entry.getPlayer().toString().equals(player.getUniqueID().toString()))
						.forEach(entry -> {
							QuestingStorage.getSidedQuestsMap().get(entry.getQuestId())
										   .getTasks().get(entry.getTaskId())
										   .getSubtasks().get(entry.getSubtaskId())
										   .getSubtask().executeSubtaskCheck(player, event);
						});
			}
		}
	}
	
	@SubscribeEvent
	public static void onSubtaskComplete(QuestEvent.Task.Subtask.Completed event){
		//Main
		if(CombinedProgressHelper.isTaskCompleted(event.getPlayer().getUniqueID(), event.getQuestId(), event.getTaskId())){
			CombinedProgressHelper.completeTask(event.getPlayer().getUniqueID(), event.getQuestId(), event.getTaskId());
		}
		ServerUtils.sendProgressAndParties(event.getPlayer());
	}
	
	@SubscribeEvent
	public static void onTaskComplete(QuestEvent.Task.Completed event){
		//Main
		if(QuestingStorage.getSidedPlayersMap().get(event.getPlayer().getUniqueID().toString()).getIndividualProgress().get(event.getQuestId()).areAllTasksCompleted()){
			QuestingStorage.getSidedPlayersMap().get(event.getPlayer().getUniqueID().toString()).getIndividualProgress().get(event.getQuestId()).setAllTasksCompleted(true);
			QuestingStorage.getSidedPlayersMap().get(event.getPlayer().getUniqueID().toString()).getIndividualProgress().getIndividuallyCompletedQuests().add(event.getQuestId());
			CombinedProgressHelper.completeQuest(event.getPlayer().getUniqueID(), event.getQuestId());
		}
		ServerUtils.sendProgressAndParties(event.getPlayer());
	}
	
	@SubscribeEvent
	public static void onQuestComplete(QuestEvent.Completed event){
		//Main
		ServerUtils.sendProgressAndParties(event.getPlayer());
		MinecraftServer server = event.getPlayer().getServer();
		if(server != null){
			if(ProgressHelper.isPlayerInParty(event.getPlayer().getUniqueID())){
				int partyId = ProgressHelper.getPlayerParty(event.getPlayer().getUniqueID());
				PartyHelper.getAllUUIDsInParty(partyId).forEach(uuid -> {
					ServerPlayerEntity playerEntity = server.getPlayerList().getPlayerByUUID(uuid);
					if(playerEntity != null){
						try{
							String title = new TranslationTextComponent("customquests.general.quest_completed").getFormattedText();
							server.getCommandManager().getDispatcher().execute("title " + playerEntity.getDisplayName().getString() + " title \"" + title + "\"", server.getCommandSource().withFeedbackDisabled());
							server.getCommandManager().getDispatcher().execute("title " + playerEntity.getDisplayName().getString() + " subtitle \"" + ClientUtils.colorify(QuestHelper.getQuestFromId(event.getQuestId()).getTitle().getText())  + " #" + event.getQuestId() + "\"", server.getCommandSource().withFeedbackDisabled());
						}catch(CommandSyntaxException ignored){}
					}
				});
			}else{
				try{
					String title = new TranslationTextComponent("customquests.general.quest_completed").getFormattedText();
					server.getCommandManager().getDispatcher().execute("title " + event.getPlayer().getDisplayName().getString() + " title \"" + title + "\"", server.getCommandSource().withFeedbackDisabled());
					server.getCommandManager().getDispatcher().execute("title " + event.getPlayer().getDisplayName().getString() + " subtitle \"" + ClientUtils.colorify(QuestHelper.getQuestFromId(event.getQuestId()).getTitle().getText())  + " #" + event.getQuestId() + "\"", server.getCommandSource().withFeedbackDisabled());
				}catch(CommandSyntaxException ignored){}
			}
		}
	}
	
	@SubscribeEvent
	public static void onCheckCycle(CheckCycleEvent event){
		//Main
		MinecraftForge.EVENT_BUS.post(new DataLoadingEvent.Pre());
		event.getPlayer().world.getPlayers().forEach(playerEntity -> {
			QuestingStorage.getSidedQuestsMap().entrySet().stream()
						   .filter(entry -> !CombinedProgressHelper.isQuestCompleted(playerEntity.getUniqueID(), entry.getKey()))
						   .filter(entry -> CombinedProgressHelper.isQuestUnlocked(playerEntity.getUniqueID(), entry.getKey()))
						   .forEach(questEntry -> {
							   questEntry.getValue().getTasks().entrySet().stream()
										 .filter(taskEntry -> !CombinedProgressHelper.isTaskCompleted(playerEntity.getUniqueID(), questEntry.getKey(), taskEntry.getKey()))
										 .forEach(taskEntry -> {
											 taskEntry.getValue().getSubtasks().entrySet().stream()
													  .filter(entrySubtask -> !CombinedProgressHelper.isSubtaskCompleted(playerEntity.getUniqueID(), questEntry.getKey(), taskEntry.getKey(), entrySubtask.getKey()))
													  .forEach(entrySubtask -> entrySubtask.getValue().getSubtask().getCurrentlyTrackingList().add(new PlayerBoundSubtaskReference(playerEntity.getUniqueID(), questEntry.getKey(), taskEntry.getKey(), entrySubtask.getKey())));
										 });
						   });
		});
		MinecraftForge.EVENT_BUS.post(new DataLoadingEvent.Post());
		
		//Standard Content
		PlayerEntity player = event.getPlayer();
		if(EffectiveSide.get().isServer()){
			ItemDetectTaskType.TRACKING_LIST
					.stream()
					.filter(entry->entry.getPlayer().toString().equals(player.getUniqueID().toString()))
					.forEach(entry-> QuestingStorage.getSidedQuestsMap().get(entry.getQuestId())
				                               .getTasks().get(entry.getTaskId())
				                               .getSubtasks().get(entry.getSubtaskId())
				                               .getSubtask().executeSubtaskCheck(player, null));
			TravelTaskType.TRACKING_LIST
					.stream()
					.filter(entry->entry.getPlayer().toString().equals(player.getUniqueID().toString()))
					.forEach(entry->QuestingStorage.getSidedQuestsMap().get(entry.getQuestId())
							.getTasks().get(entry.getTaskId())
							.getSubtasks().get(entry.getSubtaskId())
							.getSubtask().executeSubtaskCheck(player, null));
			BiomeDetectTaskType.TRACKING_LIST
					.stream()
					.filter(entry->entry.getPlayer().toString().equals(player.getUniqueID().toString()))
					.forEach(entry->QuestingStorage.getSidedQuestsMap().get(entry.getQuestId())
							.getTasks().get(entry.getTaskId())
							.getSubtasks().get(entry.getSubtaskId())
							.getSubtask().executeSubtaskCheck(player, null));
			XpDetectTaskType.TRACKING_LIST
					.stream()
					.filter(entry->entry.getPlayer().toString().equals(player.getUniqueID().toString()))
					.forEach(entry->QuestingStorage.getSidedQuestsMap().get(entry.getQuestId())
							.getTasks().get(entry.getTaskId())
							.getSubtasks().get(entry.getSubtaskId())
							.getSubtask().executeSubtaskCheck(player, null));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void syncProgressEvent(CheckCycleEvent event){
		UUID eventPlayerUUID = event.getPlayer().getUniqueID();
		if(ProgressHelper.isPlayerInParty(eventPlayerUUID)){
			int partyId = ProgressHelper.getPlayerParty(eventPlayerUUID);
			PartyHelper.syncDataBetweenPartyMembers(partyId);
		}
	}
	
	@SubscribeEvent
	public static void onPreDataLoad(DataLoadingEvent.Pre event){
		//Standard Content
		ItemDetectTaskType.TRACKING_LIST.clear();
		ItemCraftTaskType.TRACKING_LIST.clear();
		ItemSubmitTaskType.TRACKING_LIST.clear();
		TravelTaskType.TRACKING_LIST.clear();
		HuntTaskType.TRACKING_LIST.clear();
		BiomeDetectTaskType.TRACKING_LIST.clear();
		XpDetectTaskType.TRACKING_LIST.clear();
		XpSubmitTaskType.TRACKING_LIST.clear();
		BlockMinedTaskType.TRACKING_LIST.clear();
		BlockPlacedTaskType.TRACKING_LIST.clear();
	}
	
	@SubscribeEvent
	public static void onClientTick(TickEvent.ClientTickEvent event){
		if(Objects.KeyBinds.OPEN_QUESTING_SCREEN.isPressed() && (Minecraft.getInstance().currentScreen == null || Minecraft.getInstance().currentScreen instanceof InventoryScreen)){
			ClientUtils.openQuestingScreen();
		}
		if(Objects.KeyBinds.CLAIM_ALL_REWARDS.isPressed()){
			QuestingStorage.getSidedQuestsMap().entrySet()
						   .stream()
						   .filter(entry -> CombinedProgressHelper.canClaimReward(Minecraft.getInstance().player.getUniqueID(), entry.getKey()))
						   .forEach(entry ->PacketHandler.CHANNEL.sendToServer(new MessageRewardClaim(entry.getKey(), -1)));
		}
	}
}