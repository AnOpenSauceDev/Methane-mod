package me.wolfie.methane.mixin;

import me.wolfie.methane.Methane;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;
import java.util.Iterator;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class LightingUpdateMixin implements PacketListener {

    @Inject(method = "readLightData", at = @At("HEAD"), cancellable = true)
    private void readLightData(int x, int z, LightData data, CallbackInfo ci) {
        if (Methane.ModActive) ci.cancel();
    }

    @Inject(method = "updateLighting", at = @At("HEAD"), cancellable = true)
    private void updateLighting(int chunkX, int chunkZ, LightingProvider provider, LightType type, BitSet inited, BitSet uninited, Iterator<byte[]> nibbles, boolean nonEdge, CallbackInfo ci) {
        if (Methane.ModActive) ci.cancel();
    }

    @Inject(method = "onLightUpdate", at = @At("HEAD"), cancellable = true)
    public void onLightUpdate(LightUpdateS2CPacket packet, CallbackInfo ci) { // who needs it anyway?
        if (Methane.ModActive) ci.cancel();
    }

    @Inject(method="scheduleRenderChunk", at = @At("HEAD"), cancellable = true)
    private void scheduleRenderChunk(WorldChunk chunk, int x, int z, CallbackInfo ci) {
        if (Methane.ModActive) {
            ClientPlayNetworkHandler instance = ((ClientPlayNetworkHandler) (Object) this);
            ChunkSection[] chunkSections = chunk.getSectionArray();

            for (int i = 0; i < chunkSections.length; ++i) {
                int j = instance.getWorld().sectionIndexToCoord(i);
                instance.getWorld().scheduleBlockRenders(x, j, z);
            }

            instance.getWorld().markChunkRenderability(x, z);
            ci.cancel();
        }
    }
}
