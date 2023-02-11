package me.wolfie.methane.mixin;

import me.wolfie.methane.Methane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(LightingProvider.class)
public class LightProviderMixin {

    @Shadow
    private ChunkLightProvider<?, ?> blockLightProvider;
    @Shadow
    ChunkLightProvider<?, ?> skyLightProvider;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getLight(BlockPos pos, int ambientDarkness) {
        int i = this.skyLightProvider == null ? 0 : this.skyLightProvider.getLightLevel(pos) - ambientDarkness;
        int j = this.blockLightProvider == null ? 0 : this.blockLightProvider.getLightLevel(pos);
        if(Methane.ModActive) {
            return 15;
        }else {
            return Math.max(j, i);
        }
    }

}
