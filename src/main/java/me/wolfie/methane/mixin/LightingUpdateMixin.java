package me.wolfie.methane.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
/*
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.LightData;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
 */
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacketData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.BitSet;
import java.util.Iterator;

@Mixin(ClientPacketListener.class)
public abstract class LightingUpdateMixin {

    @Shadow
    private ClientLevel level;

    /**
     * @author AnOpenSauceDev
     * @reason Disable Function
     */
    @Overwrite
    private void applyLightData(int i, int j, ClientboundLightUpdatePacketData clientboundLightUpdatePacketData) {
    }


    /**
     * @author AnOpenSauce Dev
     * @reason prevent updatelighting (mojmap: readSectionList) from being called
     */
    @Overwrite
    private void readSectionList(int i, int j, LevelLightEngine levelLightEngine, LightLayer lightLayer, BitSet bitSet, BitSet bitSet2, Iterator<byte[]> iterator, boolean bl) {

    }



    /**
     * @author
     * @reason
     */
    @Overwrite // VERY likely onLightUpdate
    public void handleLightUpdatePacket(ClientboundLightUpdatePacket clientboundLightUpdatePacket) {
        return;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite // likely updateChunk
    private void updateLevelChunk(int i, int j, ClientboundLevelChunkPacketData clientboundLevelChunkPacketData) {
        return;
     }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void enableChunkLight(LevelChunk levelChunk, int i, int j) {
    }




}
