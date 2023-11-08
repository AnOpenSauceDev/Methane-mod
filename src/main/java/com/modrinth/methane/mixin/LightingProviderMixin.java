package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightingProvider.class)
public abstract class LightingProviderMixin {


    @Shadow public abstract int doLightUpdates();

    @Shadow @Final protected HeightLimitView world;

    @Shadow @Final private @Nullable ChunkLightProvider<?, ?> blockLightProvider;

    void FixBrokenLights(){
        this.doLightUpdates();
    }

    boolean isNotInSinglePlayer(){ // we lose out on a lot of performance, but saving is broken without it
        return !MinecraftClient.getInstance().isInSingleplayer();
    }


    /**
     * @author AnOpenSauceDev
     * @reason mess with lighting updates, allowing us to "pause" lighting and save CPU time.
     */
    @Inject(method = "doLightUpdates",at =@At("HEAD"),cancellable = true)
    public void doLightUpdates(CallbackInfoReturnable<Integer> cir) {
        if(Methane.ModActive && isNotInSinglePlayer()) cir.cancel();
    }





}
