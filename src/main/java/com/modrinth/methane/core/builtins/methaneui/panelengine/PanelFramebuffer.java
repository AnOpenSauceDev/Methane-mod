package com.modrinth.methane.core.builtins.methaneui.panelengine;

import com.modrinth.methane.client.objlib.ObjModel;
import com.modrinth.methane.core.RenderPhaseGetter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public class PanelFramebuffer {

    public Identifier FRAMEBUFFER_ID;
    public XRPanel parentXRPanel;

    public PanelFramebuffer(XRPanel parent,boolean resizeable, int x, int y){
        this.FRAMEBUFFER_ID = Identifier.of(parent.XR_PANEL_ID.getNamespace(),parent.XR_PANEL_ID.getPath() + ".framebuffer");
        this.parentXRPanel = parent;

        panelFramebuffer = new SimpleFramebuffer(x,y,true,true);
    }

    // use `beginWrite` and `endWrite`. The former take over render calls and the latter to restore render output to Minecraft's Framebuffer.
    SimpleFramebuffer panelFramebuffer;

    //public static ObjModel sTest = new ObjModel(Identifier.of("methane","sphere"), ObjModel.vertexBehaviour.TRIANGLES,false);


    public void renderAsQuad(VertexConsumerProvider provider, MatrixStack matrices,float yaw){

        // framebuffer setup
        panelFramebuffer.initFbo(panelFramebuffer.textureWidth,panelFramebuffer.textureHeight,false); // should probably handle this in a better way
        panelFramebuffer.setClearColor(0.0f,0.0f,0.0f,0.0f);
        panelFramebuffer.clear(true);

        RenderSystem.viewport(0,0,panelFramebuffer.textureWidth,panelFramebuffer.textureHeight);

        // setup framebuffer
        HackRenderTarget(false);


        MatrixStack mtx2 = new MatrixStack();




        RenderSystem.backupProjectionMatrix();
        var renderStack = RenderSystem.getModelViewStack();

        renderStack.pushMatrix();
        renderStack.identity();
        RenderSystem.applyModelViewMatrix();

        var mtx = new Matrix4f().perspective(
                (70 * (float) (Math.PI / 180.0)),
                panelFramebuffer.textureWidth / (float) panelFramebuffer.textureHeight,
                0.05F,
                MinecraftClient.getInstance().gameRenderer.getFarPlaneDistance()
        );

        RenderSystem.setProjectionMatrix(mtx,VertexSorter.BY_Z);

        mtx2.peek().getPositionMatrix().scale(0.1f,0.1f,0.1f);
        mtx2.peek().getPositionMatrix().translate(-0.5f,-0.5f,0.0f);

        //sTest.renderModel(provider,mtx2,true,0,0,0);
        parentXRPanel.panelRenderCallback(provider,mtx2);

        var dc = new DrawContext(MinecraftClient.getInstance(),MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers());

        dc.fill(0,0,150,150,-1000,0xFFFFFF); // uhh... This has to happen or else nothing renders to the fbo

        RenderSystem.restoreProjectionMatrix();
        renderStack.popMatrix();

        RenderSystem.applyModelViewMatrix();

        NativeImage nativeImage = new NativeImage(panelFramebuffer.textureWidth, panelFramebuffer.textureHeight, false);
        RenderSystem.bindTexture(panelFramebuffer.getColorAttachment());
        nativeImage.loadFromTextureImage(0, true);
        nativeImage.mirrorVertically();

        // get our texture before being rid of the framebuffer
        MinecraftClient.getInstance().getTextureManager().registerTexture(FRAMEBUFFER_ID,new NativeImageBackedTexture(nativeImage));

        panelFramebuffer.delete();

        HackRenderTarget(true);

        drawOutputAsQuad(matrices,yaw); // keep for now as a debug
    }

    public void drawOutputAsQuad(MatrixStack matrices,float yaw){
        var dc = new DrawContext(MinecraftClient.getInstance(),MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers());

        dc.getMatrices().loadIdentity();
        dc.getMatrices().multiplyPositionMatrix(matrices.peek().getPositionMatrix());
        dc.getMatrices().peek().getPositionMatrix().rotateY(-(yaw * MathHelper.RADIANS_PER_DEGREE)).translate(-1.25f,0.75f,1.5f).scale(0.005f);
        dc.drawTexture(FRAMEBUFFER_ID,0,0,0,0, panelFramebuffer.textureWidth,panelFramebuffer.textureHeight,panelFramebuffer.textureWidth,panelFramebuffer.textureHeight);

    }

    public void HackRenderTarget(boolean reset){

        if (reset){

            // this way we can hack it to use the implementation from RenderPhaseMixin, by referencing a subset of the class.
            ((RenderPhaseGetter) RenderPhase.MAIN_TARGET).setTarget( new RenderPhase.Target("main_target", () -> {
            }, () -> {
            }));


            MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        }else {

            ((RenderPhaseGetter) RenderPhase.MAIN_TARGET).setTarget(new RenderPhase.Target(FRAMEBUFFER_ID.toString(), () -> {
                panelFramebuffer.beginWrite(false);
            }, () -> {
                MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
            }));

        }


    }




}
