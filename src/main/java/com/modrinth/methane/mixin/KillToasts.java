package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;
import com.modrinth.methane.util.Debug;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

@Mixin(ToastManager.class)
public class KillToasts {

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
        Debug.Log("prevented a toast from loading");
    }


    @Mixin(targets = "net.minecraft.client.toast.ToastManager$Entry")
    static class Entry<T extends Toast> { // inner class of ToastManager
        /**
         * @author
         * @reason
         */
        @Overwrite
        public boolean draw(int x, DrawContext context) { // lie about drawing
            Debug.Log("prevented a toast from drawing on client");
            return true;
        }
    }




}
