package com.modrinth.methane.mixin.getLightLevelHack;

import com.modrinth.methane.Methane;
import com.modrinth.methane.client.BrightnessUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.modrinth.methane.client.BrightnessUtil.calculateBrightnessScale;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    //TODO: lerp brightness to be less jarring



    @Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
    public void MethaneSetCustomLightLevel(Direction direction, boolean shaded, CallbackInfoReturnable<Float> cir){
        if(Methane.ModActive && Methane.settings.dynamicShading) {
            cir.setReturnValue(Math.min(BrightnessUtil.grabBaseGamma() * calculateBrightnessScale(),1)); // only gets darker, never brighter. 1.0F = fully lit.
        }
    }

}
