package com.modrinth.methane.core.builtins.methaneui;

import com.modrinth.methane.client.objlib.ObjModel;
import com.modrinth.methane.util.Helpers;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.modrinth.methane.core.builtins.methaneui.MethaneMenu.MoleculeRenderer.SubWindow.windowRenderList;

public class MethaneMenu  extends Screen {

    public static Logger menuLogger = LoggerFactory.getLogger("Methane Logger");

    public MethaneMenu(Text title) {
        super(title);
    }

    @Override
    public void init(){

    }

    public static Vector2i screenDimensions = new Vector2i(0);

    public Vector2i updateScreenDimensions(){
        if(client != null){
            return screenDimensions = new Vector2i(client.getWindow().getScaledWidth(),client.getWindow().getScaledHeight());
        }else {
            return new Vector2i(0); // probably will never be hit, but I want to make the checker shut up
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta){
        updateScreenDimensions();
        MoleculeRenderer.RenderMenu(context, mouseX, mouseY);
    }

    public static HashMap<MoleculeRenderer.SubWindow,Vector4i> titleBoundsMap = new HashMap<>();
    public static HashMap<MoleculeRenderer.SubWindow,Vector4i> contentBoundsMap = new HashMap<>();

    public static class MoleculeRenderer{



        public static ObjModel sphere = new ObjModel(Identifier.of("methane","sphere"), ObjModel.vertexBehaviour.TRIANGLES,false);

        static MoleculeWindow moleculeWindow = new MoleculeWindow("Methane - menu");
        static ConfigWindow configWindow = new ConfigWindow("Methane Config");


        public static void RenderMenu(DrawContext context,int mouseX, int mouseY){

            context.fill(0,0,screenDimensions.x,screenDimensions.y,-1000,0xFF1e1e28);

            windowRenderList.forEach(subWindow -> {
                if(subWindow == null) return;

                if(!subWindow.killed) {
                    subWindow.render(context, mouseX, mouseY);
                }else {
                    // if we don't do this, GC will become a problem
                    menuLogger.info("killing window instance {}", subWindow.title);
                    titleBoundsMap.remove(subWindow);
                    contentBoundsMap.remove(subWindow);
                    subWindow = null;
                }
            });

            windowRenderList.removeIf(Objects::isNull);



        }

        public static class ConfigWindow extends SubWindow{

            public static class ConfigButton{

                public ConfigButton(Vector4i bounds, Vector2i pos, boolean state){
                    this.clickBounds = bounds;
                    this.pos = pos;
                    this.isChecked = state;
                }

                public void updatePos(Vector2i newPos){
                    this.pos = newPos;
                    this.clickBounds = new Vector4i(pos.x + 10, pos.y + 10, pos.x + 37,pos.y + 19);
                }

                Vector4i clickBounds;
                Vector2i pos;
                boolean isChecked;

                public Vector4i getLocalizedBounds(){
                    return new Vector4i(clickBounds.x - pos.x, clickBounds.y - pos.y, clickBounds.z - pos.x, clickBounds.w - pos.y);
                }

                public void RenderToggle(DrawContext context){
                    context.drawText(MinecraftClient.getInstance().textRenderer,"CONFIG_VALUE",pos.x + 40, pos.y + 10, 0xFFFFFF,true);
                    context.drawTexture(Identifier.of("methane",(isChecked) ? "customtextures/slider.png" : "customtextures/slideroff.png"), pos.x + 10, pos.y + 10,0,0,138/8,70/8,138/8,70/8);
                }

            }

            ConfigButton button;

            public ConfigWindow(String title) {
                super(title);
                pos = new Vector2i(100,50);
                button = new ConfigButton(new Vector4i(pos.x + 10, pos.y + 10, pos.x + 27,pos.y + 9),pos,false);
                buttonList.add(button);
            }

            public List<ConfigButton> buttonList = new ArrayList<>();

            @Override
            public void onClicked(Vector2i point, boolean closing) {
                for (ConfigButton buttonInst : buttonList){

                    menuLogger.info("err: {},{}",point.toString(NumberFormat.getIntegerInstance()),buttonInst.getLocalizedBounds().toString(NumberFormat.getIntegerInstance()));
                    if(Helpers.withinBounds(point,buttonInst.getLocalizedBounds())){
                        buttonInst.isChecked = !buttonInst.isChecked;
                    }
                }
            }

            @Override
            public void onRender(DrawContext context, int mouseX, int mouseY) {
                for(ConfigButton b : buttonList){
                    b.updatePos(pos);
                    b.RenderToggle(context);
                }
            }



        }

        public static class MoleculeWindow extends SubWindow{

            public MoleculeWindow(String title) {
                super(title);
            }

            @Override
            public void onClicked(Vector2i point, boolean closing) {
                 return;
            }

            int spinX = 0,spinY = 0;

            float spinLightY = 0.5f; // -1f <-> 1f
            float spinLightX = -1f; // -1f <-> 1f
            float spinLightZ = 0.5f; // -1f <-> 1f

            @Override
            public void onRender(DrawContext context, int mouseX, int mouseY) {

                RenderSystem.disableCull();
                RenderSystem.enableDepthTest();

                MatrixStack stack = context.getMatrices();
                stack.push();

                stack.translate(pos.x + bounds.x/2,pos.y + bounds.y/2,5 );
                stack.scale(10,10,10);

                var handle = MinecraftClient.getInstance().getWindow().getHandle();

                var mx = (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.input.sneaking) ? mouseX: spinX;
                var my = (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.input.sneaking) ? mouseY: spinY;

                if(MinecraftClient.getInstance().player != null){
                    spinX += (InputUtil.isKeyPressed(handle,InputUtil.GLFW_KEY_A)) ? 1 : 0;
                    spinX -= (InputUtil.isKeyPressed(handle,InputUtil.GLFW_KEY_D))? 1 : 0;

                    spinY += (InputUtil.isKeyPressed(handle,InputUtil.GLFW_KEY_W)) ? 1 : 0;
                    spinY -= (InputUtil.isKeyPressed(handle,InputUtil.GLFW_KEY_S)) ? 1 : 0;

                    spinLightY += (InputUtil.isKeyPressed(handle,InputUtil.GLFW_KEY_UP)) ? 0.05f : 0;
                    spinLightY -= (InputUtil.isKeyPressed(handle,InputUtil.GLFW_KEY_DOWN)) ? 0.05f: 0;

                    spinLightX += (InputUtil.isKeyPressed(handle,InputUtil.GLFW_KEY_LEFT)) ? 0.05f : 0;
                    spinLightX -= (InputUtil.isKeyPressed(handle,InputUtil.GLFW_KEY_RIGHT)) ? 0.05f: 0;

                    spinLightZ += (InputUtil.isKeyPressed(handle,InputUtil.GLFW_KEY_COMMA)) ? 0.05f : 0;
                    spinLightZ -= (InputUtil.isKeyPressed(handle,InputUtil.GLFW_KEY_PERIOD)) ? 0.05f: 0;

                    if(Math.abs(spinLightY) > 1){
                        spinLightY = MathHelper.clamp(spinLightY,-1f,1f);
                    }
                }

                stack.push();

                stack.peek().getPositionMatrix().translate(new Vector3f(sphere.SunDirection).mul(-5)).translate(0,0,-1);

                stack.pop();

                stack.push();

                stack.peek().getPositionMatrix().scale(1,1,0.001f);

                sphere.renderModel(context.getVertexConsumers(),context.getMatrices(),true,(mx * MathHelper.RADIANS_PER_DEGREE) * 0.01f,(my * MathHelper.RADIANS_PER_DEGREE) * 0.01f,0);

                stack.pop();

                sphere.SunDirection = new Vector3f(spinLightX, spinLightY,spinLightZ).normalize();




                stack.pop();



            }


        }



        public abstract static class SubWindow{

            static List<SubWindow> windowRenderList = new ArrayList<>();


            public String title;
            public boolean dragging = false;
            public boolean killed = false;

            Vector2i pos = new Vector2i(50,50); // top left
            Vector2i bounds = new Vector2i(120);
            Vector2i intersectPoint = new Vector2i(0);

            public SubWindow(String title){
                this.title = title;
                updateBounds();
                windowRenderList.add(this);
            }

            public void updateBounds(){
                titleBoundsMap.put(this,new Vector4i(pos.x - 1, pos.y - 12, pos.x + bounds.x, pos.y));
                contentBoundsMap.put(this,new Vector4i(pos.x, pos.y, pos.x + bounds.x, pos.y + bounds.y));
            }

            public void handleClick(Vector2i intersectPoint,boolean onCloseButton,boolean titleBar){
                // stuff for window buttons, todo later

                onClicked(intersectPoint,onCloseButton);

                if(titleBar){
                    if(onCloseButton){
                        killed = true;
                    }

                    // drag itself
                    if(!isDraggingMethaneWindow){
                        isDraggingMethaneWindow = true;
                        dragging = true;
                        this.intersectPoint = intersectPoint;
                    }else if(dragging){
                        dragging = false;
                        isDraggingMethaneWindow = false;
                    }
                }
            }

            public abstract void onClicked(Vector2i point,boolean closing);

            public void render(DrawContext context,int mouseX, int mouseY){

                if(dragging){
                    this.pos = new Vector2i(mouseX,mouseY).sub(intersectPoint.x,-5);
                }

                updateBounds();

                var mtx = context.getMatrices();
                mtx.push();

                if(dragging) {
                    mtx.translate(0, 0, 50);
                }

                // bg highlight 2
                context.fill(pos.x - 1,pos.y,pos.x + bounds.x + 1, pos.y + bounds.y + 1,-25,(dragging) ? 0xFF5f958d : 0xFF494852);

                // titlebar highlight
                context.fillGradient(pos.x - 1,pos.y - 13,pos.x + bounds.x + 1, pos.y,-10,(dragging) ? 0xFF7fB5Ad  : 0xFF5f958d,(dragging) ? 0xFF5f958d: 0xFF494852);

                // titlebar
                context.fill(pos.x,pos.y - 12,pos.x + bounds.x, pos.y - 1,-10,0xFF3f738d);

                // background fill
                context.fill(pos.x,pos.y,pos.x + bounds.x, pos.y + bounds.y,-25,0xFF323242);

                context.drawText(MinecraftClient.getInstance().textRenderer, title,pos.x + 1,pos.y - 11, 0xFFFFFFFF,false);

                context.drawTexture(Identifier.of("methane","customtextures/cross.png"),(pos.x + bounds.x) - 9,pos.y - 11,0,0 , 8,8,8,8 );



                onRender(context,mouseX,mouseY);

                mtx.pop();
            }

            public abstract void onRender(DrawContext context, int mouseX, int mouseY);

        }



    }

    static boolean isDraggingMethaneWindow = false;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean res = super.mouseClicked(mouseX,mouseY,button);

        Vector2d mouse = new Vector2d(mouseX,mouseY);

        titleBoundsMap.forEach((key,bound) ->{


            //mouse.x > bound.x && mouse.y > bound.y && mouse.x < bound.z && mouse.y < bound.w
            // within the box, xy = top left, zw = bottom right
            if(Helpers.withinBounds(mouse, bound.x, bound.y,bound.z,bound.w)){
                Vector2i intersect = new Vector2i(new Vector2i((int) (mouse.x - bound.x), (int) (mouse.y - bound.y)));
                key.handleClick(intersect,false,true);

                if(mouse.x > bound.z - 9 && mouse.y > bound.y + 1 && mouse.x < bound.z && mouse.y < bound.w ){
                    key.handleClick(intersect,true,true);
                }
            }

        });

        contentBoundsMap.forEach((key,bound) ->{


            //mouse.x > bound.x && mouse.y > bound.y && mouse.x < bound.z && mouse.y < bound.w
            // within the box, xy = top left, zw = bottom right
            if(Helpers.withinBounds(mouse, bound.x, bound.y,bound.z,bound.w)){
                Vector2i intersect = new Vector2i(new Vector2i((int) (mouse.x - bound.x), (int) (mouse.y - bound.y)));
                key.handleClick(intersect,false,false);
            }

        });

        return res;
    }

}
