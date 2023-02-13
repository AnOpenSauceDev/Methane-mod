package me.wolfie.methane;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Methane implements ModInitializer {

    public static boolean ModActive = true; // for toggles

    public static Logger MethaneLogger = LoggerFactory.getLogger("Methane");
    @Override
    public void onInitialize() {
        MethaneLogger.info("Methane has loaded!");
        AutoConfig.register(MethaneSettings.class, JanksonConfigSerializer::new);
    }

}
