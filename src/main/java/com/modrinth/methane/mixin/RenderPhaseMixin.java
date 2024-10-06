package com.modrinth.methane.mixin;

import com.modrinth.methane.core.RenderPhaseGetter;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderPhase.class)
public class RenderPhaseMixin implements RenderPhaseGetter {

    @Mutable @Shadow @Final
    public static RenderPhase.Target MAIN_TARGET = new RenderPhase.Target("main_target", () -> {
    }, () -> {
    });

    @Accessor("MAIN_TARGET")
    public static void setMainTarget(RenderPhase.Target target){
        MAIN_TARGET = target;
    }


    @Override
    public void setTarget(RenderPhase.Target target) {
        setMainTarget(target);
    }

    @Override
    public RenderPhase.Target getTarget() {
        return RenderPhase.MAIN_TARGET;
    }
}