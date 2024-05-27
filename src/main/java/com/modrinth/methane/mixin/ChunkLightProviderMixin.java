package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkLightProvider.class)
public class ChunkLightProviderMixin {

    @Inject(method = "doLightUpdates", at = @At("HEAD"),cancellable = true)
    public void hack(CallbackInfoReturnable<Integer> cir){

        if(!Methane.settings.useOldLightingEngine && Methane.ModActive){
            cir.cancel();
        }

    }

}
