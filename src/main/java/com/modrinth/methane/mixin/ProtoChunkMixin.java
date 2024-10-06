package com.modrinth.methane.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.chunk.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.EnumSet;

@Mixin(ProtoChunk.class)
public abstract class ProtoChunkMixin extends Chunk {

    @Overwrite
    public void setLightingProvider(LightingProvider lightingProvider) {
        //this.lightingProvider = lightingProvider;
    }


    // lighting provider stuff

    public ProtoChunkMixin(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, Registry<Biome> biomeRegistry, long inhabitedTime, @Nullable ChunkSection[] sectionArray, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, heightLimitView, biomeRegistry, inhabitedTime, sectionArray, blendingData);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();

        if (j >= this.getBottomY() && j < this.getTopY()) {
            int l = this.getSectionIndex(j);
            ChunkSection chunkSection = this.getSection(l);
            boolean bl = chunkSection.isEmpty();
            if (bl && state.isOf(Blocks.AIR)) {
                return state;
            } else {
                int m = ChunkSectionPos.getLocalCoord(i);
                int n = ChunkSectionPos.getLocalCoord(j);
                int o = ChunkSectionPos.getLocalCoord(k);
                BlockState blockState = chunkSection.setBlockState(m, n, o, state);
                /*
                if (this.status.isAtLeast(ChunkStatus.INITIALIZE_LIGHT)) {
                    boolean bl2 = chunkSection.isEmpty();
                    if (bl2 != bl) {
                        this.lightingProvider.setSectionStatus(pos, bl2);
                    }

                    if (ChunkLightProvider.needsLightUpdate(this, pos, blockState, state)) {
                        this.chunkSkyLight.isSkyLightAccessible(this, m, j, o);
                        this.lightingProvider.checkBlock(pos);
                    }
                }
                 */

                EnumSet<Heightmap.Type> enumSet = this.getStatus().getHeightmapTypes();
                EnumSet<Heightmap.Type> enumSet2 = null;

                for(Heightmap.Type type : enumSet) {
                    Heightmap heightmap = (Heightmap)this.heightmaps.get(type);
                    if (heightmap == null) {
                        if (enumSet2 == null) {
                            enumSet2 = EnumSet.noneOf(Heightmap.Type.class);
                        }

                        enumSet2.add(type);
                    }
                }

                if (enumSet2 != null) {
                    Heightmap.populateHeightmaps(this, enumSet2);
                }

                for(Heightmap.Type type : enumSet) {
                    ((Heightmap)this.heightmaps.get(type)).trackUpdate(m, j, o, state);
                }

                return blockState;
            }
        } else {
            return Blocks.VOID_AIR.getDefaultState();
        }
    }


}
