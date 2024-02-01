package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(method = "init",at = @At("HEAD"))
    public void warnUserAtTitle(CallbackInfo ci){
        Methane.MethaneDebugger.Log("Starting minecraft under locale: " + MinecraftClient.getInstance().getLanguageManager().getLanguage());

        if(MinecraftClient.getInstance().getLanguageManager().getLanguage().toString().equals("ko_kr")){
            Methane.MethaneDebugger.Log("Korean player detected!");
            Methane.MethaneLogger.warn("WARNING: You are playing with the Korean localization of Minecraft! Toast removal has been disabled for Methane.");
        }
        if (FabricLoader.getInstance().isModLoaded("starlight")){
            Methane.MethaneLogger.error("ERROR: Starlight is being used with Methane! Either:");
            Methane.MethaneLogger.error("1) remove Starlight, or \n 2) Use Methane's Old Lighting Engine in the config.");
        }
    }

}
