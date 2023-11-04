package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightingProvider.class)
public class LightingProviderMixin {

    /**
     * @author
     * @reason
     */
    @Inject(method = "doLightUpdates",at =@At("HEAD"),cancellable = true)
    public void doLightUpdates(CallbackInfoReturnable<Integer> cir) {
        if(Methane.ModActive) cir.cancel();
    }

}
