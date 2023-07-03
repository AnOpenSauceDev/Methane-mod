package com.modrinth.methane;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import com.modrinth.methane.client.HudRenderListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Methane implements ModInitializer {

    public static boolean ModActive = true; // for toggles

    public static Logger MethaneLogger = LoggerFactory.getLogger("Methane");
    public static MethaneSettings settings;
    @Override
    public void onInitialize() {
        MethaneLogger.info("Methane has loaded!");
        AutoConfig.register(MethaneSettings.class, GsonConfigSerializer::new);
        settings = AutoConfig.getConfigHolder(MethaneSettings.class).getConfig();

        HudRenderCallback.EVENT.register(new HudRenderListener());
    }

}
