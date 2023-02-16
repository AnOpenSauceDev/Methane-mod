package me.wolfie.methane.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.wolfie.methane.Methane;
import me.wolfie.methane.MethaneSettings;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BackgroundRenderer.class)
public class BackgroundMixin {
    /**
     * @author AnOpenSauceDev
     * @reason what fog??!?!? never heard of it.
     */
    @Inject(method = "applyFog", at = @At("HEAD"), cancellable = true)
    private static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        if (!Methane.ModActive && !MethaneSettings.persistFogSettings) return;

        if (hasBlindOrDark(camera.getFocusedEntity())) return;

        if (shouldDisableFog(camera.getSubmersionType(), thickFog, fogType)) {
            RenderSystem.setShaderFogStart(-8.0F);
            RenderSystem.setShaderFogEnd(1_000_000.0F);
            ci.cancel();
        }
    }

    @Unique
    private static boolean shouldDisableFog(CameraSubmersionType submersionType, boolean thickFog, BackgroundRenderer.FogType fogType) {
        if (submersionType == CameraSubmersionType.NONE && MethaneSettings.disableAirFog) return true;
        if (submersionType == CameraSubmersionType.WATER && MethaneSettings.disableWaterFog) return true;
        if (submersionType == CameraSubmersionType.LAVA && MethaneSettings.disableLavaFog) return true;
        if (submersionType == CameraSubmersionType.POWDER_SNOW && MethaneSettings.disablePowderedSnowFog) return true;
        if (thickFog && MethaneSettings.disableThickFog) return true;
        if (fogType == BackgroundRenderer.FogType.FOG_SKY && MethaneSettings.disableSkyFog) return true;
        return false;
    }

    @Unique
    private static boolean hasBlindOrDark(Entity entity) {
        if (!(entity instanceof ClientPlayerEntity)) return false;
        return ((ClientPlayerEntity) entity).hasStatusEffect(StatusEffects.BLINDNESS)
                || ((ClientPlayerEntity) entity).hasStatusEffect(StatusEffects.DARKNESS);
    }
}
