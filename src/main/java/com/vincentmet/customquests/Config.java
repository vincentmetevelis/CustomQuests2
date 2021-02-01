package com.vincentmet.customquests;

import com.google.gson.*;
import com.vincentmet.customquests.api.ApiUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public class Config {
    public static class ReadWrite{
        public static void readFromFile(Path path, String file){
            JsonObject json = loadConfig(path, file);
            if(json.has("can_reward_only_be_claimed_once") && json.get("can_reward_only_be_claimed_once").isJsonPrimitive() && json.get("can_reward_only_be_claimed_once").getAsJsonPrimitive().isBoolean()){
                ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE = json.get("can_reward_only_be_claimed_once").getAsBoolean();
            }
            if(json.has("edit_mode") && json.get("edit_mode").isJsonPrimitive() && json.get("edit_mode").getAsJsonPrimitive().isBoolean()){
                ServerConfig.EDIT_MODE = json.get("edit_mode").getAsBoolean();
            }
        }
        
        private static JsonObject loadConfig(Path path, String filename){
            try {
                StringBuilder res = new StringBuilder();
                Files.readAllLines(path.resolve(filename), StandardCharsets.UTF_8).forEach(res::append);
                return new JsonParser().parse(res.toString()).getAsJsonObject();
            }catch (IOException e) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String out = gson.toJson(getDefaultConfigJson());
                ApiUtils.writeTo(path, filename, out);
                return loadConfig(path, filename);
            }
        }
        
        private static JsonObject getDefaultConfigJson(){
            JsonObject json = new JsonObject();
            json.addProperty("__comment1", "Whether or not only one party member can claim a the reward, or all party members can...");
            json.addProperty("can_reward_only_be_claimed_once", false);
            json.addProperty("__comment2", "[NOT IMPLEMENTED YET] Whether or not edit mode is turned on by default.");
            json.addProperty("edit_mode", false);
            return json;
        }
    }
    
    public static class ServerConfig{
        public static boolean CAN_REWARD_ONLY_BE_CLAIMED_ONCE = false;
        public static boolean EDIT_MODE = false;
    }
    
    public static class ServerToClientSyncedConfig{
        public static boolean CAN_REWARD_ONLY_BE_CLAIMED_ONCE = false;
        public static boolean EDIT_MODE = false;
    }
    
    public static class SidedConfig{
        public static boolean canRewardOnlyBeClaimedOnce(){
            return EffectiveSide.get().isClient() ? ServerToClientSyncedConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE : ServerConfig.CAN_REWARD_ONLY_BE_CLAIMED_ONCE;
        }
        
        public static boolean isEditModeOnByDefault(){
            return EffectiveSide.get().isClient() ? ServerToClientSyncedConfig.EDIT_MODE : ServerConfig.EDIT_MODE;
        }
    }
}