package me.wolfie.methane.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethaneServerError implements DedicatedServerModInitializer {
    public static Logger MethaneLogger = LoggerFactory.getLogger("Methane");

    @Override
    public void onInitializeServer() {
        MethaneLogger.error("Methane is running on the server! This mod is client-only! Consider removing Methane from your server.");
    }
}
