package com.modrinth.methane.mixin.getLightLevelHack;

import com.modrinth.methane.Methane;
import com.modrinth.methane.client.BrightnessUtil;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.modrinth.methane.client.BrightnessUtil.calculateBrightnessScale;

@Mixin(ChunkRendererRegion.class)
public class ChunkRenderReigonMixin {


    //impl. of: BlockRenderView$getBrightness
    @Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
    public void MethaneSetCustomLightLevel(Direction direction, boolean shaded, CallbackInfoReturnable<Float> cir){
        if(Methane.ModActive && Methane.settings.dynamicShading) {
            cir.setReturnValue(Math.min(BrightnessUtil.grabBaseGamma() * calculateBrightnessScale(),1)); // only gets darker, never brighter. 1.0F = fully lit.
        }
    }

}
