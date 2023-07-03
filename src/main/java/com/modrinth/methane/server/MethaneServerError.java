package com.modrinth.methane.server;

import com.modrinth.methane.Methane;
import net.fabricmc.api.DedicatedServerModInitializer;

public class MethaneServerError implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        Methane.MethaneLogger.error("Methane is running on the server! This mod is client-only! Consider removing Methane from your server.");
    }
}
