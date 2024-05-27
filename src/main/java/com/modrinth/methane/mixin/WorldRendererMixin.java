package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;
import net.minecraft.client.render.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldRenderer.class,priority = 1000)
public class WorldRendererMixin {

    @Inject(method = "renderWeather", at = @At("HEAD"),cancellable = true)
    public void delWeather(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci){
        if(Methane.settings.destructiveSettings.DestroyWeather)
            ci.cancel();
    }

    @Inject(method = "renderLayer", at = @At("HEAD"),cancellable = true)
    public void debugDeleteLayers(RenderLayer renderLayer, double x, double y, double z, Matrix4f matrix4f, Matrix4f positionMatrix, CallbackInfo ci){

        if(Methane.settings.destructiveSettings.RenderLayerSkips && renderLayer.toString().contains("tripwire") /*|| renderLayer.toString().contains("cutout")*/) {
            Methane.MethaneDebugger.Log(renderLayer.toString());
            Methane.MethaneDebugger.LogWarning("skipped renderlayer + " + renderLayer);
            ci.cancel();
        }

    }


}
