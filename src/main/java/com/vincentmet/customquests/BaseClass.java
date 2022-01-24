package com.vincentmet.customquests;

import com.vincentmet.customquests.command.CQCommand;
import com.vincentmet.customquests.network.messages.PacketHandler;
import com.vincentmet.customquests.standardcontent.StandardContentRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.event.server.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(Ref.MODID)
public class BaseClass{
    public BaseClass(){
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
		MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::serverStartup);
        MinecraftForge.EVENT_BUS.addListener(this::serverStopping);
	}
	
	private void setupClient(final FMLClientSetupEvent event){
		ClientRegistry.registerKeyBinding(Objects.KeyBinds.OPEN_QUESTING_SCREEN);
		ClientRegistry.registerKeyBinding(Objects.KeyBinds.CLAIM_ALL_REWARDS);
	}
	
	private void setupCommon(final FMLCommonSetupEvent event){
    	//Main
		Ref.questsBackupDirectory = FMLPaths.CONFIGDIR.get().resolve("customquests").resolve("backups");
		Config.readConfigToMemory(Ref.PATH_CONFIG, "config.json");
		PacketHandler.init();
		
		//Standard Content
		StandardContentRegistry.registerTaskTypes();
		StandardContentRegistry.registerRewardTypes();
		StandardContentRegistry.registerButtonShapes();
		StandardContentRegistry.registerTextTypes();
	}
	
	private void serverStopping(final FMLServerStoppingEvent event){
    	Config.writeConfigToDisk(Ref.PATH_CONFIG, "config.json");
	}
	
	private void serverStartup(final FMLServerStartingEvent event){
    	//Main
		Ref.currentServerInstance = event.getServer();
        CQCommand.register(event.getServer().getCommands().getDispatcher());
	}
}