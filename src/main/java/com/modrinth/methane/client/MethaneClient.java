package com.modrinth.methane.client;

import com.modrinth.methane.Methane;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class MethaneClient implements ClientModInitializer {

    public KeyBinding MethaneToggle;

    @Override
    public void onInitializeClient() {

        Methane.ModActive = Methane.settings.modstate;

        // this causes us to need the Fabric API.
        MethaneToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                        "key.methane.toggle",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_UNKNOWN,
                        "category.methane.keys"
                )
        );


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (MethaneToggle.wasPressed()){

                    Methane.ModActive = !Methane.ModActive;
                    HudRenderListener.ShowTicks = (15 * 20);
                    if(!Methane.settings.hudrender){

                        if(Methane.ModActive)
                        {

                            client.player.sendMessage(Text.translatable("methane.active"));

                        }else
                        {
                            client.player.sendMessage(Text.translatable("methane.offline"));
                        }

                    }
            }
        });

    }
}
