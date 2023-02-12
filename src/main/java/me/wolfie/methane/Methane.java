package me.wolfie.methane;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Methane implements ClientModInitializer {

    public static boolean ModActive = true; // for toggles

    public static Logger MethaneLogger = LoggerFactory.getLogger("Methane");
    @Override
    public void onInitializeClient() {
        MethaneLogger.info("Methane has loaded!");
    }

}
