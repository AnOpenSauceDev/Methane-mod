package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GameRenderer.class, priority = 400) //exordium kludge
public abstract class GameRendererMixin {

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/LightmapTextureManager;update(F)V"))
    private void update(LightmapTextureManager instance, float delta) {
        if (Methane.ModActive) return;
        instance.update(delta);
    }


}
