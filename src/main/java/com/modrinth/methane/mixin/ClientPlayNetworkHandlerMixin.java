package com.modrinth.methane.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "startWorldLoading", at = @At("HEAD"))
    public void loadHook(ClientPlayerEntity player, ClientWorld world, CallbackInfo ci){
        if(FabricLoader.getInstance().isModLoaded("voxy")){
            player.sendMessage(Text.of("You're using Voxy alongside Methane. Toggling Methane's status alongside Voxy breaks it's LOD's in very weird ways. "));
            player.sendMessage(Text.of("Disable Methane outside of the world when you want to toggle while using Voxy."));
        }
    }

}
