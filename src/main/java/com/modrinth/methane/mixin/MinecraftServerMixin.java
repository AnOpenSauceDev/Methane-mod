package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;
import com.modrinth.methane.client.MethaneClient;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftServer.class, priority = 3000)
public class MinecraftServerMixin {

    //fixes saving when someone ALT-F4's
    @Inject(method = "shutdown", at = @At("HEAD"))
    public void shutdownHook(CallbackInfo ci){
        MethaneClient.ToggleMethaneSetBool(null,false); // forcefully shutdown Methane properly, just in case it's active.
        Methane.ModActive = false;
    }

}
