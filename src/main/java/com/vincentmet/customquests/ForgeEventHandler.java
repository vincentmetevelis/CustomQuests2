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
import net.minecraft.world.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.FolderName;
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
		if(event.getWorld() instanceof ServerWorld && event.getWorld().dimensionType().equalTo(DimensionType.DEFAULT_OVERWORLD)){
			Ref.currentServerInstance = ((ServerWorld)event.getWorld()).getServer();
			Ref.currentWorldDirectory = ((ServerWorld)event.getWorld()).getServer().getWorldPath(new FolderName("."));
			Ref.currentProgressDirectory = Ref.currentWorldDirectory.resolve(Ref.MODID);
			Ref.progressBackupDirectory = Ref.currentProgressDirectory.resolve("backups");
			if(!event.getWorld().isClientSide()){
				MinecraftForge.EVENT_BUS.post(new DataLoadingEvent.Pre());
				CQHelper.readAllFilesAndPutIntoHashmaps();
				MinecraftForge.EVENT_BUS.post(new DataLoadingEvent.Post());
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
		//Main
		CQHelper.generateMissingProgress(event.getPlayer().getUUID());
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
		if(event.getWorld() instanceof ServerWorld && event.getWorld().dimensionType().equalTo(DimensionType.DEFAULT_OVERWORLD)){
			CQHelper.writeQuestsAndChaptersToFile(Ref.PATH_CONFIG, Ref.FILENAME_QUESTS + Ref.FILE_EXT_JSON);
			CQHelper.writePlayersAndPartiesToFile(Ref.currentProgressDirectory, Ref.FILENAME_PARTIES + Ref.FILE_EXT_JSON);
		}
	}
	
	@SubscribeEvent
	public static void onWorldTick(TickEvent.WorldTickEvent event){
		//Main
		if(event.side.isServer() && event.world.getGameTime() % 100 == 0 && event.phase == TickEvent.Phase.START){
			event.world.players().forEach(playerEntity -> MinecraftForge.EVENT_BUS.post(new CheckCycleEvent(playerEntity)));
		}
	}
	
	@SubscribeEvent
	public static void onCraft(PlayerEvent.ItemCraftedEvent event){
		//Standard Content
		if(EffectiveSide.get().isServer()){
			UUID uuid = event.getPlayer().getUUID();
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
			Entity source = event.getSource().getEntity();
			if(source instanceof PlayerEntity){
				UUID uuid = source.getUUID();
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
					.filter(entry -> entry.getPlayer().toString().equals(player.getStringUUID()))
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
						.filter(entry -> entry.getPlayer().toString().equals(player.getStringUUID()))
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
		if(CombinedProgressHelper.isTaskCompleted(event.getPlayer().getUUID(), event.getQuestId(), event.getTaskId())){
			CombinedProgressHelper.completeTask(event.getPlayer().getUUID(), event.getQuestId(), event.getTaskId());
		}
		ServerUtils.sendProgressAndParties(event.getPlayer());
	}
	
	@SubscribeEvent
	public static void onTaskComplete(QuestEvent.Task.Completed event){
		//Main
		if(QuestingStorage.getSidedPlayersMap().get(event.getPlayer().getStringUUID()).getIndividualProgress().get(event.getQuestId()).areAllTasksCompleted()){
			QuestingStorage.getSidedPlayersMap().get(event.getPlayer().getStringUUID()).getIndividualProgress().get(event.getQuestId()).setAllTasksCompleted(true);
			QuestingStorage.getSidedPlayersMap().get(event.getPlayer().getStringUUID()).getIndividualProgress().getIndividuallyCompletedQuests().add(event.getQuestId());
			CombinedProgressHelper.completeQuest(event.getPlayer().getUUID(), event.getQuestId());
		}
		ServerUtils.sendProgressAndParties(event.getPlayer());
	}
	
	@SubscribeEvent
	public static void onQuestComplete(QuestEvent.Completed event){
		//Main
		ServerUtils.sendProgressAndParties(event.getPlayer());
		MinecraftServer server = event.getPlayer().getServer();
		if(server != null){
			if(ProgressHelper.isPlayerInParty(event.getPlayer().getUUID())){
				int partyId = ProgressHelper.getPlayerParty(event.getPlayer().getUUID());
				PartyHelper.getAllUUIDsInParty(partyId).forEach(uuid -> {
					ServerPlayerEntity playerEntity = server.getPlayerList().getPlayer(uuid);
					if(playerEntity != null){
						try{
							String title = new TranslationTextComponent("customquests.general.quest_completed").getString();
							server.getCommands().getDispatcher().execute("title " + playerEntity.getDisplayName().getString() + " title \"" + title + "\"", server.createCommandSourceStack().withSuppressedOutput());
							server.getCommands().getDispatcher().execute("title " + playerEntity.getDisplayName().getString() + " subtitle \"" + ClientUtils.colorify(QuestHelper.getQuestFromId(event.getQuestId()).getTitle().getText())  + " #" + event.getQuestId() + "\"", server.createCommandSourceStack().withSuppressedOutput());
						}catch(CommandSyntaxException ignored){}
					}
				});
			}else{
				try{
					String title = new TranslationTextComponent("customquests.general.quest_completed").getString();
					server.getCommands().getDispatcher().execute("title " + event.getPlayer().getDisplayName().getString() + " title \"" + title + "\"", server.createCommandSourceStack().withSuppressedOutput());
					server.getCommands().getDispatcher().execute("title " + event.getPlayer().getDisplayName().getString() + " subtitle \"" + ClientUtils.colorify(QuestHelper.getQuestFromId(event.getQuestId()).getTitle().getText())  + " #" + event.getQuestId() + "\"", server.createCommandSourceStack().withSuppressedOutput());
				}catch(CommandSyntaxException ignored){}
			}
		}
	}
	
	@SubscribeEvent
	public static void onCheckCycle(CheckCycleEvent event){
		//Main
		MinecraftForge.EVENT_BUS.post(new DataLoadingEvent.Pre());
		event.getPlayer().level.players().forEach(playerEntity -> {
			QuestingStorage.getSidedQuestsMap().entrySet().stream()
						   .filter(entry -> !CombinedProgressHelper.isQuestCompleted(playerEntity.getUUID(), entry.getKey()))
						   .filter(entry -> CombinedProgressHelper.isQuestUnlocked(playerEntity.getUUID(), entry.getKey()))
						   .forEach(questEntry -> {
							   questEntry.getValue().getTasks().entrySet().stream()
										 .filter(taskEntry -> !CombinedProgressHelper.isTaskCompleted(playerEntity.getUUID(), questEntry.getKey(), taskEntry.getKey()))
										 .forEach(taskEntry -> {
											 taskEntry.getValue().getSubtasks().entrySet().stream()
													  .filter(entrySubtask -> !CombinedProgressHelper.isSubtaskCompleted(playerEntity.getUUID(), questEntry.getKey(), taskEntry.getKey(), entrySubtask.getKey()))
													  .forEach(entrySubtask -> entrySubtask.getValue().getSubtask().getCurrentlyTrackingList().add(new PlayerBoundSubtaskReference(playerEntity.getUUID(), questEntry.getKey(), taskEntry.getKey(), entrySubtask.getKey())));
										 });
						   });
		});
		MinecraftForge.EVENT_BUS.post(new DataLoadingEvent.Post());
		
		//Standard Content
		PlayerEntity player = event.getPlayer();
		if(EffectiveSide.get().isServer()){
			ItemDetectTaskType.TRACKING_LIST
					.stream()
					.filter(entry->entry.getPlayer().toString().equals(player.getStringUUID()))
					.forEach(entry-> QuestingStorage.getSidedQuestsMap().get(entry.getQuestId())
				                               .getTasks().get(entry.getTaskId())
				                               .getSubtasks().get(entry.getSubtaskId())
				                               .getSubtask().executeSubtaskCheck(player, null));
			TravelTaskType.TRACKING_LIST
					.stream()
					.filter(entry->entry.getPlayer().toString().equals(player.getStringUUID()))
					.forEach(entry->QuestingStorage.getSidedQuestsMap().get(entry.getQuestId())
							.getTasks().get(entry.getTaskId())
							.getSubtasks().get(entry.getSubtaskId())
							.getSubtask().executeSubtaskCheck(player, null));
			BiomeDetectTaskType.TRACKING_LIST
					.stream()
					.filter(entry->entry.getPlayer().toString().equals(player.getStringUUID()))
					.forEach(entry->QuestingStorage.getSidedQuestsMap().get(entry.getQuestId())
							.getTasks().get(entry.getTaskId())
							.getSubtasks().get(entry.getSubtaskId())
							.getSubtask().executeSubtaskCheck(player, null));
			XpDetectTaskType.TRACKING_LIST
					.stream()
					.filter(entry->entry.getPlayer().toString().equals(player.getStringUUID()))
					.forEach(entry->QuestingStorage.getSidedQuestsMap().get(entry.getQuestId())
							.getTasks().get(entry.getTaskId())
							.getSubtasks().get(entry.getSubtaskId())
							.getSubtask().executeSubtaskCheck(player, null));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void syncProgressEvent(CheckCycleEvent event){
		UUID eventPlayerUUID = event.getPlayer().getUUID();
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
		if(Objects.KeyBinds.OPEN_QUESTING_SCREEN.isDown() && (Minecraft.getInstance().screen == null || Minecraft.getInstance().screen instanceof InventoryScreen)){
			ClientUtils.openQuestingScreen();
		}
		if(Objects.KeyBinds.CLAIM_ALL_REWARDS.isDown()){
			QuestingStorage.getSidedQuestsMap().entrySet()
						   .stream()
						   .filter(entry -> CombinedProgressHelper.canClaimReward(Minecraft.getInstance().player.getUUID(), entry.getKey()))
						   .forEach(entry ->PacketHandler.CHANNEL.sendToServer(new MessageRewardClaim(entry.getKey(), -1)));
		}
	}
}