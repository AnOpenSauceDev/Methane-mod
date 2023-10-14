package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;
import com.modrinth.methane.MethaneSettings;
import com.modrinth.methane.util.Debug;
import net.minecraft.block.BlockRenderType;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldRenderer.class,priority = Integer.MAX_VALUE) // >:)
public class WorldRendererMixin {

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"),cancellable = true)
    public void deletesky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci){
        if(MethaneSettings.destructiveSettings.DestroySky)
        ci.cancel();
    }

    @Inject(method = "renderWeather", at = @At("HEAD"),cancellable = true)
    public void delWeather(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci){
        if(MethaneSettings.destructiveSettings.DestroyWeather)
            ci.cancel();
    }

    @Inject(method = "renderLayer", at = @At("HEAD"),cancellable = true)
    public void debugDeleteLayers(RenderLayer renderLayer, MatrixStack matrices, double cameraX, double cameraY, double cameraZ, Matrix4f positionMatrix, CallbackInfo ci){

        if(MethaneSettings.destructiveSettings.RenderLayerSkips && renderLayer.toString().contains("tripwire") /*|| renderLayer.toString().contains("cutout")*/) {
            Debug.Log(renderLayer.toString());
            Debug.LogWarning("skipped renderlayer + " + renderLayer);
            ci.cancel();
        }
    }


}
