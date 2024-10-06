package com.modrinth.methane.client;

import com.modrinth.methane.util.MethaneConstants;
import com.modrinth.methane.core.builtins.methaneui.MethaneMenu;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class MethaneClient implements ClientModInitializer {


    KeyBinding MethaneOptions = KeyBindingHelper.registerKeyBinding(new KeyBinding("methane.serverpopup.settings", GLFW.GLFW_KEY_BACKSLASH,"category.methane.keys"));

    private static final Identifier DEFAULT_CUSTOM_SHADER = Identifier.of("methane","default_shader");

    public static ShaderProgram defaultShader;

    public static ShaderInst inst;

    @Override
    public void onInitializeClient() {

        MethaneConstants.IS_CLIENT = true;

        CoreShaderRegistrationCallback.EVENT.register( context -> {
            context.register(DEFAULT_CUSTOM_SHADER, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,shaderProgram -> defaultShader = shaderProgram);
            inst = new ShaderInst(defaultShader);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(client != null){
                while (MethaneOptions.wasPressed()){
                    client.setScreen(new MethaneMenu(Text.of("e")));
                }
            }
        });
    }

}
