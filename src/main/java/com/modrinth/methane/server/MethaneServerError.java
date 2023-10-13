package com.modrinth.methane.server;

import com.modrinth.methane.Methane;
import net.fabricmc.api.DedicatedServerModInitializer;

public class MethaneServerError implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        Methane.MethaneLogger.error("Methane is only meant to run on the client, did you mean to use Methane Server Utils?");
        Methane.MethaneLogger.error("Get Methane Server Utils at: https://modrinth.com/mod/methane-server-utilities");
    }
}
