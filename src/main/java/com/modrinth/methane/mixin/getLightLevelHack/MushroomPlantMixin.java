package com.modrinth.methane.mixin.getLightLevelHack;

import com.modrinth.methane.Methane;
import net.minecraft.block.BlockState;
import net.minecraft.block.MushroomPlantBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MushroomPlantBlock.class)
public class MushroomPlantMixin {

    @Redirect(method = "canPlaceAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldView;getBaseLightLevel(Lnet/minecraft/util/math/BlockPos;I)I"))
    public int canPlaceAt(WorldView instance, BlockPos blockPos, int i) {
       if(Methane.ModActive){
         return 0;
       }else {
           return instance.getBaseLightLevel(blockPos,i);
       }
    }


}
