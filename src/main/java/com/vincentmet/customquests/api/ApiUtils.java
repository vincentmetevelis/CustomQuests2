package com.vincentmet.customquests.api;

import com.google.gson.JsonObject;
import com.vincentmet.customquests.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Date;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;
import org.codehaus.plexus.util.StringInputStream;

public class ApiUtils{
	//Just to not worry about the errors, it'll fall back to the default item NBT if it fails
	public static CompoundTag getNbtFromJson(String jsonString){
		try {
			return TagParser.parseTag(jsonString);
		}catch (Exception ignored){
			//NOOP
		}
		return new CompoundTag();
	}
	
	public static void writeTo(Path location, String filename, Object text){
		try{
			if(!location.toFile().exists()){
				location.toFile().mkdirs();
			}
			Files.write(location.resolve(filename), text.toString().getBytes());
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static boolean isMouseInBounds(double mouseX, double mouseY, int x1, int y1, int x2, int y2){
		return x2 > mouseX && mouseX >= x1 && y2 > mouseY && mouseY >= y1;
	}
	
	public static JsonObject generateDefaultQuestsJson(){
		JsonObject json = new JsonObject();
		json.add("chapters", new JsonObject());
		json.add("quests", new JsonObject());
		return json;
	}
	
	public static JsonObject generateDefaultPartiesJson(){
		JsonObject json = new JsonObject();
		json.add("parties", new JsonObject());
		json.add("players", new JsonObject());
		return json;
	}
	
	@SuppressWarnings("deprecation")
	public static String getLocalFormattedTime(){
		Date date = new Date();
		int year = 1900+date.getYear();
		int month = date.getMonth() + 1;
		int day = date.getDate();
		int hour = date.getHours();
		int minute = date.getMinutes();
		int second = date.getSeconds();
		
		return year + "-" + month + "-" + day + "_" + hour + "-" + minute + "-" + second;
	}
	
	public static void backupData(){
		File oldFile = Ref.PATH_CONFIG.resolve(Ref.FILENAME_QUESTS+Ref.FILE_EXT_JSON).toFile();
		Path newPath = Ref.questsBackupDirectory;
		File newFile = new File(Ref.FILENAME_QUESTS + "_" + getLocalFormattedTime() + Ref.FILE_EXT_JSON);
		try{
			writeTo(newPath, newFile.toString(), new String(Files.readAllBytes(oldFile.toPath()), StandardCharsets.UTF_8));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void backupProgress(){
		File oldFile = Ref.currentProgressDirectory.resolve(Ref.FILENAME_PARTIES+Ref.FILE_EXT_JSON).toFile();
		Path newPath = Ref.progressBackupDirectory;
		File newFile = new File(Ref.FILENAME_PARTIES + "_" + getLocalFormattedTime() + Ref.FILE_EXT_JSON);
		try{
			writeTo(newPath, newFile.toString(), new String(Files.readAllBytes(oldFile.toPath()), StandardCharsets.UTF_8));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static boolean hasPlayerEditorAccess(ServerPlayer serverPlayer){
		MinecraftServer server = serverPlayer.getServer();
		if(server != null){
			return serverPlayer.hasPermissions(serverPlayer.getServer().getOperatorUserPermissionLevel()) && Config.SidedConfig.isEditModeOn();
		}
		return false;
	}
}