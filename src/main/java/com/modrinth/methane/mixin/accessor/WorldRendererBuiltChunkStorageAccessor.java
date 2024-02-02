package com.modrinth.methane.mixin.accessor;

import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldRenderer.class)
public interface WorldRendererBuiltChunkStorageAccessor {

    @Accessor
    BuiltChunkStorage getChunks();

}
