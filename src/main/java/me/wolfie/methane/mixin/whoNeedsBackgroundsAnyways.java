package me.wolfie.methane.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.wolfie.methane.Methane;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = BackgroundRenderer.class, priority = 999)
public class whoNeedsBackgroundsAnyways { // Apparently Sodium does...

    @Shadow
    private static final int field_32685 = 96;
    @Shadow
    public static final float field_32684 = 5000.0F;
    @Shadow
    private static float red;
    @Shadow
    private static float green;
    @Shadow
    private static float blue;
    private static int waterFogColor = -1;
    private static int nextWaterFogColor = -1;
    private static long lastWaterFogColorUpdateTime = -1L;




    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void render(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness) {

            //Side note: we COULD remove all this code, but then the horizon would be black

            Entity entity = camera.getFocusedEntity();

            float f;
            float g;
            float h;
            float r;
            float s;
            float t;




            //this is different

            r = 0.25F + 0.75F * (float) viewDistance / 32.0F;
            r = 1.0F - (float) Math.pow((double) r, 0.25);
            Vec3d vec3d = world.getSkyColor(camera.getPos(), tickDelta);
            s = (float) vec3d.x;
            t = (float) vec3d.y;
            float u = (float) vec3d.z;
            float v = MathHelper.clamp(MathHelper.cos(world.getSkyAngle(tickDelta) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
            BiomeAccess biomeAccess = world.getBiomeAccess();
            Vec3d vec3d2 = camera.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
            Vec3d vec3d3 = CubicSampler.sampleColor(vec3d2, (x, y, z) -> {
                return world.getDimensionEffects().adjustFogColor(Vec3d.unpackRgb(((Biome) biomeAccess.getBiomeForNoiseGen(x, y, z).value()).getFogColor()), v);
            });
            red = (float) vec3d3.getX();
            green = (float) vec3d3.getY();
            blue = (float) vec3d3.getZ();
            if (viewDistance >= 4) {
                f = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0.0F ? -1.0F : 1.0F;
                Vec3f vector3f = new Vec3f(f, 0.0F, 0.0F);
                h = camera.getHorizontalPlane().dot(vector3f);
                if (h < 0.0F) {
                    h = 0.0F;
                }

                if (h > 0.0F) {
                    float[] fs = world.getDimensionEffects().getFogColorOverride(world.getSkyAngle(tickDelta), tickDelta);
                    if (fs != null) {
                        h *= fs[3];
                        red = red * (1.0F - h) + fs[0] * h;
                        green = green * (1.0F - h) + fs[1] * h;
                        blue = blue * (1.0F - h) + fs[2] * h;
                    }
                }
            }

            red += (s - red) * r;
            green += (t - green) * r;
            blue += (u - blue) * r;



            if (skyDarkness > 0.0F) {
                red = red * (1.0F - skyDarkness) + red * 0.7F * skyDarkness;
                green = green * (1.0F - skyDarkness) + green * 0.6F * skyDarkness;
                blue = blue * (1.0F - skyDarkness) + blue * 0.6F * skyDarkness;
            }




            label86:
            {
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity2 = (LivingEntity) entity;
                    if (livingEntity2.hasStatusEffect(StatusEffects.NIGHT_VISION) && !livingEntity2.hasStatusEffect(StatusEffects.DARKNESS)) {
                        s = GameRenderer.getNightVisionStrength(livingEntity2, tickDelta);
                        break label86;
                    }
                }

                s = 0.0F;
                //   }
                //}


                if (red != 0.0F && green != 0.0F && blue != 0.0F) {
                    t = Math.min(1.0F / red, Math.min(1.0F / green, 1.0F / blue));
                    red = red * (1.0F - s) + red * t * s;
                    green = green * (1.0F - s) + green * t * s;
                    blue = blue * (1.0F - s) + blue * t * s;
                }


                RenderSystem.clearColor(red, green, blue, 0.0F);


            }

        }



    /**
     * @author AnOpenSauceDev
     * @reason what fog??!?!? never heard of it.
     */
    @Overwrite
    public static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta) {

    }
}
