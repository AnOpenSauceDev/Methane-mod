package me.wolfie.methane.mixin;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GameRenderer.class)
public abstract class ScrewWithGameRenderer {

    @Shadow
    LightmapTextureManager lightmapTextureManager;

    @Shadow
    MinecraftClient client;

    @Shadow
    public abstract void updateTargetedEntity(float TickDelta);

    @Shadow
    Camera camera;

    @Shadow
    float viewDistance;

    @Shadow
    public abstract double getFov(Camera camera, float tickDelta, boolean changingFov);

    @Shadow
    public abstract Matrix4f getBasicProjectionMatrix(double fov);

    @Shadow
    public abstract void renderHand(MatrixStack matrices, Camera camera, float tickDelta);
    @Shadow
    int ticks;

    @Shadow
    boolean renderHand;

    @Shadow
    abstract void loadProjectionMatrix(Matrix4f matrix4f);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void renderWorld(float tickDelta, long limitTime, MatrixStack matrices) {
        /*
        this.lightmapTextureManager.update(tickDelta);
        if (this.client.getCameraEntity() == null) {
            this.client.setCameraEntity(this.client.player);
        }

         */

        this.updateTargetedEntity(tickDelta);
        this.client.getProfiler().push("center");
        //boolean bl = this.shouldRenderBlockOutline();
        this.client.getProfiler().swap("camera");
        Camera camera = this.camera;
        this.viewDistance = (float)(this.client.options.getClampedViewDistance() * 16);
        MatrixStack matrixStack = new MatrixStack();
        double d = this.getFov(camera, tickDelta, true);
        matrixStack.multiplyPositionMatrix(this.getBasicProjectionMatrix(d));
        /*this.bobViewWhenHurt(matrixStack, tickDelta);
        if ((Boolean)this.client.options.getBobView().getValue()) {
            this.bobView(matrixStack, tickDelta);
        }

         */

        float f = ((Double)this.client.options.getDistortionEffectScale().getValue()).floatValue();
        float g = MathHelper.lerp(tickDelta, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength) * f * f;
        if (g > 0.0f) {
            int i = this.client.player.hasStatusEffect(StatusEffects.NAUSEA) ? 7 : 20;
            float h = 5.0f / (g * g + 5.0f) - g * 0.04f;
            h *= h;
            Vec3f vec3f = new Vec3f(0.0f, MathHelper.SQUARE_ROOT_OF_TWO / 2.0f, MathHelper.SQUARE_ROOT_OF_TWO / 2.0f);
            matrixStack.multiply(vec3f.getDegreesQuaternion(((float)this.ticks + tickDelta) * (float)i));
            matrixStack.scale(1.0f / h, 1.0f, 1.0f);
            float j = -((float)this.ticks + tickDelta) * (float)i;
            matrixStack.multiply(vec3f.getDegreesQuaternion(j));
        }
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        this.loadProjectionMatrix(matrix4f);
        camera.update(this.client.world, (Entity)(this.client.getCameraEntity() == null ? this.client.player : this.client.getCameraEntity()), !this.client.options.getPerspective().isFirstPerson(), this.client.options.getPerspective().isFrontView(), tickDelta);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0f));
        Matrix3f matrix3f = matrices.peek().getNormalMatrix().copy();
        RenderSystem.setInverseViewRotationMatrix(matrix3f);
        this.client.worldRenderer.setupFrustum(matrices, camera.getPos(), this.getBasicProjectionMatrix(Math.max(d, (double)(Integer)this.client.options.getFov().getValue())));
        this.client.worldRenderer.render(matrices, tickDelta, limitTime, false, camera, MinecraftClient.getInstance().gameRenderer, this.lightmapTextureManager, matrix4f);
        this.client.getProfiler().swap("hand");

        if (this.renderHand) {
            RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
            this.renderHand(matrices, camera, tickDelta);
        }



        this.client.getProfiler().pop();
    }



}
