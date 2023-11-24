package com.modrinth.methane.server;

import com.modrinth.methane.Methane;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class MethaneServerError implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() { // Methane Server Utils might load after Methane due to how the Fabric Loader works (meaning that the wrong message will pop up), but this still gets the message across.

        Methane.MethaneLogger.error("----------------------------------### WARNING! Please read! ###----------------------------------------");

        if(!FabricLoader.getInstance().isModLoaded("methane_server")) { // if Methane Server Utils is not installed.
            Methane.MethaneLogger.error("Methane is only meant to run on the client, did you mean to use Methane Server Utils?");
            Methane.MethaneLogger.error("Get Methane Server Utils at: https://modrinth.com/mod/methane-server-utilities");
        }else { // If Methane Server Utils is installed

            Methane.MethaneLogger.error("Methane Server Utils is installed on the server, but so is Methane!");
            Methane.MethaneLogger.error("Methane is not designed to run on the server, Methane Server Utils is meant to.");
            Methane.MethaneLogger.error("Please remove Methane from your server's mods list if you can.");
        }
        Methane.MethaneLogger.error("Methane (not Methane Server Utils) may or may not cause instability on the server, as it's designed for use on the **client** only.");
        Methane.MethaneLogger.error("----------------------------------### End of scary warning. ###----------------------------------------");

    }
}
