package com.modrinth.methane.client;

import com.github.anopensaucedev.libmcdevfabric.Debug;
import com.modrinth.methane.Methane;
import com.modrinth.methane.mixin.accessor.WorldRendererBuiltChunkStorageAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BuiltChunkStorage;

import java.util.Arrays;

public class BrightnessUtil {

    // our default brightness value * the user's gamma
    public static float DEFAULT_LIGHT_VALUE = grabBaseGamma(); // 1.0F = fully lit, 0.0F = complete darkness

    public static float grabBaseGamma(){
        float f = MinecraftClient.getInstance().options.getGamma().getValue().floatValue() + 0.1f;
        f += 1.0f;
        return f / 2;
    }

    public static float calculateBrightnessScale(){
        MinecraftClient client = MinecraftClient.getInstance();

        float scale = 1.0F; // 1 = normal, 0 = pitch black.

        long time = client.world.getTimeOfDay();
        if(time >= 0 && time <= 12000){ // 0 <-> 12000 ticks is when the sky is fully bright

        } else if (time >= 12001 && time <= 12999) { // 12001 <-> 13000 is when the sky gets a bit darker
            scale = 0.9F;
        } else if (time >= 13000 && time <= 17000) { // darker... (mobs start spawning!)
            scale = 0.5F;
        } else if (time >= 17001 && time <= 20000) { // darker...
            scale = 0.25F;
        } else if (time >= 20001 && time <= 23000) { // lighter!
            scale = 0.5F;
        } else if (time >= 23001 && time <= 24000) { // day!
            scale = 1.0F;
        }



        return scale;
    }

    public static void rebuildChunks(MinecraftClient client){
        BuiltChunkStorage storage = ((WorldRendererBuiltChunkStorageAccessor) client.worldRenderer).getChunks();
        Arrays.stream(storage.chunks).parallel().forEach(builtChunk -> {
            builtChunk.scheduleRebuild(true);
        });
    }

}
