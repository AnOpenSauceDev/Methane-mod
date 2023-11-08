package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;
import com.modrinth.methane.client.MethaneClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {


    @Inject(method = "disconnect",at=@At("HEAD"))
    public void gracefullyHandleDisconnections(CallbackInfo ci){
        if(MinecraftClient.getInstance().isInSingleplayer()) MethaneClient.ToggleMethaneSetBool(MinecraftClient.getInstance(),false);
    }

    @Inject(method = "disconnect",at=@At("TAIL"))
    public void gracefullyHandleDisconnections2(CallbackInfo ci){
        MethaneClient.ToggleMethaneSetBool(MinecraftClient.getInstance(),true);
    }

}
