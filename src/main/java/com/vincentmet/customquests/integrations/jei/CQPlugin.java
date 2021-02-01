package com.vincentmet.customquests.integrations.jei;

import com.vincentmet.customquests.Ref;
import mezz.jei.api.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class CQPlugin implements IModPlugin{
    public static IJeiRuntime runtime;
    public static final ResourceLocation PLUGIN_UID = new ResourceLocation(Ref.MODID, Ref.MODID);
    
    @Override
    public ResourceLocation getPluginUid(){
        return PLUGIN_UID;
    }
    
    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime){
        CQPlugin.runtime = jeiRuntime;
    }
}