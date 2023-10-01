package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
@Mixin(LightmapTextureManager.class)
public abstract class LightingUpdateMixin implements PacketListener {

    @Shadow
    private float flickerIntensity;
    @Shadow
    private boolean dirty;

    /**
     * @author AnOpenSauceDev
     * @reason force light to not tick.
     */
    @Overwrite
    public void tick(){
        if(Methane.ModActive){
            //TODO: light flicker impl
            this.dirty = false;
            return;
        }
        else
        {
            this.flickerIntensity += (float)((Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
            this.flickerIntensity *= 0.9f;
            this.dirty = true;
        }

    }

}
