package me.wolfie.methane.client;

import me.wolfie.methane.Methane;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import static me.wolfie.methane.Methane.ModActive;
import static me.wolfie.methane.Methane.settings;

public class MethaneClient implements ClientModInitializer {

    public KeyBinding MethaneToggle;

    @Override
    public void onInitializeClient() {

        Methane.ModActive = settings.modstate;

        // this causes us to need the Fabric API.
        MethaneToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                        "key.methane.toggle",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_BACKSLASH,
                        "category.methane.keys"
                )
        );


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (MethaneToggle.wasPressed()){
                ModActive = !ModActive;
                if(ModActive) {
                    client.player.sendMessage(Text.translatable("methane.active"));
                    HudRenderListener.ShowTicks = (5 * 20); // 5 seconds, in ticks
                }else {
                    client.player.sendMessage(Text.translatable("methane.offline"));
                    HudRenderListener.ShowTicks = (5 * 20);
                }
            }
        });

    }
}
