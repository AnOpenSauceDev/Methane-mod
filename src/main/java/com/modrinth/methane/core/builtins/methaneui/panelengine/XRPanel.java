package com.modrinth.methane.core.builtins.methaneui.panelengine;


import com.modrinth.methane.client.objlib.ObjModel;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

// A 3D-rendered framebuffer panel, essentially an app.
public abstract class XRPanel {

    public XRPanel(Identifier identifier,boolean resizable, boolean decorated, int x, int y){
        XR_PANEL_ID = identifier;
        framebuffer = new PanelFramebuffer(this, resizable, x, y);
        this.x = x;
        this.y = y;
        this.decorated = decorated;
    }

    boolean resizable,decorated;
    int x,y;

    public Identifier XR_PANEL_ID;

    PanelFramebuffer framebuffer;

    public void renderFramebuffer(VertexConsumerProvider provider,MatrixStack stack,float yaw){
        framebuffer.renderAsQuad(provider,stack,yaw);
    }

    public static ObjModel sTest = new ObjModel(Identifier.of("methane","abomination"), ObjModel.vertexBehaviour.AUTO_DETECT,false);

    float p1 = 0,y1 = 0;

    RenderLayer FIX_BG_HACK = RenderLayer.of("xr_hack", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS,1024, RenderLayer.MultiPhaseParameters.builder().build(true));

    public void panelRenderCallback(VertexConsumerProvider provider, MatrixStack viewportMatrices){


        DrawContext context = new DrawContext(MinecraftClient.getInstance(),MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers());
        var mtx = context.getMatrices();
        mtx.push();
        mtx.peek().getPositionMatrix().set(viewportMatrices.peek().getPositionMatrix());

        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        mtx.translate(0,0,-15);

        /*
        var buf = provider.getBuffer(FIX_BG_HACK);

        buf.vertex(mtx.peek(),500,50,-1).color(0xFFBBBBBB); // t-left
        buf.vertex(mtx.peek(),500,-100,-1).color(0xFFBBBBBB); // b-left
        buf.vertex(mtx.peek(),-1,-100,-1).color(0xFFBBBBBB);  // b-right
        buf.vertex(mtx.peek(),-1,50,-1).color(0xFFBBBBBB);  // t-right



        buf = null; // KILL


         */

        sTest.renderModel(provider,mtx,true,y1 += 0.000025f,p1 += 0.00001f,0);

        mtx.translate(-2.5f,1,0);

        sTest.renderModel(provider,mtx,true,y1 += 0.000023f,p1 += 0.0000095f,0);

        if (decorated){
            /*
            context.fill(0,0,x,10,-3,0xFF494852);
            context.fill(1,1,x - 1,9,-3,0xFF3f738d);

            context.fill(0,0,x,y,-3,0xFFFFFF);

             */
        }

        mtx.pop();

        onRender(viewportMatrices, context);

    }

    public abstract void onRender(MatrixStack viewportMatrices, DrawContext context);

}
