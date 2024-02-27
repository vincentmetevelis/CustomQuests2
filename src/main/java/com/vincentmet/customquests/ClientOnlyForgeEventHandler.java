package com.vincentmet.customquests;

import com.vincentmet.customquests.api.ClientUtils;
import com.vincentmet.customquests.api.CombinedProgressHelper;
import com.vincentmet.customquests.api.QuestingStorage;
import com.vincentmet.customquests.network.messages.PacketHandler;
import com.vincentmet.customquests.network.messages.button.MessageRewardClaim;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Ref.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientOnlyForgeEventHandler {
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