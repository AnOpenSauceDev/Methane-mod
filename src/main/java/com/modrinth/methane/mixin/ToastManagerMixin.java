package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ToastManager.class,priority = 4500) // take priority over other mixins (and The Open Sauce Toast Killer)
public class ToastManagerMixin { // basically the entire source code of The Open Sauce Toast Killer is here.

    public boolean evaluateToastStatus(){
        if(Methane.settings.disableToasts && !MinecraftClient.getInstance().getLanguageManager().getLanguage().toString().equals("ko_kr")){
            return true;
        }else {
            return false;
        }
    }

    @Inject(method = "draw",at = @At("HEAD"),cancellable = true)
    public void killToasts(DrawContext context, CallbackInfo ci){
        if(evaluateToastStatus()) ci.cancel();
    }

    /**
     * @author AnOpenSauceDev
     * @reason try to forcefully overwrite toast behaviour
     */
    @Inject(method = "add",at=@At("HEAD"),cancellable = true)
    public void add(Toast toast, CallbackInfo ci){
        if(evaluateToastStatus()){ Methane.MethaneDebugger.Log("prevented a toast from loading"); ci.cancel();}
    }


    @Mixin(targets = "net.minecraft.client.toast.ToastManager$Entry")
    static class Entry<T extends Toast> { // inner class of ToastManager

        public boolean evaluateToastStatus(){
            if(Methane.settings.disableToasts && !MinecraftClient.getInstance().getLanguageManager().getLanguage().toString().equals("ko_kr")){
                return true;
            }else {
                return false;
            }
        }


        /**
         * @author AnOpenSauceDev
         * @reason remove toast rendering logic
         */
        @Inject(method = "draw", at=@At("HEAD"),cancellable = true)
        public void draw(int x, DrawContext context, CallbackInfoReturnable<Boolean> cir) { // lie about drawing
            if(evaluateToastStatus()) cir.setReturnValue(true);
        }
    }




}
