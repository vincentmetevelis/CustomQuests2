package com.vincentmet.customquests;

import com.google.gson.*;
import com.vincentmet.customquests.api.ApiUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class Config {
    
    public static void processJson(JsonObject json){
        if(json.has("can_reward_only_be_claimed_once")){
            JsonElement jsonElement = json.get("can_reward_only_be_claimed_once");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isBoolean()){
                    ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE = jsonPrimitive.getAsBoolean();
                }else{
                    ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE = false;
                }
            }else{
                ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE = false;
            }
        }else{
            ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE = false;
        }
        
        if(json.has("give_device_on_first_login")){
            JsonElement jsonElement = json.get("give_device_on_first_login");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isBoolean()){
                    ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN = jsonPrimitive.getAsBoolean();
                }else{
                    ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN = false;
                }
            }else{
                ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN = false;
            }
        }else{
            ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN = false;
        }
        
        if(json.has("debug_mode")){
            JsonElement jsonElement = json.get("debug_mode");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isBoolean()){
                    ServerConfig.DEBUG_MODE = jsonPrimitive.getAsBoolean();
                }else{
                    ServerConfig.DEBUG_MODE = false;
                }
            }else{
                ServerConfig.DEBUG_MODE = false;
            }
        }else{
            ServerConfig.DEBUG_MODE = false;
        }
        
        if(json.has("backups")){
            JsonElement jsonElement = json.get("backups");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isBoolean()){
                    ServerConfig.BACKUPS = jsonPrimitive.getAsBoolean();
                }else{
                    ServerConfig.BACKUPS = false;
                }
            }else{
                ServerConfig.BACKUPS = false;
            }
        }else{
            ServerConfig.BACKUPS = false;
        }
        
        if(json.has("edit_mode")){
            JsonElement jsonElement = json.get("edit_mode");
            if(jsonElement.isJsonPrimitive()){
                JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
                if(jsonPrimitive.isBoolean()){
                    ServerConfig.EDIT_MODE = jsonPrimitive.getAsBoolean();
                }else{
                    ServerConfig.EDIT_MODE = false;
                }
            }else{
                ServerConfig.EDIT_MODE = false;
            }
        }else{
            ServerConfig.EDIT_MODE = false;
        }
    }
    
    public static JsonObject getJson(){
        JsonObject json = new JsonObject();
        json.addProperty("can_reward_only_be_claimed_once", ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE);
        json.addProperty("give_device_on_first_login", ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN);
        json.addProperty("debug_mode", ServerConfig.DEBUG_MODE);
        json.addProperty("backups", ServerConfig.BACKUPS);
        json.addProperty("edit_mode", ServerConfig.EDIT_MODE);
        return json;
    }
    
    public static void readConfigToMemory(Path path, String file){
        processJson(loadConfig(path, file));
        writeConfigToDisk(path, file);
    }
    
    public static void writeConfigToDisk(Path path, String file){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String out = gson.toJson(getJson());
        ApiUtils.writeTo(path, file, out);
    }
    
    private static JsonObject loadConfig(Path path, String filename){
        try {
            StringBuilder res = new StringBuilder();
            Files.readAllLines(path.resolve(filename), StandardCharsets.UTF_8).forEach(res::append);
            return new JsonParser().parse(res.toString()).getAsJsonObject();
        }catch (IOException e) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String out = gson.toJson(new JsonObject());
            ApiUtils.writeTo(path, filename, out);
            return loadConfig(path, filename);
        }
    }
    
    public static class ServerConfig{
        public static boolean CAN_REWARD_ONLY_BE_CLAIMED_ONCE = false;
        public static boolean GIVE_DEVICE_ON_FIRST_LOGIN = false;
        public static boolean DEBUG_MODE = false;
        public static boolean BACKUPS = false;
        public static boolean EDIT_MODE = false;
    }
    
    public static class ServerToClientSyncedConfig{
        public static boolean CAN_REWARD_ONLY_BE_CLAIMED_ONCE = false;
        public static boolean GIVE_DEVICE_ON_FIRST_LOGIN = false;
        public static boolean DEBUG_MODE = false;
        public static boolean BACKUPS = false;
        public static boolean EDIT_MODE = false;
    }
    
    public static class SidedConfig{
        public static boolean canRewardOnlyBeClaimedOnce(){
            return EffectiveSide.get().isClient() ? ServerToClientSyncedConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE : ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE;
        }
        
        public static boolean giveDeviceOnFirstLogin(){
            return EffectiveSide.get().isClient() ? ServerToClientSyncedConfig.GIVE_DEVICE_ON_FIRST_LOGIN : ServerConfig.GIVE_DEVICE_ON_FIRST_LOGIN;
        }
        
        public static boolean isDebugModeOn(){
            return EffectiveSide.get().isClient() ? ServerToClientSyncedConfig.DEBUG_MODE : ServerConfig.DEBUG_MODE;
        }
        
        public static boolean areBackupsEnabled(){
            return EffectiveSide.get().isClient() ? ServerToClientSyncedConfig.BACKUPS : ServerConfig.BACKUPS;
        }
        
        public static boolean isEditModeOnByDefault(){
            return EffectiveSide.get().isClient() ? ServerToClientSyncedConfig.EDIT_MODE : ServerConfig.EDIT_MODE;
        }
    }
}