package com.modrinth.methane.core;

import net.minecraft.client.render.RenderPhase;

public interface RenderPhaseGetter {

    void setTarget(RenderPhase.Target target);
    RenderPhase.Target getTarget();

}
