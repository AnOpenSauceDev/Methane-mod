package me.wolfie.methane;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Methane implements ModInitializer {

    public static boolean ModActive = true; // for toggles

    public static Logger MethaneLogger = LoggerFactory.getLogger("Methane");
    @Override
    public void onInitialize() {
        MethaneLogger.info("Methane has loaded!");
    }

}
