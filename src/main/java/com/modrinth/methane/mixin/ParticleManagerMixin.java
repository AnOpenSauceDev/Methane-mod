package com.modrinth.methane.mixin;

import com.modrinth.methane.core.builtins.methaneui.panelengine.PanelEngine;
import com.modrinth.methane.core.builtins.methaneui.panelengine.PanelFramebuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.CampfireSmokeParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @Redirect(method = "renderParticles",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;buildGeometry(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V"))
    public void methane$particleDistanceHack(Particle instance, VertexConsumer consumer, Camera camera, float v){
        // if the particle midpoint is outside the range, don't render (but still process, useful for stuff like bee honey).
        if(inRadius2D(instance.getBoundingBox().getCenter(),camera.getPos(),8*16)){ // filler value for 8 chunks RADIUS
            renderedInFrame++;
            instance.buildGeometry(consumer,camera,v);
        } else if (instance instanceof CampfireSmokeParticle) {
            renderedInFrame++;
            instance.buildGeometry(consumer,camera,v);
        }



    }

    @Unique
    public boolean inRadius2D(Vec3d pointA, Vec3d pointB, double radius){
        double dx = pointB.x - pointA.x;
        double dz = pointB.z - pointA.z;

        return (dx * dx + dz * dz) < radius * radius;

    }

    // DEBUG!

    long startTime = 0;

    int renderedInFrame = 0;

    @Inject(method = "renderParticles", at = @At("HEAD"))
    public void methanedebug$Timer1Start(LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci){
        startTime = System.nanoTime();
    }

    @Inject(method = "renderParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;buildGeometry(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V"))
    public void methanedebug$count(LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci){
        //renderedInFrame++;
    }

    @Inject(method = "renderParticles", at = @At("TAIL"))
    public void methanedebug$Timer1Finish(LightmapTextureManager lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci){
        MinecraftClient.getInstance().player.sendMessage(Text.of("Time taken to render particles: " + (System.nanoTime() - startTime) / 1_000_000f + "(ms), rendered " + renderedInFrame + " particles."),true);
        renderedInFrame = 0;
    }

}
