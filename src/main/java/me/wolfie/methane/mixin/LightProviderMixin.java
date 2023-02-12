package me.wolfie.methane.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightingProvider.class)
public class LightProviderMixin {

    @Inject(method = "getLight", at = @At("RETURN"), cancellable = true)
    private void returnGetLight(BlockPos pos, int ambientDarkness, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(15);
    }
}
