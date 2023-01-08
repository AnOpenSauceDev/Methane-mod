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

    public static Logger MethaneLogger = LoggerFactory.getLogger("Methane");
    @Override
    public void onInitialize() {
        MethaneLogger.info("Methane has loaded!");
        verifyIfClient.Verify();
        if (FabricLoader.getInstance().isModLoaded("sodium")) {
            MethaneLogger.warn("Hello there! It seems like you have Sodium installed.");
            MethaneLogger.warn("Due to this, some optimizations on things like fog have been disabled.");
            MethaneLogger.warn("That being said, everything else 'should' work fine.");
        }
    }

}
