package com.modrinth.methane.client.objlib;

import com.modrinth.methane.client.ShaderInst;
import net.fabricmc.fabric.impl.client.rendering.FabricShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.navigation.NavigationDirection;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A very <s>passive</s> <b>aggressive-aggressive</b> OBJ parser that I hacked together due to some form of insanity. While it implements the OBJ standard mostly,
 * this is NOT a good example of how OBJ works, or how to render geometry in minecraft. Everything is done in an extreme way here.
 * <br></br>
 * Takes an OBJ file, and <h1 style="font-size: 50px;">emits crap</h1>
 * (muhahaha style injection)
 */
public class ObjModel {

    public static final Logger LOGGER = LoggerFactory.getLogger("OBJ-LOGGER");

    // 4 is unused, unless in quad mode.

    public enum vertexBehaviour{

        AUTO_DETECT,
        TRIANGLES, // used via omission
        QUADS

    }

    boolean quadsInsteadOfTriangles = false;

    public static final Identifier FALLBACK = Identifier.of("custom_obj","fallback.png");
    public static final Identifier DEBUG_SELECTED = Identifier.of("custom_obj","selected.png");
    public Identifier ModelTexture;

    // can be of a keyframe, probably fed via something else though.
    ObjModel InterpolationModel;


    public NativeImageBackedTexture setModelTexture(Identifier identifier){

        Identifier fileID = Identifier.of(identifier.getNamespace(),identifier.getPath() + "/texture.png");

        try{
            // todo: allow user to supply a URL.
        //InputStream stream = new URI("file:///tmp/mb.png").toURL().openStream();

            var texture = NativeImage.read(MinecraftClient.getInstance().getResourceManager().open(fileID));

             var nibtx = new NativeImageBackedTexture(texture);

             MinecraftClient.getInstance().getTextureManager().registerTexture(identifier,nibtx);

             ModelTexture = identifier;

             return nibtx;

        }catch (Exception e){

            e.printStackTrace();

            return null;

        }
    }

    public void setupDevTextures(){
        // do later for animator
    }



    public static final boolean TRANSLUCENCY_ENABLED = false;

    public BiFunction<Identifier, RenderPhase.ShaderProgram, RenderLayer> objTriangleLayer = Util.memoize((textureID,shaderProgram) ->{

         RenderPhase.Texture texture2 = new RenderPhase.Texture(textureID,false,true);

         RenderLayer.MultiPhaseParameters parameters = RenderLayer.MultiPhaseParameters.builder().program(shaderProgram).transparency(RenderPhase.Transparency.TRANSLUCENT_TRANSPARENCY).lightmap(RenderPhase.ENABLE_LIGHTMAP).overlay(RenderPhase.DISABLE_OVERLAY_COLOR).texture(texture2).build(true);


         return RenderLayer.of("obj", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.TRIANGLES,
                 4194304,true,TRANSLUCENCY_ENABLED,parameters);

        }

    );

    public BiFunction<Identifier,RenderPhase.ShaderProgram,RenderLayer> objQuadLayer = Util.memoize( (textureID, shaderProgram) ->{

                RenderPhase.Texture texture2 = new RenderPhase.Texture(textureID,false,true);

                RenderLayer.MultiPhaseParameters parameters = RenderLayer.MultiPhaseParameters.builder().program(shaderProgram).transparency(RenderPhase.Transparency.TRANSLUCENT_TRANSPARENCY).lightmap(RenderPhase.ENABLE_LIGHTMAP).overlay(RenderPhase.DISABLE_OVERLAY_COLOR).texture(texture2).build(true);


                return RenderLayer.of("obj", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL, VertexFormat.DrawMode.QUADS,
                        4194304,true,TRANSLUCENCY_ENABLED,parameters);

                }

    );

    static RenderLayer.MultiPhaseParameters parameters_dbg = RenderLayer.MultiPhaseParameters.builder().program(RenderPhase.SOLID_PROGRAM).transparency(RenderPhase.Transparency.NO_TRANSPARENCY).lightmap(RenderPhase.DISABLE_LIGHTMAP).overlay(RenderPhase.DISABLE_OVERLAY_COLOR).build(true);


    public static RenderLayer lineDebug = RenderLayer.of("obj", VertexFormats.POSITION, VertexFormat.DrawMode.DEBUG_LINES,
                        4194304,false,false,parameters_dbg);





    vertexBehaviour selectedBehaviour;
    boolean doBackface;


    /**
     * Takes an Identifier, and grabs the texture + model needed.
     * @param path an ID path. Should read as "mod_id", ~~"objmodel/[any extra directories]/[model name]"~~. From there we'll grab from ~~"objmodel/[model name]~~/model.obj" and ~~"objmodel/[model name]~~/texture.png".
     * @param behaviour
     * @param drawBackFace
     */
    public ObjModel(Identifier path, vertexBehaviour behaviour, boolean drawBackFace) {

        doBackface = drawBackFace;
        selectedBehaviour = behaviour;

        setModelTexture(path);
        //setModelTexture(FALLBACK); // who needs a backup?

        try{
            ReadMtlFile(path);
            ReadObjFile(path);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public ObjModel(Identifier path, vertexBehaviour behaviour, boolean drawBackFace, @NotNull ObjModel interp) {

        InterpolationModel = interp;

        doBackface = drawBackFace;
        selectedBehaviour = behaviour;

        setModelTexture(FALLBACK);

        try{
            ReadMtlFile(path); // we need our materials first!
            ReadObjFile(path);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public RenderPhase.ShaderProgram program = RenderPhase.SOLID_PROGRAM;

    public void SetShaderProgram(ShaderInst setProgram){
        this.program = new RenderPhase.ShaderProgram(setProgram::getProgram);
    }

    // somehow the absolute jank I wrote a week or so ago is stupidly fast enough for me to not bother optimizing yet.
    public void ReadObjFile(Identifier path) throws IOException {

        Identifier fileID = Identifier.of(path.getNamespace(),path.getPath() + "/model.obj");

        BufferedReader reader = new BufferedReader(new InputStreamReader(MinecraftClient.getInstance().getResourceManager().open(fileID)));
        String line = reader.readLine();



        parseVertex(line);


        while (line != null) {
            line = reader.readLine();
            parseVertex(line);
        }

    }

    public void ReadMtlFile(Identifier path) throws IOException {

        Identifier fileID = Identifier.of(path.getNamespace(),path.getPath() + "/model.mtl");

        BufferedReader reader = new BufferedReader(new InputStreamReader(MinecraftClient.getInstance().getResourceManager().open(fileID)));
        String line = reader.readLine();



        parseMaterial(line);


        while (line != null) {
            line = reader.readLine();
            parseMaterial(line);
        }

    }

    Material workingMaterial = new Material();

    HashMap<String,Material> materialMap = new HashMap<>();

    public void parseMaterial(String line){

        if(line == null){return;}

        String[] elements = line.split(" ");

        // could be a switch, but eh
        if(elements[0].equals("newmtl")){

            materialMap.put(elements[1],workingMaterial); // put material as name, it's a pointer so it's ok

            if(workingMaterial.name == null){
                workingMaterial.name = elements[1];
            }else {
                workingMaterial = new Material();
                workingMaterial.name = elements[1];
            }
        }

        if(elements[0].equals("Kd")){ // diffuse colour
            workingMaterial.colour = new Vector3f(Float.parseFloat(elements[1]),Float.parseFloat(elements[2]),Float.parseFloat(elements[3]));
        }

        if(elements[0].equals("d")){
            workingMaterial.alpha = Float.parseFloat(elements[1]);
        }

        if(elements[0].equals("Ns")){
            workingMaterial.specular = Float.parseFloat(elements[1]) * 0.01f;
        }

    }

    public static class Material{

        public Vector3f colour = new Vector3f(1);
        public float alpha = 1.0f; //
        public float specular = 1.0f; //
        public String name = "blank";

    }


    public Identifier getIDOrFallback(){
        if(ModelTexture == null){
            return FALLBACK;
        }else {
            return ModelTexture;
        }
    }

    TextureManager manager = MinecraftClient.getInstance().getTextureManager();

    float delta = 0;

    public Vector3f SunDirection = new Vector3f(0.5f,-1,0.5f).normalize(); // the fake light point's direction

    public static final Material FALLBACK_MATERIAL = new Material();

    float shine = 2f;

    public int shadeWithLight(Vector3f normal, Material material){

        if(material == null){
            material = FALLBACK_MATERIAL;
        }

        Vector3f between = new Vector3f(0,0,1).add(SunDirection).normalize();
        float specular = (float) Math.pow(Math.max(0,new Vector3f(normal).dot(between)),material.specular);

        int lightVal = (int) ((new Vector3f(normal).dot(SunDirection) + specular) * 100);
        lightVal = Math.max(lightVal, 0);
        return lightVal;
    }

    int light = 240;

    public void renderModel(VertexConsumerProvider provider, MatrixStack stack, boolean shading,float yaw,float pitch,int customLightFlags){

        if(customLightFlags > 16) {
            light = customLightFlags;
        }


        stack.push();

        Matrix4f entry;



        entry = stack.peek().getPositionMatrix();

        var radsX = MathHelper.DEGREES_PER_RADIAN * pitch;

        entry.rotateX(radsX);

        var rads = MathHelper.DEGREES_PER_RADIAN * yaw;

        entry.rotateY(rads);






        if(quadsInsteadOfTriangles){



            VertexConsumer consumer;


            consumer = provider.getBuffer(objQuadLayer.apply(getIDOrFallback(),program));



            manager.bindTexture(getIDOrFallback());


            if(InterpolationModel == null) {

                for(int x = 0; x < quads.size(); x++) {

                    OBJQuad objQuad = quads.get(x);


                    var material = objQuad.selectedMaterial;
                    if(material == null){ material = new Material();}

                    if(customLightFlags == 1){
                        material = new Material();
                        material.alpha = 1.0f;
                        material.colour = new Vector3f(0);
                    }


                    consumer.vertex(entry, objQuad.vertex1.x, objQuad.vertex1.y, objQuad.vertex1.z).color(material.colour.x, material.colour.y, material.colour.z, material.alpha).normal(objQuad.vertex1Normal.x, objQuad.vertex1Normal.y, objQuad.vertex1Normal.z).texture(objQuad.vertex1Texture.x, -objQuad.vertex1Texture.y).light((shading) ? 0 : light);
                    consumer.vertex(entry, objQuad.vertex2.x, objQuad.vertex2.y, objQuad.vertex2.z).color(material.colour.x, material.colour.y, material.colour.z, material.alpha).normal(objQuad.vertex2Normal.x, objQuad.vertex2Normal.y, objQuad.vertex2Normal.z).texture(objQuad.vertex2Texture.x, -objQuad.vertex2Texture.y).light((shading) ? 0 : light);
                    consumer.vertex(entry, objQuad.vertex3.x, objQuad.vertex3.y, objQuad.vertex3.z).color(material.colour.x, material.colour.y, material.colour.z, material.alpha).normal(objQuad.vertex3Normal.x, objQuad.vertex3Normal.y, objQuad.vertex3Normal.z).texture(objQuad.vertex3Texture.x, -objQuad.vertex3Texture.y).light((shading) ? 0 : light);
                    consumer.vertex(entry, objQuad.vertex4.x, objQuad.vertex4.y, objQuad.vertex4.z).color(material.colour.x, material.colour.y, material.colour.z, material.alpha).normal(objQuad.vertex4Normal.x, objQuad.vertex4Normal.y, objQuad.vertex3Normal.z).texture(objQuad.vertex4Texture.x, -objQuad.vertex4Texture.y).light((shading) ? 0 : light);

                    if (doBackface) {
                        consumer.vertex(entry, objQuad.vertex4.x, objQuad.vertex4.y, objQuad.vertex4.z).color(material.colour.x, material.colour.y, material.colour.z, material.alpha).normal(objQuad.vertex4Normal.x, objQuad.vertex4Normal.y, objQuad.vertex3Normal.z).texture(objQuad.vertex4Texture.x, -objQuad.vertex4Texture.y).light(light);
                        consumer.vertex(entry, objQuad.vertex3.x, objQuad.vertex3.y, objQuad.vertex3.z).color(material.colour.x, material.colour.y, material.colour.z, material.alpha).normal(objQuad.vertex3Normal.x, objQuad.vertex3Normal.y, objQuad.vertex3Normal.z).texture(objQuad.vertex3Texture.x, -objQuad.vertex3Texture.y).light(light);
                        consumer.vertex(entry, objQuad.vertex2.x, objQuad.vertex2.y, objQuad.vertex2.z).color(material.colour.x, material.colour.y, material.colour.z, material.alpha).normal(objQuad.vertex2Normal.x, objQuad.vertex2Normal.y, objQuad.vertex2Normal.z).texture(objQuad.vertex2Texture.x, -objQuad.vertex2Texture.y).light(light);
                        consumer.vertex(entry, objQuad.vertex1.x, objQuad.vertex1.y, objQuad.vertex1.z).color(material.colour.x, material.colour.y, material.colour.z, material.alpha).normal(objQuad.vertex1Normal.x, objQuad.vertex1Normal.y, objQuad.vertex1Normal.z).texture(objQuad.vertex1Texture.x, -objQuad.vertex1Texture.y).light(light);
                    }

                }
            }

        }else
        {

            var layer = objTriangleLayer.apply(getIDOrFallback(),program);

            VertexConsumer consumer = provider.getBuffer(layer);

            manager.bindTexture(getIDOrFallback());

            for(int x = 0; x < triangles.size(); x++){


                OBJTriangle objTriangle = triangles.get(x);

                var material = objTriangle.selectedMaterial;
                if(material == null){ material = new Material();}


                if(customLightFlags == 1){
                    material = new Material();
                    material.alpha = 1.0f;
                    material.colour = new Vector3f(0);
                }

                if(InterpolationModel == null) {



                    consumer.vertex(entry, objTriangle.vertex1.x, objTriangle.vertex1.y, objTriangle.vertex1.z).color(material.colour.x,material.colour.y,material.colour.z, material.alpha).normal(objTriangle.vertex1Normal.x, objTriangle.vertex1Normal.y, objTriangle.vertex1Normal.z).texture(objTriangle.vertex1Texture.x, -objTriangle.vertex1Texture.y).light((shading) ? shadeWithLight(new Vector3f(objTriangle.vertex1Normal).rotateY(rads).rotateX(radsX),objTriangle.selectedMaterial) : light);
                    consumer.vertex(entry, objTriangle.vertex2.x, objTriangle.vertex2.y, objTriangle.vertex2.z).color(material.colour.x,material.colour.y,material.colour.z, material.alpha).normal(objTriangle.vertex2Normal.x, objTriangle.vertex2Normal.y, objTriangle.vertex2Normal.z).texture(objTriangle.vertex2Texture.x, -objTriangle.vertex2Texture.y).light((shading) ? shadeWithLight(new Vector3f(objTriangle.vertex2Normal).rotateY(rads).rotateX(radsX),objTriangle.selectedMaterial) : light);
                    consumer.vertex(entry, objTriangle.vertex3.x, objTriangle.vertex3.y, objTriangle.vertex3.z).color(material.colour.x,material.colour.y,material.colour.z, material.alpha).normal(objTriangle.vertex3Normal.x, objTriangle.vertex3Normal.y, objTriangle.vertex3Normal.z).texture(objTriangle.vertex3Texture.x, -objTriangle.vertex3Texture.y).light((shading) ? shadeWithLight(new Vector3f(objTriangle.vertex3Normal).rotateY(rads).rotateX(radsX),objTriangle.selectedMaterial) : light);

                    if (doBackface) {

                        consumer.vertex(entry, objTriangle.vertex3.x, objTriangle.vertex3.y, objTriangle.vertex3.z).color(material.colour.x,material.colour.y,material.colour.z, material.alpha).normal(objTriangle.vertex3Normal.x, objTriangle.vertex3Normal.y, objTriangle.vertex3Normal.z).texture(objTriangle.vertex3Texture.x, -objTriangle.vertex3Texture.y).light((shading) ? 0 : light);

                        consumer.vertex(entry, objTriangle.vertex2.x, objTriangle.vertex2.y, objTriangle.vertex2.z).color(material.colour.x,material.colour.y,material.colour.z, material.alpha).normal(objTriangle.vertex2Normal.x, objTriangle.vertex2Normal.y, objTriangle.vertex2Normal.z).texture(objTriangle.vertex2Texture.x, -objTriangle.vertex2Texture.y).light((shading) ? 0 : light);

                        consumer.vertex(entry, objTriangle.vertex1.x, objTriangle.vertex1.y, objTriangle.vertex1.z).color(material.colour.x,material.colour.y,material.colour.z, material.alpha).normal(objTriangle.vertex1Normal.x, objTriangle.vertex1Normal.y, objTriangle.vertex1Normal.z).texture(objTriangle.vertex1Texture.x, -objTriangle.vertex1Texture.y).light((shading) ? 0 : light);

                    }

                }else {

                    // animation hack

                    consumer.vertex(entry, MathHelper.lerp(delta,objTriangle.vertex1.x,InterpolationModel.triangles.get(x).vertex1.x), MathHelper.lerp(delta,objTriangle.vertex1.y,InterpolationModel.triangles.get(x).vertex1.y), MathHelper.lerp(delta,objTriangle.vertex1.z,InterpolationModel.triangles.get(x).vertex1.z)).color(material.colour.x,material.colour.y,material.colour.z, material.alpha).normal(objTriangle.vertex1Normal.x, objTriangle.vertex1Normal.y, objTriangle.vertex1Normal.z).texture(objTriangle.vertex1Texture.x, -objTriangle.vertex1Texture.y).light(light);
                    consumer.vertex(entry, MathHelper.lerp(delta,objTriangle.vertex2.x,InterpolationModel.triangles.get(x).vertex2.x), MathHelper.lerp(delta,objTriangle.vertex2.y,InterpolationModel.triangles.get(x).vertex2.y), MathHelper.lerp(delta,objTriangle.vertex2.z,InterpolationModel.triangles.get(x).vertex2.z)).color(material.colour.x,material.colour.y,material.colour.z, material.alpha).normal(objTriangle.vertex2Normal.x, objTriangle.vertex2Normal.y, objTriangle.vertex2Normal.z).texture(objTriangle.vertex2Texture.x, -objTriangle.vertex2Texture.y).light(light);
                    consumer.vertex(entry, MathHelper.lerp(delta,objTriangle.vertex3.x,InterpolationModel.triangles.get(x).vertex3.x), MathHelper.lerp(delta,objTriangle.vertex3.y,InterpolationModel.triangles.get(x).vertex3.y), MathHelper.lerp(delta,objTriangle.vertex3.z,InterpolationModel.triangles.get(x).vertex3.z)).color(material.colour.x,material.colour.y,material.colour.z, material.alpha).normal(objTriangle.vertex3Normal.x, objTriangle.vertex3Normal.y, objTriangle.vertex3Normal.z).texture(objTriangle.vertex3Texture.x, -objTriangle.vertex3Texture.y).light(light);

                    if (doBackface) {

                        consumer.vertex(entry, MathHelper.lerp(delta,objTriangle.vertex3.x,InterpolationModel.triangles.get(x).vertex3.x), MathHelper.lerp(delta,objTriangle.vertex3.y,InterpolationModel.triangles.get(x).vertex3.y), MathHelper.lerp(delta,objTriangle.vertex3.z,InterpolationModel.triangles.get(x).vertex3.z)).color(material.colour.x,material.colour.y,material.colour.z, material.alpha).normal(objTriangle.vertex3Normal.x, objTriangle.vertex3Normal.y, objTriangle.vertex3Normal.z).texture(objTriangle.vertex3Texture.x, -objTriangle.vertex3Texture.y).light(light);
                        consumer.vertex(entry, MathHelper.lerp(delta,objTriangle.vertex2.x,InterpolationModel.triangles.get(x).vertex2.x), MathHelper.lerp(delta,objTriangle.vertex2.y,InterpolationModel.triangles.get(x).vertex2.y), MathHelper.lerp(delta,objTriangle.vertex2.z,InterpolationModel.triangles.get(x).vertex2.z)).color(material.colour.x,material.colour.y,material.colour.z, material.alpha).normal(objTriangle.vertex2Normal.x, objTriangle.vertex2Normal.y, objTriangle.vertex2Normal.z).texture(objTriangle.vertex2Texture.x, -objTriangle.vertex2Texture.y).light(light);
                        consumer.vertex(entry, MathHelper.lerp(delta,objTriangle.vertex1.x,InterpolationModel.triangles.get(x).vertex1.x), MathHelper.lerp(delta,objTriangle.vertex1.y,InterpolationModel.triangles.get(x).vertex1.y), MathHelper.lerp(delta,objTriangle.vertex1.z,InterpolationModel.triangles.get(x).vertex1.z)).color(material.colour.x,material.colour.y,material.colour.z, material.alpha).normal(objTriangle.vertex1Normal.x, objTriangle.vertex1Normal.y, objTriangle.vertex1Normal.z).texture(objTriangle.vertex1Texture.x, -objTriangle.vertex1Texture.y).light(light);

                    }



                }



            }

        }


        stack.pop();

    }


    Debug debug = new Debug(); // a tradeoff to organizing the debugger

    public class Debug{
        private float DISTANCE_LIMIT = 50.0f;

        public Collection<? extends Integer> debugPickFace(int mouseX, int mouseY, boolean forcedDepth, float depth, Matrix4f mat4f, DrawContext context,float sx, float sy){

            DISTANCE_LIMIT += (sx * sy);

            Vector3f coords = new Vector3f(mouseX,mouseY,1); // depth of 1 because I'm lazy.

            List<Float> values = new ArrayList<>();


            for(int x = 0; x < triangles.size(); x++) {

                OBJTriangle objTriangle = triangles.get(x);

                var midpoint = new Vector3f((objTriangle.vertex1.x + objTriangle.vertex2.x +objTriangle.vertex3.x)/3, (objTriangle.vertex1.y +objTriangle.vertex2.y +objTriangle.vertex3.y )/3, (objTriangle.vertex1.z + objTriangle.vertex2.z + objTriangle.vertex3.z)/3);



                //midpoint.add(pos);
                //midpoint.mul(scale);



                midpoint.mulProject(mat4f);



                if(!forcedDepth) {
                    coords.z = midpoint.z;
                }else {
                    coords.z = depth;
                }

                var dst = coords.distance(midpoint);

                if(dst > DISTANCE_LIMIT){
                    continue; // skip
                }

                values.add(dst);

            }

            List<Float> v2 =  new ArrayList<>();

            for(float value : values){
                v2.add(value);
            }

            Collections.sort(v2);
            v2 = v2.subList(0,Math.min(v2.size(),11));

            if(v2.isEmpty()){
                return new ArrayList<>(); // empty to save us
            }

            List<Integer> last = new ArrayList<>();

            for (int i = 0; i < v2.size(); i++) {
                last.add(values.indexOf(v2.get(i)));
            }


            return last; // select 10 closest faces

        }


        /**
         * INSANELY slow without immediatelyfast/sodium. This is probably due to the buffer switching. Could I fix it? Yes! Will I? No!
         * @param provider
         * @param stack
         * @param yaw
         * @param highlightSelection
         * @param lines
         */
        public void renderModelDebug(VertexConsumerProvider provider, MatrixStack stack, float yaw, Set<Integer> highlightSelection,boolean lines){

            stack.push();



            Matrix4f entry;
            if(yaw == 0){
                entry = stack.peek().getPositionMatrix();
            }else {
                entry = stack.peek().getPositionMatrix().rotateY(3.14159f + -yaw);
            }

            var light = LightmapTextureManager.MAX_LIGHT_COORDINATE;

            if(quadsInsteadOfTriangles){

                VertexConsumer consumer;

                if(InterpolationModel == null) {

                    for(int x = 0; x < quads.size(); x++) {

                        OBJQuad objQuad = quads.get(x);




                        if(highlightSelection.contains(x)){

                            consumer = provider.getBuffer(objQuadLayer.apply(DEBUG_SELECTED,program));

                            consumer.vertex(entry, objQuad.vertex1.x, objQuad.vertex1.y, objQuad.vertex1.z).color(0.1f, 0.1f, 1f, 1f).normal(objQuad.vertex1Normal.x, objQuad.vertex1Normal.y, objQuad.vertex1Normal.z).texture(objQuad.vertex1Texture.x, -objQuad.vertex1Texture.y).light(light);
                            consumer.vertex(entry, objQuad.vertex2.x, objQuad.vertex2.y, objQuad.vertex2.z).color(0.1f, 0.1f, 1f, 1f).normal(objQuad.vertex2Normal.x, objQuad.vertex2Normal.y, objQuad.vertex2Normal.z).texture(objQuad.vertex2Texture.x, -objQuad.vertex2Texture.y).light(light);
                            consumer.vertex(entry, objQuad.vertex3.x, objQuad.vertex3.y, objQuad.vertex3.z).color(0.1f, 0.1f, 1f, 1f).normal(objQuad.vertex3Normal.x, objQuad.vertex3Normal.y, objQuad.vertex3Normal.z).texture(objQuad.vertex3Texture.x, -objQuad.vertex3Texture.y).light(light);
                            consumer.vertex(entry, objQuad.vertex4.x, objQuad.vertex4.y, objQuad.vertex4.z).color(0.1f, 0.1f, 1f, 1f).normal(objQuad.vertex4Normal.x, objQuad.vertex4Normal.y, objQuad.vertex3Normal.z).texture(objQuad.vertex4Texture.x, -objQuad.vertex4Texture.y).light(light);

                            if (doBackface) {
                                consumer.vertex(entry, objQuad.vertex4.x, objQuad.vertex4.y, objQuad.vertex4.z).color(0.1f, 0.1f, 1f, 1f).normal(objQuad.vertex4Normal.x, objQuad.vertex4Normal.y, objQuad.vertex3Normal.z).texture(objQuad.vertex4Texture.x, -objQuad.vertex4Texture.y).light(light);
                                consumer.vertex(entry, objQuad.vertex3.x, objQuad.vertex3.y, objQuad.vertex3.z).color(0.1f, 0.1f, 1f, 1f).normal(objQuad.vertex3Normal.x, objQuad.vertex3Normal.y, objQuad.vertex3Normal.z).texture(objQuad.vertex3Texture.x, -objQuad.vertex3Texture.y).light(light);
                                consumer.vertex(entry, objQuad.vertex2.x, objQuad.vertex2.y, objQuad.vertex2.z).color(0.1f, 0.1f, 1f, 1f).normal(objQuad.vertex2Normal.x, objQuad.vertex2Normal.y, objQuad.vertex2Normal.z).texture(objQuad.vertex2Texture.x, -objQuad.vertex2Texture.y).light(light);
                                consumer.vertex(entry, objQuad.vertex1.x, objQuad.vertex1.y, objQuad.vertex1.z).color(0.1f, 0.1f, 1f, 1f).normal(objQuad.vertex1Normal.x, objQuad.vertex1Normal.y, objQuad.vertex1Normal.z).texture(objQuad.vertex1Texture.x, -objQuad.vertex1Texture.y).light(light);
                            }

                            if(lines){
                                consumer = provider.getBuffer(lineDebug);

                                consumer.vertex(entry, objQuad.vertex1.x, objQuad.vertex1.y, objQuad.vertex1.z);
                                consumer.vertex(entry, objQuad.vertex2.x, objQuad.vertex2.y, objQuad.vertex2.z);
                                consumer.vertex(entry, objQuad.vertex3.x, objQuad.vertex3.y, objQuad.vertex3.z);
                                consumer.vertex(entry, objQuad.vertex4.x, objQuad.vertex4.y, objQuad.vertex4.z);
                            }

                        }else {

                            consumer = provider.getBuffer(objQuadLayer.apply(getIDOrFallback(),program));

                            consumer.vertex(entry, objQuad.vertex1.x, objQuad.vertex1.y, objQuad.vertex1.z).color(1f, 1f, 1f, 1f).normal(objQuad.vertex1Normal.x, objQuad.vertex1Normal.y, objQuad.vertex1Normal.z).texture(objQuad.vertex1Texture.x, -objQuad.vertex1Texture.y).light(light);
                            consumer.vertex(entry, objQuad.vertex2.x, objQuad.vertex2.y, objQuad.vertex2.z).color(1f, 1f, 1f, 1f).normal(objQuad.vertex2Normal.x, objQuad.vertex2Normal.y, objQuad.vertex2Normal.z).texture(objQuad.vertex2Texture.x, -objQuad.vertex2Texture.y).light(light);
                            consumer.vertex(entry, objQuad.vertex3.x, objQuad.vertex3.y, objQuad.vertex3.z).color(1f, 1f, 1f, 1f).normal(objQuad.vertex3Normal.x, objQuad.vertex3Normal.y, objQuad.vertex3Normal.z).texture(objQuad.vertex3Texture.x, -objQuad.vertex3Texture.y).light(light);
                            consumer.vertex(entry, objQuad.vertex4.x, objQuad.vertex4.y, objQuad.vertex4.z).color(1f, 1f, 1f, 1f).normal(objQuad.vertex4Normal.x, objQuad.vertex4Normal.y, objQuad.vertex3Normal.z).texture(objQuad.vertex4Texture.x, -objQuad.vertex4Texture.y).light(light);

                            if (doBackface) {
                                consumer.vertex(entry, objQuad.vertex4.x, objQuad.vertex4.y, objQuad.vertex4.z).color(1f, 1f, 1f, 1f).normal(objQuad.vertex4Normal.x, objQuad.vertex4Normal.y, objQuad.vertex3Normal.z).texture(objQuad.vertex4Texture.x, -objQuad.vertex4Texture.y).light(light);
                                consumer.vertex(entry, objQuad.vertex3.x, objQuad.vertex3.y, objQuad.vertex3.z).color(1f, 1f, 1f, 1f).normal(objQuad.vertex3Normal.x, objQuad.vertex3Normal.y, objQuad.vertex3Normal.z).texture(objQuad.vertex3Texture.x, -objQuad.vertex3Texture.y).light(light);
                                consumer.vertex(entry, objQuad.vertex2.x, objQuad.vertex2.y, objQuad.vertex2.z).color(1f, 1f, 1f, 1f).normal(objQuad.vertex2Normal.x, objQuad.vertex2Normal.y, objQuad.vertex2Normal.z).texture(objQuad.vertex2Texture.x, -objQuad.vertex2Texture.y).light(light);
                                consumer.vertex(entry, objQuad.vertex1.x, objQuad.vertex1.y, objQuad.vertex1.z).color(1f, 1f, 1f, 1f).normal(objQuad.vertex1Normal.x, objQuad.vertex1Normal.y, objQuad.vertex1Normal.z).texture(objQuad.vertex1Texture.x, -objQuad.vertex1Texture.y).light(light);
                            }

                            if(lines){
                                consumer = provider.getBuffer(lineDebug);

                                consumer.vertex(entry, objQuad.vertex1.x, objQuad.vertex1.y, objQuad.vertex1.z);
                                consumer.vertex(entry, objQuad.vertex2.x, objQuad.vertex2.y, objQuad.vertex2.z);
                                consumer.vertex(entry, objQuad.vertex3.x, objQuad.vertex3.y, objQuad.vertex3.z);
                                consumer.vertex(entry, objQuad.vertex4.x, objQuad.vertex4.y, objQuad.vertex4.z);
                            }
                        }





                    }
                }

            }else
            {
                for(int x = 0; x < triangles.size(); x++){


                    OBJTriangle objTriangle = triangles.get(x);


                    VertexConsumer consumer;



                    if(highlightSelection.contains(x)) {

                        consumer = provider.getBuffer(objTriangleLayer.apply(DEBUG_SELECTED,program));

                        // flip y
                        consumer.vertex(entry, objTriangle.vertex1.x, objTriangle.vertex1.y, objTriangle.vertex1.z).color(0.1f, 0.1f, 1f, 1f).normal(objTriangle.vertex1Normal.x, objTriangle.vertex1Normal.y, objTriangle.vertex1Normal.z).texture(objTriangle.vertex1Texture.x, -objTriangle.vertex1Texture.y).light(light);
                        consumer.vertex(entry, objTriangle.vertex2.x, objTriangle.vertex2.y, objTriangle.vertex2.z).color(0.1f, 0.1f, 1f, 1f).normal(objTriangle.vertex2Normal.x, objTriangle.vertex2Normal.y, objTriangle.vertex2Normal.z).texture(objTriangle.vertex2Texture.x, -objTriangle.vertex2Texture.y).light(light);
                        consumer.vertex(entry, objTriangle.vertex3.x, objTriangle.vertex3.y, objTriangle.vertex3.z).color(0.1f, 0.1f, 1f, 1f).normal(objTriangle.vertex3Normal.x, objTriangle.vertex3Normal.y, objTriangle.vertex3Normal.z).texture(objTriangle.vertex3Texture.x, -objTriangle.vertex3Texture.y).light(light);

                        if (doBackface) {

                            consumer.vertex(entry, objTriangle.vertex3.x, objTriangle.vertex3.y, objTriangle.vertex3.z).color(0.1f, 0.1f, 1f, 1f).normal(objTriangle.vertex3Normal.x, objTriangle.vertex3Normal.y, objTriangle.vertex3Normal.z).texture(objTriangle.vertex3Texture.x, -objTriangle.vertex3Texture.y).light(light);

                            consumer.vertex(entry, objTriangle.vertex2.x, objTriangle.vertex2.y, objTriangle.vertex2.z).color(0.1f, 0.1f, 1f, 1f).normal(objTriangle.vertex2Normal.x, objTriangle.vertex2Normal.y, objTriangle.vertex2Normal.z).texture(objTriangle.vertex2Texture.x, -objTriangle.vertex2Texture.y).light(light);

                            consumer.vertex(entry, objTriangle.vertex1.x, objTriangle.vertex1.y, objTriangle.vertex1.z).color(0.1f, 0.1f, 1f, 1f).normal(objTriangle.vertex1Normal.x, objTriangle.vertex1Normal.y, objTriangle.vertex1Normal.z).texture(objTriangle.vertex1Texture.x, -objTriangle.vertex1Texture.y).light(light);

                        }

                        if(lines){
                            consumer = provider.getBuffer(lineDebug);

                            consumer.vertex(entry, objTriangle.vertex1.x, objTriangle.vertex1.y, objTriangle.vertex1.z);
                            consumer.vertex(entry, objTriangle.vertex2.x, objTriangle.vertex2.y, objTriangle.vertex2.z);
                            consumer.vertex(entry, objTriangle.vertex3.x, objTriangle.vertex3.y, objTriangle.vertex3.z);
                        }


                    }else {

                        consumer = provider.getBuffer(objTriangleLayer.apply(getIDOrFallback(),program));

                        // you need to flip the y on these textures
                        consumer.vertex(entry, objTriangle.vertex1.x, objTriangle.vertex1.y, objTriangle.vertex1.z).color(1f, 1f, 1f, 1f).normal(objTriangle.vertex1Normal.x, objTriangle.vertex1Normal.y, objTriangle.vertex1Normal.z).texture(objTriangle.vertex1Texture.x,-objTriangle.vertex1Texture.y).light(light);
                        consumer.vertex(entry, objTriangle.vertex2.x, objTriangle.vertex2.y, objTriangle.vertex2.z).color(1f, 1f, 1f, 1f).normal(objTriangle.vertex2Normal.x, objTriangle.vertex2Normal.y, objTriangle.vertex2Normal.z).texture(objTriangle.vertex2Texture.x, -objTriangle.vertex2Texture.y).light(light);
                        consumer.vertex(entry, objTriangle.vertex3.x, objTriangle.vertex3.y, objTriangle.vertex3.z).color(1f, 1f, 1f, 1f).normal(objTriangle.vertex3Normal.x, objTriangle.vertex3Normal.y, objTriangle.vertex3Normal.z).texture(objTriangle.vertex3Texture.x, -objTriangle.vertex3Texture.y).light(light);

                        if (doBackface) {

                            consumer.vertex(entry, objTriangle.vertex3.x, objTriangle.vertex3.y, objTriangle.vertex3.z).color(1f, 1f, 1f, 1f).normal(objTriangle.vertex3Normal.x, objTriangle.vertex3Normal.y, objTriangle.vertex3Normal.z).texture(objTriangle.vertex3Texture.x, -objTriangle.vertex3Texture.y).light(light);

                            consumer.vertex(entry, objTriangle.vertex2.x, objTriangle.vertex2.y, objTriangle.vertex2.z).color(1f, 1f, 1f, 1f).normal(objTriangle.vertex2Normal.x, objTriangle.vertex2Normal.y, objTriangle.vertex2Normal.z).texture(objTriangle.vertex2Texture.x, -objTriangle.vertex2Texture.y).light(light);

                            consumer.vertex(entry, objTriangle.vertex1.x, objTriangle.vertex1.y, objTriangle.vertex1.z).color(1f, 1f, 1f, 1f).normal(objTriangle.vertex1Normal.x, objTriangle.vertex1Normal.y, objTriangle.vertex1Normal.z).texture(objTriangle.vertex1Texture.x, -objTriangle.vertex1Texture.y).light(light);

                        }

                        if(lines){
                            consumer = provider.getBuffer(lineDebug);

                            consumer.vertex(entry, objTriangle.vertex1.x, objTriangle.vertex1.y, objTriangle.vertex1.z);
                            consumer.vertex(entry, objTriangle.vertex2.x, objTriangle.vertex2.y, objTriangle.vertex2.z);
                            consumer.vertex(entry, objTriangle.vertex3.x, objTriangle.vertex3.y, objTriangle.vertex3.z);
                        }
                    }

                }
            }


            stack.pop();

        }


    }



    List<OBJTriangle> triangles = new ArrayList<>();
    List<OBJQuad> quads = new ArrayList<>();

    List<Vector3f> vertices = new ArrayList<>();
    List<Vector3f> verticesNormal = new ArrayList<>();
    List<Vector2f> verticesTexture = new ArrayList<>();

    Material selectedMaterial;

    // I think we're okay to ignore "f" in OBJ, and the rest is for models.
    public void parseVertex(@Nullable String line){

        // newlines are allowed in OBJ files!
        if(line == null || line.isEmpty()){
            return;
        }

        char[] chars = line.toCharArray();



        Vector3f vector = new Vector3f();

        int vertexIndex = 0;

        boolean ignoreNext =false;

        String value = ""; // atoi this

        // we only want to do all of this if it's an actual vertex.
        if(chars[0] == 'v' && chars[1] != 'n' && chars[1] != 't') {

            for (int x = 0; x < chars.length; x++) {

                if (ignoreNext) {
                    ignoreNext = false;
                    continue; // skip the loop
                }

                if (x == 0 && chars[x] == 'v') {
                    ignoreNext = true;
                    continue;
                }

                // this way we should only process a space if it's after a value.
                if (chars[x] == ' ' || x == chars.length - 1) {

                    if(value.isBlank()){
                        System.err.println("you forgot something!");
                    }

                    switch (vertexIndex) {
                        case 0:
                            vector.x = Float.parseFloat(value);
                            vertexIndex += 1;
                            break;

                        case 1:
                            vector.y = Float.parseFloat(value);
                            vertexIndex += 1;
                            break;

                        case 2: // end of line
                            vector.z = Float.parseFloat(value);


                            vertices.add(vector);
                            vertexIndex = 0;
                            break;
                    }

                    value = "";
                    continue; // don't add the whitepsace



                }

                value += chars[x];
            }
        }

        // vertex normals

        if(chars[0] == 'v' && chars[1] == 'n') {

            for (int x = 0; x < chars.length; x++) {

                if (ignoreNext) {
                    ignoreNext = false;
                    continue; // skip the loop
                }

                if(chars[x] == 'v'){
                    continue;
                }

                // skip vn
                if (chars[x] == 'n') {
                    ignoreNext = true;
                    continue;
                }

                // this way we should only process a space if it's after a value.
                if (chars[x] == ' ' || x == chars.length - 1) {

                    if(value.isBlank()){
                        System.err.println("you forgot something!");
                    }

                    switch (vertexIndex) {
                        case 0:
                            // remember, we only generate one 'thing' (vertex, normal, texture, etc) per line, so we can be careless here.
                            vector.x = Float.parseFloat(value);
                            vertexIndex += 1;
                            break;

                        case 1:
                            vector.y = Float.parseFloat(value);
                            vertexIndex += 1;
                            break;

                        case 2: // end of line
                            vector.z = Float.parseFloat(value);


                            verticesNormal.add(vector);
                            vertexIndex = 0;
                            break;
                    }

                    value = "";
                    continue; // don't add the whitepsace



                }

                value += chars[x];
            }
        }

        // vertex texture UV

        Vector2f textureVector = new Vector2f();

        if(chars[0] == 'v' && chars[1] == 't') {

            for (int x = 0; x < chars.length; x++) {

                if (ignoreNext) {
                    ignoreNext = false;
                    continue; // skip the loop
                }

                if(chars[x] == 'v'){
                    continue;
                }

                // skip vt
                if (chars[x] == 't') {
                    ignoreNext = true;
                    continue;
                }

                // this way we should only process a space if it's after a value.
                if (chars[x] == ' ' || x == chars.length - 1) {

                    if(value.isBlank()){
                        System.err.println("you forgot something!");
                    }

                    switch (vertexIndex) {
                        case 0:
                            textureVector.x = Float.parseFloat(value);
                            vertexIndex += 1;
                            break;


                        case 1: // end of line
                            textureVector.y = Float.parseFloat(value);

                            verticesTexture.add(textureVector);
                            vertexIndex = 0;
                            break;
                    }

                    value = "";
                    continue; // don't add the whitepsace



                }

                value += chars[x];
            }
        }

        // face stuff, we've done all the vertices by now. (or we should have in a reasonable OBJ file, that being said this shoddy code is probably able to survive some of that)

        if (chars[0] == 'f'){



            var facedata = line.split(" ");

            int vertexCount = facedata.length - 1;

            // determine intelligently
            if(selectedBehaviour == vertexBehaviour.AUTO_DETECT) {
                quadsInsteadOfTriangles = vertexCount == 4;
            }else {
                quadsInsteadOfTriangles = selectedBehaviour == vertexBehaviour.QUADS;
            }

            // triangles and quads ONLY!
            if(vertexCount > 4 || vertexCount < 3) {
                System.err.println("I have detected a ridiculous vertex-count face (" + vertexCount + ") and will now proceed to screw up your mesh >:(");
            }

            int[] valuesVertex = new int[vertexCount]; // up to 4 vertices.
            int[] valuesNormal = new int[vertexCount];
            int[] valuesTexture = new int[vertexCount];


            // TODO: handle the edge-cases of textures without normals, and just position elements.

            // skip the 'f' split.
            for (int x = 1; x < facedata.length; x++) {


                // f 1/2/3 -> ('1')/2/3
                valuesVertex[x -1] = Integer.parseInt(facedata[x].split("/")[0]);
                if(facedata[x].contains("//")) { // normals can be omitted via a "//", and we should handle that.
                    valuesNormal[x - 1] = Integer.parseInt(facedata[x].split("/")[1]);
                }else {
                    valuesTexture[x - 1] = Integer.parseInt(facedata[x].split("/")[1]);
                    valuesNormal[x - 1] = Integer.parseInt(facedata[x].split("/")[2]);
                }

            }

            // todo: support n-gons
            if(quadsInsteadOfTriangles) {
                // remember! OBJ files index from 1! This sucks, but is just a simple subtract.
                quads.add(new OBJQuad(vertices.get(valuesVertex[0] -1),vertices.get(valuesVertex[1] - 1),vertices.get(valuesVertex[2] - 1),vertices.get(valuesVertex[3] - 1))
                        .normal(verticesNormal.get(valuesNormal[0] - 1),verticesNormal.get(valuesNormal[1] - 1), verticesNormal.get(valuesNormal[2] - 1),verticesNormal.get(valuesNormal[3]-1))
                        .texture(verticesTexture.get(valuesTexture[0] - 1), verticesTexture.get(valuesTexture[1] - 1), verticesTexture.get(valuesTexture[2] - 1),verticesTexture.get(valuesTexture[3]-1))
                        .material(selectedMaterial));
            }else {
                triangles.add(new OBJTriangle(vertices.get(valuesVertex[0] - 1),vertices.get(valuesVertex[1] - 1),vertices.get(valuesVertex[2] - 1))
                        .normal(verticesNormal.get(valuesNormal[0] - 1),verticesNormal.get(valuesNormal[1] - 1), verticesNormal.get(valuesNormal[2] - 1))
                        .texture(verticesTexture.get(valuesTexture[0] - 1), verticesTexture.get(valuesTexture[1] - 1), verticesTexture.get(valuesTexture[2] - 1))
                        .material(selectedMaterial));
            }
        }

        // handle materials for colour

        String[] mtcommand = line.split(" ");

        if(Objects.equals(mtcommand[0], "usemtl")){
            selectedMaterial = materialMap.get(mtcommand[1]);
        }


    }


    // OBJ is already counter-clockwise on export, so we can just slam tris in.
    public static class OBJTriangle{

        public OBJTriangle(Vector3f v1,Vector3f v2, Vector3f v3){
            vertex1 = v1;
            vertex2 = v2;
            vertex3 = v3;
        }

        Material selectedMaterial;

        Vector3f vertex1;
        Vector3f vertex2;
        Vector3f vertex3;

        Vector3f vertex1Normal;
        Vector3f vertex2Normal;
        Vector3f vertex3Normal;

        Vector2f vertex1Texture;
        Vector2f vertex2Texture;
        Vector2f vertex3Texture;

        public OBJTriangle normal(Vector3f vn1,Vector3f vn2,Vector3f vn3){
            vertex1Normal = vn1;
            vertex2Normal = vn2;
            vertex3Normal = vn3;
            return this;
        }

        public OBJTriangle texture(Vector2f vt1,Vector2f vt2,Vector2f vt3){
            vertex1Texture = vt1;
            vertex2Texture = vt2;
            vertex3Texture = vt3;
            return this;
        }

        public OBJTriangle material(Material mat){
            this.selectedMaterial = mat;
            return this;
        }

    }

    public static class OBJQuad{

        public OBJQuad(Vector3f v1,Vector3f v2, Vector3f v3,Vector3f v4){
            vertex1 = v1;
            vertex2 = v2;
            vertex3 = v3;
            vertex4 = v4;
        }

        Material selectedMaterial;

        Vector3f vertex1;
        Vector3f vertex2;
        Vector3f vertex3;
        Vector3f vertex4;

        Vector3f vertex1Normal;
        Vector3f vertex2Normal;
        Vector3f vertex3Normal;
        Vector3f vertex4Normal;

        Vector2f vertex1Texture;
        Vector2f vertex2Texture;
        Vector2f vertex3Texture;
        Vector2f vertex4Texture;

        public OBJQuad normal(Vector3f vn1, Vector3f vn2, Vector3f vn3, Vector3f vn4){
            vertex1Normal = vn1;
            vertex2Normal = vn2;
            vertex3Normal = vn3;
            vertex4Normal = vn4;
            return this;
        }

        public OBJQuad texture(Vector2f vt1, Vector2f vt2, Vector2f vt3, Vector2f vt4){
            vertex1Texture = vt1;
            vertex2Texture = vt2;
            vertex3Texture = vt3;
            vertex4Texture = vt4;
            return this;
        }

        public OBJQuad material(Material mat){
            this.selectedMaterial = mat;
            return  this;
        }


    }


}
