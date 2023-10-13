package com.modrinth.methane.util;

import com.modrinth.methane.Methane;
import net.fabricmc.loader.api.FabricLoader;

public class Debug {

    private static boolean isDev = FabricLoader.getInstance().isDevelopmentEnvironment();

    public static void Log(String x){
        if(isDev)
        Methane.DebugLogger.info(x);
    }

    public static void LogWarning(String x){
        if(isDev)
        Methane.DebugLogger.warn(x);
    }

    public static void LogError(String x){
        if(isDev)
        Methane.DebugLogger.error(x);
    }

}
