package com.modrinth.methane.mixin;

import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightingProvider.class)
public class LightingProviderMixin {


//    @Inject(method = "doLightUpdates",at = @At("HEAD"),cancellable = true)
//    public void methane$disableLightUpdates(CallbackInfoReturnable<Integer> cir){
//        cir.cancel();
//    }

}
