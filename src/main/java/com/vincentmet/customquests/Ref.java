package com.vincentmet.customquests;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class Ref{
	public static final String MODID = "customquests";
	public static final String VERSION_MOD = "2.3.0-DEV5";
	public static final String VERSION_MC = "1.18.1";
	public static final String VERSION_COMBINED = VERSION_MC + "-" + VERSION_MOD;
	public static Path currentWorldDirectory;
	public static Path currentProgressDirectory;
	public static Path questsBackupDirectory;
	public static Path progressBackupDirectory;
	public static MinecraftServer currentServerInstance;//todo preferable not use this one at all
	public static final int NO_PARTY = -1;
	public static final ResourceLocation INVALID_RESOURCELOCATION = new ResourceLocation(MODID, "invalid");
	
	//Quick and dirty workaround so that I don't have to setup a MessageFactory, but still have the log message look nice :D
	public static final class CustomQuests{
		public static final Logger LOGGER = LogManager.getLogger();
	}
	
	public static final Path PATH_CONFIG = FMLPaths.CONFIGDIR.get().resolve(MODID);
	public static final String FILENAME_QUESTS = "Quests";
	public static final String FILENAME_PARTIES = "Parties";
	public static final String FILE_EXT_JSON = ".json";
}