package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;
import com.modrinth.methane.util.MethaneConstants;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public abstract class LightmapTextureManagerMixin implements PacketListener {

    @Shadow
    private float flickerIntensity;
    @Shadow
    private boolean dirty;

    @Shadow public abstract void disable();

    @Shadow public abstract void close();

    @Shadow public abstract void enable();


    @Inject(method = "update", at = @At("HEAD"),cancellable = true)
    public void cancel(float delta, CallbackInfo ci){
        if(Methane.ModActive) ci.cancel();

    }


}
