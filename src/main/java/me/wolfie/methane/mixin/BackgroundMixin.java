package me.wolfie.methane.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = FogRenderer.class, priority = 999)
public class BackgroundMixin {

    @Shadow
    private static float fogRed;
    @Shadow
    private static float fogGreen;
    @Shadow
    private static float fogBlue;
    private static int waterFogColor = -1;
    private static int nextWaterFogColor = -1;
    private static long lastWaterFogColorUpdateTime = -1L;




    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void setupColor(Camera camera, float tickDelta, ClientLevel world, int viewDistance, float skyDarkness) {

            //Side note: we COULD remove all this code, but then the horizon would be black

            Entity entity = camera.getEntity();

            float f;
            float g;
            float h;
            float r;
            float s;
            float t;




            //this is different

            r = 0.25F + 0.75F * (float) viewDistance / 32.0F;
            r = 1.0F - (float) Math.pow((double) r, 0.25);
            Vec3 vec3d = world.getSkyColor(camera.getPosition(), tickDelta);
            s = (float) vec3d.x;
            t = (float) vec3d.y;
            float u = (float) vec3d.z;
            float v = Mth.clamp(Mth.cos(world.getSunAngle(tickDelta) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
            BiomeManager biomeAccess = world.getBiomeManager();
            Vec3 vec3d2 = camera.getPosition().subtract(2.0, 2.0, 2.0).multiply(0.25,0.25,0.25);
            Vec3 vec3d3 = CubicSampler.gaussianSampleVec3(vec3d2, (x, y, z) -> {
                return world.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(((Biome) biomeAccess.getNoiseBiomeAtQuart(x, y, z).value()).getFogColor()), v);
            });
            fogRed = (float) vec3d3.x();
            fogGreen = (float) vec3d3.y();
            fogBlue = (float) vec3d3.z();
            if (viewDistance >= 4) {
                f = Mth.sin(world.getSunAngle(tickDelta)) > 0.0F ? -1.0F : 1.0F;
                Vector3f vector3f = new Vector3f(f, 0.0F, 0.0F);
                h = camera.getLeftVector().dot(vector3f);
                if (h < 0.0F) {
                    h = 0.0F;
                }

                if (h > 0.0F) {
                    float[] fs = world.effects().getSunriseColor(world.getTimeOfDay(tickDelta), tickDelta);
                    if (fs != null) {
                        h *= fs[3];
                        fogRed = fogRed * (1.0F - h) + fs[0] * h;
                        fogGreen = fogGreen * (1.0F - h) + fs[1] * h;
                        fogBlue = fogBlue * (1.0F - h) + fs[2] * h;
                    }
                }
            }

            fogRed += (s - fogRed) * r;
            fogGreen += (t - fogGreen) * r;
            fogBlue += (u - fogBlue) * r;



            if (skyDarkness > 0.0F) {
                fogRed = fogRed * (1.0F - skyDarkness) + fogRed * 0.7F * skyDarkness;
                fogGreen = fogGreen * (1.0F - skyDarkness) + fogGreen * 0.6F * skyDarkness;
                fogBlue = fogBlue * (1.0F - skyDarkness) + fogBlue * 0.6F * skyDarkness;
            }




            label86:
            {
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity2 = (LivingEntity) entity;
                    if (livingEntity2.hasEffect(MobEffects.NIGHT_VISION) && !livingEntity2.hasEffect(MobEffects.DARKNESS)) {
                        s = GameRenderer.getNightVisionScale(livingEntity2, tickDelta);
                        break label86;
                    }
                }

                s = 0.0F;
                //   }
                //}


                if (fogRed != 0.0F && fogGreen != 0.0F && fogBlue != 0.0F) {
                    t = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
                    fogRed = fogRed * (1.0F - s) + fogRed * t * s;
                    fogGreen = fogGreen * (1.0F - s) + fogGreen * t * s;
                    fogBlue = fogBlue * (1.0F - s) + fogBlue * t * s;
                }


                RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);


            }

        }



    /**
     * @author AnOpenSauceDev
     * @reason what fog??!?!? never heard of it.
     */
    @Overwrite
    public static void setupFog(Camera camera, FogRenderer.FogMode fogType, float viewDistance, boolean thickFog, float tickDelta) {

    }
}
