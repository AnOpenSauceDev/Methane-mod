package com.modrinth.methane.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/*  OPTIONAL MIXIN  */
@Mixin(Particle.class)
public abstract class ParticleMixin {

    // This is very similar to what Methane used to do, disabling lighting and collision logic for particles.

    @Shadow private boolean stopped;

    @Shadow public abstract void setBoundingBox(Box boundingBox);

    @Shadow public abstract Box getBoundingBox();

    @Shadow protected abstract void repositionFromBoundingBox();

    /**
     * @author AnOpenSauceDev
     * @reason Disable particle collisions, can greatly improve CPU performance in some (albeit unlikely in vanilla SP) particle-heavy situations
     */
    @Overwrite
    public void move(double dx, double dy, double dz) {
        if (!this.stopped) {
            //double d = dx;
            double e = dy;
            //double f = dz;

            /*
            if (this.collidesWithWorld && (dx != 0.0 || dy != 0.0 || dz != 0.0) && dx * dx + dy * dy + dz * dz < MAX_SQUARED_COLLISION_CHECK_DISTANCE) {
                Vec3d vec3d = Entity.adjustMovementForCollisions(null, new Vec3d(dx, dy, dz), this.getBoundingBox(), this.world, List.of());
                dx = vec3d.x;
                dy = vec3d.y;
                dz = vec3d.z;
            }
             */

            // move logic
            if (dx != 0.0 || dy != 0.0 || dz != 0.0) {
                this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
                this.repositionFromBoundingBox();
            }

            if (Math.abs(e) >= 1.0E-5F && Math.abs(dy) < 1.0E-5F) {
                this.stopped = true;
            }

            /*
            this.onGround = e != dy && e < 0.0;
            if (d != dx) {
                this.velocityX = 0.0;
            }
             */

            /*
            if (f != dz) {
                this.velocityZ = 0.0;
            }
             */

        }
    }


    @Inject(method = "getBrightness",at=@At("HEAD"),cancellable = true)
    public void methane$BrightnessHack(float tint, CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(240);
    }


}
