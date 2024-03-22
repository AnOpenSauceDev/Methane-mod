package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;

import com.modrinth.methane.MethaneSettings;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.MapRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GameRenderer.class, priority = 400) //our priority must be lower than exordium's own priority, otherwise exordium will get very angry and crash MC.
public abstract class GameRendererMixin {

    @Shadow @Final private LightmapTextureManager lightmapTextureManager;

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/LightmapTextureManager;update(F)V"))
    private void update(LightmapTextureManager instance, float delta) {
        if (Methane.ModActive) return;
        instance.update(delta);
    }

}
