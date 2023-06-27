package me.wolfie.methane.mixin;

import me.wolfie.methane.Methane;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;
import java.util.Iterator;

@Mixin(LightmapTextureManager.class)
public abstract class LightingUpdateMixin implements PacketListener {


    @Shadow
    private float flickerIntensity;
    @Shadow
    private boolean dirty;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void tick(){
        if(Methane.settings.modstate){

        }else {
            this.flickerIntensity += (float)((Math.random() - Math.random()) * Math.random() * Math.random() * 0.1);
            this.flickerIntensity *= 0.9f;
            this.dirty = true;
        }
    }

}
