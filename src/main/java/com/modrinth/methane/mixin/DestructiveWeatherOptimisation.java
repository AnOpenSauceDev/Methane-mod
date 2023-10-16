package com.modrinth.methane.mixin;


import com.modrinth.methane.Methane;
import com.modrinth.methane.MethaneSettings;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class DestructiveWeatherOptimisation { // Dragons Beware!

    @Inject(method = "renderWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBiome(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/registry/entry/RegistryEntry;"),cancellable = true)
    public void biomeHacks(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci){
        if(Methane.ModActive && MethaneSettings.DestructiveSettings.destructiveweatheroptimizations) ci.cancel();
    }

    @Inject(method = "renderWeather", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/biome/Biome;hasPrecipitation()Z"),cancellable = true)
    public void biomeHacksPt2(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci){
        if(Methane.ModActive && MethaneSettings.DestructiveSettings.destructiveweatheroptimizations) rtrue(); // Before anyone asks me this, the rain splashing is ticked separately, thus causing the weird biome-dependent splashing effects. (which i don't really want to remove, but who knows)
    }

    public boolean rtrue(){
        return  true;
    }

}
