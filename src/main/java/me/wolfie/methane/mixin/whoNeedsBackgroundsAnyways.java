package me.wolfie.methane.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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


    private static boolean warned = false;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static void render(Camera camera, float tickDelta, ClientWorld world, int viewDistance, float skyDarkness) {
        if(MinecraftClient.getInstance().player != null) {
            if (FabricLoader.getInstance().isModLoaded("sodium")) {
                if (!warned) {
                    warned = true;
                    MinecraftClient.getInstance().player.sendMessage(Text.of("Hello there! It seems like you have Sodium installed."));
                    MinecraftClient.getInstance().player.sendMessage(Text.of("If you aren't seeing a bunch of weird 'lighting' bugs, ignore this."));
                    MinecraftClient.getInstance().player.sendMessage(Text.of("In the case that you are... ¯\\_(ツ)_/¯"));
                }
            }
        }
        /*
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();
        float f;
        float g;
        float h;
        float r;
        float s;
        float t;

         */
        /*
        if (cameraSubmersionType == CameraSubmersionType.WATER) {
            long l = Util.getMeasuringTimeMs();
            int i = ((Biome)world.getBiome(new BlockPos(camera.getPos())).value()).getWaterFogColor();
            if (lastWaterFogColorUpdateTime < 0L) {
                waterFogColor = i;
                nextWaterFogColor = i;
                lastWaterFogColorUpdateTime = l;
            }

            int j = waterFogColor >> 16 & 255;
            int k = waterFogColor >> 8 & 255;
            int m = waterFogColor & 255;
            int n = nextWaterFogColor >> 16 & 255;
            int o = nextWaterFogColor >> 8 & 255;
            int p = nextWaterFogColor & 255;
            f = MathHelper.clamp((float)(l - lastWaterFogColorUpdateTime) / 5000.0F, 0.0F, 1.0F);
            g = MathHelper.lerp(f, (float)n, (float)j);
            h = MathHelper.lerp(f, (float)o, (float)k);
            float q = MathHelper.lerp(f, (float)p, (float)m);
            red = g / 255.0F;
            green = h / 255.0F;
            blue = q / 255.0F;
            if (waterFogColor != i) {
                waterFogColor = i;
                nextWaterFogColor = MathHelper.floor(g) << 16 | MathHelper.floor(h) << 8 | MathHelper.floor(q);
                lastWaterFogColorUpdateTime = l;
            }
        } else if (cameraSubmersionType == CameraSubmersionType.LAVA) {
            red = 0.6F;
            green = 0.1F;
            blue = 0.0F;
            lastWaterFogColorUpdateTime = -1L;
        } else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
            red = 0.623F;
            green = 0.734F;
            blue = 0.785F;
            lastWaterFogColorUpdateTime = -1L;
            RenderSystem.clearColor(red, green, blue, 0.0F);
        } */
         /*
            r = 0.25F + 0.75F * (float)viewDistance / 32.0F;
            r = 1.0F - (float)Math.pow((double)r, 0.25);
            Vec3d vec3d = world.getSkyColor(camera.getPos(), tickDelta);
            s = (float)vec3d.x;
            t = (float)vec3d.y;
            float u = (float)vec3d.z;
            float v = MathHelper.clamp(MathHelper.cos(world.getSkyAngle(tickDelta) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
            BiomeAccess biomeAccess = world.getBiomeAccess();
            Vec3d vec3d2 = camera.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
            Vec3d vec3d3 = CubicSampler.sampleColor(vec3d2, (x, y, z) -> {
                return world.getDimensionEffects().adjustFogColor(Vec3d.unpackRgb(((Biome)biomeAccess.getBiomeForNoiseGen(x, y, z).value()).getFogColor()), v);
            });
            red = (float)vec3d3.getX();
            green = (float)vec3d3.getY();
            blue = (float)vec3d3.getZ();
            if (viewDistance >= 4) {
                f = MathHelper.sin(world.getSkyAngleRadians(tickDelta)) > 0.0F ? -1.0F : 1.0F;
                Vector3f vector3f = new Vector3f(f, 0.0F, 0.0F);
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
            f = world.getRainGradient(tickDelta);
            if (f > 0.0F) {
                g = 1.0F - f * 0.5F;
                h = 1.0F - f * 0.4F;
                red *= g;
                green *= g;
                blue *= h;
            }

            g = world.getThunderGradient(tickDelta);
            if (g > 0.0F) {
                h = 1.0F - g * 0.5F;
                red *= h;
                green *= h;
                blue *= h;
            }

            //lastWaterFogColorUpdateTime = -1L;
        }

       // r = ((float)camera.getPos().y - (float)world.getBottomY()) * world.getLevelProperties().getHorizonShadingRatio();
       /*
       BackgroundRenderer.StatusEffectFogModifier statusEffectFogModifier = getFogModifier(entity, tickDelta);
        if (statusEffectFogModifier != null) {
            LivingEntity livingEntity = (LivingEntity)entity;
            r = statusEffectFogModifier.applyColorModifier(livingEntity, livingEntity.getStatusEffect(statusEffectFogModifier.getStatusEffect()), r, tickDelta);
        }



        if (skyDarkness > 0.0F) {
            red = red * (1.0F - skyDarkness) + red * 0.7F * skyDarkness;
            green = green * (1.0F - skyDarkness) + green * 0.6F * skyDarkness;
            blue = blue * (1.0F - skyDarkness) + blue * 0.6F * skyDarkness;
        }



        if (cameraSubmersionType == CameraSubmersionType.WATER) {
            if (entity instanceof ClientPlayerEntity) {
                s = ((ClientPlayerEntity)entity).getUnderwaterVisibility();
            } else {
                s = 1.0F;
            }
        } else {
            label86: {
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity2 = (LivingEntity)entity;
                    if (livingEntity2.hasStatusEffect(StatusEffects.NIGHT_VISION) && !livingEntity2.hasStatusEffect(StatusEffects.DARKNESS)) {
                        s = GameRenderer.getNightVisionStrength(livingEntity2, tickDelta);
                        break label86;
                    }
                }

                s = 0.0F;
            }
        }



        if (red != 0.0F && green != 0.0F && blue != 0.0F) {
            t = Math.min(1.0F / red, Math.min(1.0F / green, 1.0F / blue));
            red = red * (1.0F - s) + red * t * s;
            green = green * (1.0F - s) + green * t * s;
            blue = blue * (1.0F - s) + blue * t * s;
        }

        RenderSystem.clearColor(red, green, blue, 0.0F);

        */
        float v = MathHelper.clamp(MathHelper.cos(world.getSkyAngle(tickDelta) * 6.2831855F) * 2.0F + 0.5F, 0.0F, 1.0F);
        BiomeAccess biomeAccess = world.getBiomeAccess();
        Vec3d vec3d2 = camera.getPos().subtract(2.0, 2.0, 2.0).multiply(0.25);
        Vec3d vec3d3 = CubicSampler.sampleColor(vec3d2, (x, y, z) -> {
            return world.getDimensionEffects().adjustFogColor(Vec3d.unpackRgb(((Biome)biomeAccess.getBiomeForNoiseGen(x, y, z).value()).getFogColor()), v);
        });
    }

    /**
     * @author AnOpenSauceDev
     * @reason what fog??!?!? never heard of it.
     */
    @Overwrite
    public static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta) {
        /*
        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        Entity entity = camera.getFocusedEntity();
        BackgroundRenderer.FogData fogData = new BackgroundRenderer.FogData(fogType);
        BackgroundRenderer.StatusEffectFogModifier statusEffectFogModifier = getFogModifier(entity, tickDelta);
        if (cameraSubmersionType == CameraSubmersionType.LAVA) {
            if (entity.isSpectator()) {
                fogData.fogStart = -8.0F;
                fogData.fogEnd = viewDistance * 0.5F;
            } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                fogData.fogStart = 0.0F;
                fogData.fogEnd = 3.0F;
            } else {
                fogData.fogStart = 0.25F;
                fogData.fogEnd = 1.0F;
            }
        } else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
            if (entity.isSpectator()) {
                fogData.fogStart = -8.0F;
                fogData.fogEnd = viewDistance * 0.5F;
            } else {
                fogData.fogStart = 0.0F;
                fogData.fogEnd = 2.0F;
            }
        } else if (statusEffectFogModifier != null) {
            LivingEntity livingEntity = (LivingEntity)entity;
            StatusEffectInstance statusEffectInstance = livingEntity.getStatusEffect(statusEffectFogModifier.getStatusEffect());
            if (statusEffectInstance != null) {
                statusEffectFogModifier.applyStartEndModifier(fogData, livingEntity, statusEffectInstance, viewDistance, tickDelta);
            }
        } else if (cameraSubmersionType == CameraSubmersionType.WATER) {
            fogData.fogStart = -8.0F;
            fogData.fogEnd = 96.0F;
            if (entity instanceof ClientPlayerEntity) {
                ClientPlayerEntity clientPlayerEntity = (ClientPlayerEntity)entity;
                fogData.fogEnd *= Math.max(0.25F, clientPlayerEntity.getUnderwaterVisibility());
                RegistryEntry<Biome> registryEntry = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
                if (registryEntry.isIn(BiomeTags.HAS_CLOSER_WATER_FOG)) {
                    fogData.fogEnd *= 0.85F;
                }
            }

            if (fogData.fogEnd > viewDistance) {
                fogData.fogEnd = viewDistance;
                fogData.fogShape = FogShape.CYLINDER;
            }
        } else if (thickFog) {
            fogData.fogStart = viewDistance * 0.05F;
            fogData.fogEnd = Math.min(viewDistance, 192.0F) * 0.5F;
        } else if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
            fogData.fogStart = 0.0F;
            fogData.fogEnd = viewDistance;
            fogData.fogShape = FogShape.CYLINDER;
        } else {
            float f = MathHelper.clamp(viewDistance / 10.0F, 4.0F, 64.0F);
            fogData.fogStart = viewDistance - f;
            fogData.fogEnd = viewDistance;
            fogData.fogShape = FogShape.CYLINDER;
        }

        RenderSystem.setShaderFogStart(fogData.fogStart);
        RenderSystem.setShaderFogEnd(fogData.fogEnd);
        RenderSystem.setShaderFogShape(fogData.fogShape);

         */
    }

}
