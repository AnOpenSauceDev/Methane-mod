package com.modrinth.methane.mixin;


import com.modrinth.methane.Methane;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Particle.class,priority = 1200)
public abstract class ParticleMixin {

    @Shadow public abstract Box getBoundingBox();

    @Shadow @Final protected ClientWorld world;

    @Shadow private boolean stopped;

    @Shadow protected boolean collidesWithWorld;

    @Shadow @Final private static double MAX_SQUARED_COLLISION_CHECK_DISTANCE;

    @Shadow public abstract void setBoundingBox(Box boundingBox);

    @Shadow protected abstract void repositionFromBoundingBox();

    @Shadow protected boolean onGround;

    @Shadow protected double velocityX;

    @Shadow protected double velocityZ;

    @Inject(method = "getBrightness",at = @At("HEAD"), cancellable = true)
    public void skipBrightnessCalc(float tint, CallbackInfoReturnable<Integer> cir){

        if(Methane.ModActive) cir.cancel(); // saves a lot of CPU time, probably because getting the light of hundreds of particles without *any* grouping is slow

    }


    // crashes with a handful of mods, but has a HUGE performance gain per-particle.
    /**
     * @author AnOpenSauceDev
     * @reason I couldn't get this to behave right, so I ruined everything
     */
    @Overwrite
    public void move(double dx, double dy, double dz) {
        if (stopped) {
            return;
        }
        double d = dx;
        double e = dy;
        double f = dz;

       /* for some reason this saves tonnes of CPU time when it rains...
        if (collidesWithWorld && (dx != 0.0 || dy != 0.0 || dz != 0.0) && dx * dx + dy * dy + dz * dz < MAX_SQUARED_COLLISION_CHECK_DISTANCE && !Methane.settings.destructiveweatheroptimizations) {
            Vec3d vec3d = Entity.adjustMovementForCollisions(null, new Vec3d(dx, dy, dz), this.getBoundingBox(), this.world, List.of());
            dx = vec3d.x;
            dy = vec3d.y;
            dz = vec3d.z;
        }

        */
        if (dx != 0.0 || dy != 0.0 || dz != 0.0) {
            this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
            this.repositionFromBoundingBox();
        }
        if (Math.abs(e) >= (double)1.0E-5f && Math.abs(dy) < (double)1.0E-5f) {
            this.stopped = true;
        }
        boolean bl = this.onGround = e != dy && e < 0.0;
        if (d != dx) {
            this.velocityX = 0.0;
        }
        if (f != dz) {
            this.velocityZ = 0.0;
        }
    }

}
