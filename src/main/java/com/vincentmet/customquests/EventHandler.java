package com.vincentmet.customquests;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.vincentmet.customquests.api.*;
import com.vincentmet.customquests.event.*;
import com.vincentmet.customquests.standardcontent.StandardContentProgressHelper;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.player.*;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.thread.EffectiveSide;

@Mod.EventBusSubscriber(modid = Ref.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler{//todo make a dirty system for packet updates (for parties and players) #DooDoo
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
	}
	
	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save event){
		CQHelper.writeQuestsAndChaptersToFile(Ref.PATH_CONFIG, Ref.FILENAME_QUESTS + Ref.FILE_EXT_JSON);
		CQHelper.writePlayersAndPartiesToFile(Ref.currentProgressDirectory, Ref.FILENAME_PARTIES + Ref.FILE_EXT_JSON);
	}
	
	@SubscribeEvent
	public static void registerItem(RegistryEvent.Register<Item> event){
		//Main
		event.getRegistry().registerAll(Objects.Items.QUESTING_DEVICE);
		event.getRegistry().registerAll(Objects.ItemBlocks.QUESTING_BLOCK);
		//Standard Content
		//event.getRegistry().registerAll(Objects.ItemBlocks.DELIVERY_BLOCK);
	}
	
	@SubscribeEvent
	public static void registerBlock(RegistryEvent.Register<Block> event){
		//Main
		event.getRegistry().registerAll(Objects.Blocks.QUESTING_BLOCK);
		//Standard Content
		//event.getRegistry().registerAll(Objects.Blocks.DELIVERY_BLOCK);
	}
	
	@SubscribeEvent
	public static void registerTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> event){
		//Standard Content
		//event.getRegistry().registerAll(Objects.TileEntities.DELIVERY_BLOCK);
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
			StandardContentProgressHelper.ITEM_CRAFT_SUBTASKS_TO_CHECK
					.stream()
					.filter(entry -> entry.getFirst().toString().equals(uuid.toString()))
					.forEach(entry -> {
						QuestingStorage.getSidedQuestsMap().get(entry.getSecond())
						                                 .getTasks().get(entry.getThird())
						                                 .getSubtasks().get(entry.getFourth())
						                                 .getSubtask().executeSubtaskCheck(event.getPlayer(), event);
					});
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
		MinecraftServer server = event.getPlayer().getServer();//todo below replace below with proper display message after release, and move to some kind of display queue?!
		if(server != null){
			if(ProgressHelper.isPlayerInParty(event.getPlayer().getUniqueID())){
				int partyId = ProgressHelper.getPlayerParty(event.getPlayer().getUniqueID());
				PartyHelper.getAllUUIDsInParty(partyId).forEach(uuid -> {
					ServerPlayerEntity playerEntity = server.getPlayerList().getPlayerByUUID(uuid);
					if(playerEntity != null){
						try{
							String title = new TranslationTextComponent("customquests.general.quest_completed").getFormattedText();
							server.getCommandManager().getDispatcher().execute("title " + playerEntity.getDisplayName().getString() + " title \"" + title + "\"", server.getCommandSource().withFeedbackDisabled());
							server.getCommandManager().getDispatcher().execute("title " + playerEntity.getDisplayName().getString() + " subtitle \"" + QuestHelper.getQuestFromId(event.getQuestId()).getTitle().getText()  + " #" + event.getQuestId() + "\"", server.getCommandSource().withFeedbackDisabled());
						}catch(CommandSyntaxException e){
							Ref.CustomQuests.LOGGER.warn("There is something wrong with the quest completion message, please notify the mod author (vincentmet)!");
						}
					}
				});
			}else{
				try{
					String title = new TranslationTextComponent("customquests.general.quest_completed").getFormattedText();
					server.getCommandManager().getDispatcher().execute("title " + event.getPlayer().getDisplayName().getString() + " title \"" + title + "\"", server.getCommandSource().withFeedbackDisabled());
					server.getCommandManager().getDispatcher().execute("title " + event.getPlayer().getDisplayName().getString() + " subtitle \"" + QuestHelper.getQuestFromId(event.getQuestId()).getTitle().getText()  + " #" + event.getQuestId() + "\"", server.getCommandSource().withFeedbackDisabled());
				}catch(CommandSyntaxException e){
					Ref.CustomQuests.LOGGER.warn("There is something wrong with the quest completion message, please notify the mod author (vincentmet)!");
				}
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
													  .forEach(entrySubtask -> entrySubtask.getValue().getSubtask().onLoad(playerEntity.getUniqueID(), questEntry.getKey(), taskEntry.getKey(), entrySubtask.getKey()));
										 });
						   });
		});
		MinecraftForge.EVENT_BUS.post(new DataLoadingEvent.Post());
		
		//Standard Content
		PlayerEntity player = event.getPlayer();
		if(EffectiveSide.get().isServer()){
			StandardContentProgressHelper.ITEM_DETECT_SUBTASKS_TO_CHECK
					.stream()
					.filter(entry->entry.getFirst().toString().equals(player.getUniqueID().toString()))
					.forEach(entry-> QuestingStorage.getSidedQuestsMap().get(entry.getSecond())
				                               .getTasks().get(entry.getThird())
				                               .getSubtasks().get(entry.getFourth())
				                               .getSubtask().executeSubtaskCheck(player, null));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void syncProgressEvent(CheckCycleEvent event){
		UUID eventPlayerUUID = event.getPlayer().getUniqueID();
		if(ProgressHelper.isPlayerInParty(eventPlayerUUID)){
			int partyId = ProgressHelper.getPlayerParty(eventPlayerUUID);
			PartyHelper.syncAllPartyDataWithinParty();
			PartyHelper.forEachPlayerInPartyCurrentlyOnline(partyId, ServerUtils::sendProgressAndParties);
		}
	}
	
	/*@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event){
		//SoundEvent se = new SoundEvent(new ResourceLocation(Ref.MODID, "quest0")).setRegistryName(new ResourceLocation(Ref.MODID, "quest0"));
		//QuestingStorage.SOUNDS.put("quest0", se); //todo make this some kind of modular system
		//Main
		event.getRegistry().registerAll(
				//QuestingStorage.SOUNDS.get("quest0")
		);
	}*/
	
	@SubscribeEvent
	public static void onPreDataLoad(DataLoadingEvent.Pre event){
		//Standard Content
		StandardContentProgressHelper.ITEM_DETECT_SUBTASKS_TO_CHECK.clear();
		StandardContentProgressHelper.ITEM_CRAFT_SUBTASKS_TO_CHECK.clear();
		StandardContentProgressHelper.ITEM_SUBMIT_SUBTASKS_TO_CHECK.clear();
	}
	
	@SubscribeEvent
	public static void onPostDataLoad(DataLoadingEvent.Post event){
		PartyHelper.executePartyDeletionQueue();
	}
}