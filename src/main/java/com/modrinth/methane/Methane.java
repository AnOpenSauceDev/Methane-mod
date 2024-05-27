package com.modrinth.methane;

import com.github.anopensaucedev.libmcdevfabric.Debug;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.modrinth.methane.util.MethaneConstants.MOD_NAME;

public class Methane implements ModInitializer {

    public static boolean ModActive = true; // for toggles
    public static Logger MethaneLogger = LoggerFactory.getLogger(MOD_NAME);
    public static MethaneSettings settings;
    public static Debug MethaneDebugger = new Debug("Methane Developer Debugger");

    public static boolean isClient = false;

    public static boolean playerBlockingPacket; // whether the player was blocked due to a race condition

    public static boolean ServerForbidsChanging = false;

    @Override
    public void onInitialize() {

        MethaneLogger.info("Methane has loaded!");
        MethaneDebugger.Log("Methane is in developer mode. If you are reading this in a non-dev environment, please create an issue.");

        AutoConfig.register(MethaneSettings.class, GsonConfigSerializer::new);
        settings = AutoConfig.getConfigHolder(MethaneSettings.class).getConfig();



        if(Methane.settings.destructiveSettings.DestroyWeather || Methane.settings.destructiveSettings.destructiveweatheroptimizations || Methane.settings.destructiveSettings.RenderLayerSkips){
            Methane.MethaneLogger.warn("One or more destructive Methane renderer features are being used. You might experience unusual bugs with other mods.");
        }
    }



    public static boolean intToBoolConversion(int i){
        return i != 0; // if "i" is not zero, return true
    }

}
