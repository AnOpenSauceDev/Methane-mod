package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ToastManager.class,priority = 4500) // take priority over other mixins (and The Open Sauce Toast Killer)
public class KillToasts { // basically the entire source code of The Open Sauce Toast Killer is here.

    @Inject(method = "draw",at = @At("HEAD"),cancellable = true)
    public void killToasts(DrawContext context, CallbackInfo ci){
        //Debug.Log("Killed a toast that tried to draw");
        ci.cancel();
    }

    /**
     * @author AnOpenSauceDev
     * @reason try to forcefully overwrite toast behaviour
     */
    @Overwrite
    public void add(Toast toast){
        Methane.MethaneDebugger.Log("prevented a toast from loading");
    }


    @Mixin(targets = "net.minecraft.client.toast.ToastManager$Entry")
    static class Entry<T extends Toast> { // inner class of ToastManager
        /**
         * @author AnOpenSauceDev
         * @reason remove toast rendering logic
         */
        @Overwrite
        public boolean draw(int x, DrawContext context) { // lie about drawing
            Methane.MethaneDebugger.Log("prevented a toast from drawing!");
            return true;
        }
    }




}
