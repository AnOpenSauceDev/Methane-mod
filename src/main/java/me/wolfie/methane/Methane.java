package me.wolfie.methane;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.FogShape;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Methane implements ModInitializer {

    public static boolean ModActive = true; // for toggles

    public static Logger MethaneLogger = LoggerFactory.getLogger("Methane");
    @Override
    public void onInitialize() {
        MethaneLogger.info("Methane has loaded!");
        verifyIfClient.Verify();
        if (FabricLoader.getInstance().isModLoaded("sodium")) {
            // TODO: make these translatable later on
            MethaneLogger.warn("--- Sodium has been detected! ---");
            MethaneLogger.warn("Because of this, some minor fog-related things have been disabled.");
        }
    }

}
