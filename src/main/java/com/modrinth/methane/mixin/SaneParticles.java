package com.modrinth.methane.mixin;


import com.modrinth.methane.Methane;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Particle.class)
public class SaneParticles {

    @Inject(method = "getBrightness",at = @At("HEAD"), cancellable = true)
    public void skipBrightnessCalc(float tint, CallbackInfoReturnable<Integer> cir){

        if(Methane.ModActive) cir.cancel(); // saves a lot of time, probably because getting the light of hundreds of particles without *any* grouping is slow

    }

}
