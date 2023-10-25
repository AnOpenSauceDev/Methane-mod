package com.modrinth.methane.client;

import com.modrinth.methane.Methane;
import com.modrinth.methane.util.Debug;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import static com.modrinth.methane.util.MethaneConstants.METHANE_STATE_PACKET;
import static net.minecraft.client.realms.task.LongRunningTask.setScreen;

public class MethaneClient implements ClientModInitializer {

    public KeyBinding MethaneToggle;

    public static final Identifier METHANE_RESP_PACKET = new Identifier("methane_server","pong");


    public static boolean intToBoolConversion(int i){
        return i != 0; // if "i" is not zero, return true
    }

    @Override
    public void onInitializeClient() {

        //HudRenderCallback.EVENT.register(new HudRenderListener());

        ClientPlayNetworking.registerGlobalReceiver(METHANE_STATE_PACKET, ((client, handler, buf, responseSender) -> {


            int[] data = buf.readIntArray(); // 0 = enforceModState, 1 = globalModState, 2 = forceMethane (won't ever be used)
            if (intToBoolConversion(data[0])) {
                MethaneClient.ToggleMethaneSetBool(client, intToBoolConversion(data[1]));
                Debug.Log("forcing methane server config");

                Methane.ServerForbidsChanging = true;
                Methane.playerBlockingPacket = true;
            } else {
                // if the server allows changes
                Debug.Log("Methane settings prompt open");
                setScreen(new MethaneJoinPopUp(Text.of("Methane Server Settings"), intToBoolConversion(data[1])));
            }

        }));

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



            if(client.player == null){ // I'm assuming that ClientPlayerEntity is only ever null if you quit the server.
                Methane.ServerForbidsChanging = false;
            }else if(Methane.playerBlockingPacket){ // I wanted to avoid this at all costs, but it looks like I have to do this hack.

                    Methane.playerBlockingPacket = false;
                    ClientPlayNetworking.send(METHANE_RESP_PACKET, PacketByteBufs.empty());

            }


            while (MethaneToggle.wasPressed()){

                    ToggleMethane(client,false);

            }
        });



    }

    public static void ToggleMethane(MinecraftClient client,boolean force) {
        if(!Methane.ServerForbidsChanging || force){

        Methane.ModActive = !Methane.ModActive;

        if(Methane.settings.hudrender){

            if(Methane.ModActive)
            {

                client.player.sendMessage(Text.translatable("methane.active"),true);

            }else
            {

                client.player.sendMessage(Text.translatable("methane.offline"),true);
            }


        }else {
            if(Methane.ModActive)
            {

                client.player.sendMessage(Text.translatable("methane.active"));

            }else
            {

                client.player.sendMessage(Text.translatable("methane.offline"));
            }
        }
    }
     }

    public static void ToggleMethaneSetBool(MinecraftClient client,boolean state) {



        Methane.ModActive = state;
        if(Methane.settings.hudrender) {
            if(Methane.settings.hudrender){

                if(Methane.ModActive)
                {

                    client.player.sendMessage(Text.translatable("methane.active"),true);

                }else
                {

                    client.player.sendMessage(Text.translatable("methane.offline"),true);
                }


            }else {
                if(Methane.ModActive)
                {

                    client.player.sendMessage(Text.translatable("methane.active"));

                }else
                {

                    client.player.sendMessage(Text.translatable("methane.offline"));
                }
            }

        }
    }

}
