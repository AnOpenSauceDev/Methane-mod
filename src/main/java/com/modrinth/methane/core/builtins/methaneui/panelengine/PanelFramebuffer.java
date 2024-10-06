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
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

public class PanelFramebuffer {

    // use `beginWrite` and `endWrite`. The former take over render calls and the latter to restore render output to Minecraft's Framebuffer.
    SimpleFramebuffer panelFramebuffer = new SimpleFramebuffer(640,480,true,true);

    RenderLayer layer = RenderLayer.of("temporaryrenderer",VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLES,158382, RenderLayer.MultiPhaseParameters.builder().program(new RenderPhase.ShaderProgram(GameRenderer::getPositionColorProgram)).transparency(RenderPhase.Transparency.NO_TRANSPARENCY).build(false));
    RenderLayer layer2 = RenderLayer.of("temporaryrenderer2",VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS,158382, RenderLayer.MultiPhaseParameters.builder().transparency(RenderPhase.NO_TRANSPARENCY).program(new RenderPhase.ShaderProgram(GameRenderer::getPositionTexProgram)).build(false));

    public static ObjModel sTest = new ObjModel(Identifier.of("methane","sphere"), ObjModel.vertexBehaviour.TRIANGLES,false);


    public void renderAsQuad(VertexConsumerProvider provider, MatrixStack matrices,float yaw){

        // framebuffer setup
        panelFramebuffer.initFbo(640,480,false); // should probably handle this in a better way
        panelFramebuffer.setClearColor(0.5f,0.5f,0.5f,0.0f);
        panelFramebuffer.clear(true);

        RenderSystem.viewport(0,0,panelFramebuffer.textureWidth,panelFramebuffer.textureHeight);

        // setup framebuffer

        //panelFramebuffer.beginWrite(true);
        HackRenderTarget(false);


        // do crap

        MatrixStack mtx2 = new MatrixStack();



        mtx2.loadIdentity();

        RenderSystem.backupProjectionMatrix();
        var renderStack = RenderSystem.getModelViewStack();

        renderStack.pushMatrix();
        renderStack.identity();
        RenderSystem.applyModelViewMatrix();

        var mtx = new Matrix4f().perspective(
                (float)(70 * (float) (Math.PI / 180.0)),
                panelFramebuffer.textureWidth / (float) panelFramebuffer.textureHeight,
                0.05F,
                MinecraftClient.getInstance().gameRenderer.getFarPlaneDistance()
        );

        RenderSystem.setProjectionMatrix(mtx,VertexSorter.BY_Z);

        mtx2.peek().getPositionMatrix().scale(0.1f,0.1f,0.1f);
        mtx2.peek().getPositionMatrix().translate(-0.5f,-0.5f,0.0f);

        sTest.renderModel(provider,mtx2,true,0,0,0);

        var dc = new DrawContext(MinecraftClient.getInstance(),MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers());

        dc.fill(0,0,150,150,-1000,0xFFFFFF); // uhh... This has to happen or else nothing renders to the fbo

        RenderSystem.restoreProjectionMatrix();
        renderStack.popMatrix();

        RenderSystem.applyModelViewMatrix(); // revert so we don't rendering


        //RenderSystem.viewport(0,0,minecraftFramebuffer.textureWidth,minecraftFramebuffer.textureHeight);

        // uhh

        /*
        if(MinecraftClient.getInstance().player.isSneaking()) {
            ScreenshotRecorder.saveScreenshot(MinecraftClient.getInstance().runDirectory, panelFramebuffer, msg -> {
            });
        }

         */

        NativeImage nativeImage = new NativeImage(640, 480, false);
        RenderSystem.bindTexture(panelFramebuffer.getColorAttachment());
        nativeImage.loadFromTextureImage(0, true);
        nativeImage.mirrorVertically();

        // get our texture before being rid of the framebuffer
        MinecraftClient.getInstance().getTextureManager().registerTexture(Identifier.of("methane","panel_fb_x"),new NativeImageBackedTexture(nativeImage));

        panelFramebuffer.delete();


        HackRenderTarget(true);


        drawQuad(matrices,true);



        dc = new DrawContext(MinecraftClient.getInstance(),MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers());

        dc.getMatrices().loadIdentity();
        dc.getMatrices().multiplyPositionMatrix(matrices.peek().getPositionMatrix());
        dc.getMatrices().peek().getPositionMatrix().rotateY(-(yaw * MathHelper.RADIANS_PER_DEGREE)).translate(-1.25f,0.75f,1.5f).scale(0.005f);

        dc.drawText(MinecraftClient.getInstance().textRenderer,"rahh",0,0,0xFFFFFF,true);
        dc.drawTexture(Identifier.of("methane","panel_fb_x"),0,0,0,0,640,480,640,480);




    }

    public void drawQuad(MatrixStack matrices,boolean debug){



        if(debug){

            RenderSystem.setShader(GameRenderer::getPositionColorProgram);

            BufferBuilder screen = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS,VertexFormats.POSITION_COLOR);

            float zPos = 0;

            screen.vertex(matrices.peek().getPositionMatrix(),1.0f,2,zPos).color(1.0f,1.0f,0,0); // top left
            screen.vertex(matrices.peek().getPositionMatrix(),1.0f,-0f,zPos).color(1.0f,1.0f,0,0); // bottom left
            screen.vertex(matrices.peek().getPositionMatrix(),-1.0f,-0f,zPos).color(1.0f,1.0f,0,0); // bottom right
            screen.vertex(matrices.peek().getPositionMatrix(),-1.0f,2f,zPos).color(1.0f,1.0f,0,0); // top right

            // backface

            screen.vertex(matrices.peek().getPositionMatrix(),-1.0f,2f,zPos).color(1.0f,1.0f,0,0); // top right
            screen.vertex(matrices.peek().getPositionMatrix(),-1.0f,-0f,zPos).color(1.0f,1.0f,0,0); // bottom right
            screen.vertex(matrices.peek().getPositionMatrix(),1.0f,-0f,zPos).color(1.0f,1.0f,0,0); // bottom left
            screen.vertex(matrices.peek().getPositionMatrix(),1.0f,2,zPos).color(1.0f,1.0f,0,0); // top left

            var b = screen.end();

            BufferRenderer.draw(b);
            b.close();

            return;
        }

        BufferBuilder screen = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS,VertexFormats.POSITION_TEXTURE);

        float zPos = 0;

        screen.vertex(matrices.peek().getPositionMatrix(),1.0f,2,zPos).texture(0,0); // top left
        screen.vertex(matrices.peek().getPositionMatrix(),1.0f,-0f,zPos).texture(0,1); // bottom left
        screen.vertex(matrices.peek().getPositionMatrix(),-1.0f,-0f,zPos).texture(1,1); // bottom right
        screen.vertex(matrices.peek().getPositionMatrix(),-1.0f,2f,zPos).texture(1,0); // top right

        // backface

        screen.vertex(matrices.peek().getPositionMatrix(),-1.0f,2f,zPos).texture(1,0); // top right
        screen.vertex(matrices.peek().getPositionMatrix(),-1.0f,-0f,zPos).texture(1,1); // bottom right
        screen.vertex(matrices.peek().getPositionMatrix(),1.0f,-0f,zPos).texture(0,1); // bottom left
        screen.vertex(matrices.peek().getPositionMatrix(),1.0f,2,zPos).texture(0,0); // top left

        var b = screen.end();

        BufferRenderer.draw(b);
        b.close();

    }

    public void HackRenderTarget(boolean reset){

        if (reset){

            // this way we can hack it to use the implementation from RenderPhaseMixin, by referencing a subset of the class.
            ((RenderPhaseGetter) RenderPhase.MAIN_TARGET).setTarget( new RenderPhase.Target("main_target", () -> {
            }, () -> {
            }));


            MinecraftClient.getInstance().getFramebuffer().beginWrite(true);

        }else {

            ((RenderPhaseGetter) RenderPhase.MAIN_TARGET).setTarget(new RenderPhase.Target("throwaway_panel", () -> {
                panelFramebuffer.beginWrite(false);
            }, () -> {
                MinecraftClient.getInstance().getFramebuffer().beginWrite(false);
            }));

        }


    }




}
