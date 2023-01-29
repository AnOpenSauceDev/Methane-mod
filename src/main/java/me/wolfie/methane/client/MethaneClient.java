package me.wolfie.methane.client;

import me.wolfie.methane.Methane;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.lwjgl.glfw.GLFW;

public class MethaneClient implements ClientModInitializer {

    public KeyBinding MethaneToggle;

    @Override
    public void onInitializeClient() {

        MethaneToggle = KeyBindingHelper.registerKeyBinding( new KeyBinding(
                        "key.methane.toggle",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_BACKSLASH,
                        "category.methane.keys"
                )
        );


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
    
            while (MethaneToggle.wasPressed()){
                Methane.ModActive = !Methane.ModActive;
                client.player.sendMessage(Text.of("Set Methane's status to: " + Methane.ModActive + ".")); // make me translatable!
                //client.world.reloadColor();
            }
        });
    }
}
