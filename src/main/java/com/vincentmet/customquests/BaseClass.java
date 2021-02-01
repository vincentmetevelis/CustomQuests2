package com.vincentmet.customquests;

import com.vincentmet.customquests.command.CQCommand;
import com.vincentmet.customquests.network.messages.PacketHandler;
import com.vincentmet.customquests.standardcontent.StandardContentRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(Ref.MODID)
public class BaseClass{
    public BaseClass(){
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
		MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(EventHandler.class);
        MinecraftForge.EVENT_BUS.addListener(this::serverStartup);
	}
	
	private void setupCommon(final FMLCommonSetupEvent event){
    	//Main
		Ref.questsBackupDirectory = FMLPaths.CONFIGDIR.get().resolve("customquests").resolve("backups");
		Config.ReadWrite.readFromFile(Ref.PATH_CONFIG, "config.json");
		PacketHandler.init();
		
		//Standard Content
		StandardContentRegistry.registerTaskTypes();
		StandardContentRegistry.registerRewardTypes();
		StandardContentRegistry.registerButtonShapes();
		StandardContentRegistry.registerTextTypes();
		
	}
	
	private void serverStartup(final FMLServerStartingEvent event){
    	//Main
		Ref.currentServerInstance = event.getServer();
        CQCommand.register(event.getCommandDispatcher());
	}
}