package com.modrinth.methane;

import com.modrinth.methane.client.MethaneClient;
import com.modrinth.methane.client.MethaneJoinPopUp;
import com.modrinth.methane.util.Debug;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import com.modrinth.methane.client.HudRenderListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.modrinth.methane.util.MethaneConstants.METHANE_STATE_PACKET;
import static com.modrinth.methane.util.MethaneConstants.MOD_NAME;
import static net.minecraft.client.realms.task.LongRunningTask.setScreen;

public class Methane implements ModInitializer {

    public static boolean ModActive = true; // for toggles

    public static Logger DebugLogger = LoggerFactory.getLogger("Methane Developer Debugger");

    public static Logger MethaneLogger = LoggerFactory.getLogger(MOD_NAME);
    public static MethaneSettings settings;

    public static boolean playerBlockingPacket; // whether the player was blocked due to a race condition

    public static boolean ServerForbidsChanging = false;

    @Override
    public void onInitialize() {

        MethaneLogger.info("Methane has loaded!");
        Debug.Log("Methane is in developer mode. If you are reading this in a non-dev environment, please create an issue.");

        AutoConfig.register(MethaneSettings.class, GsonConfigSerializer::new);
        settings = AutoConfig.getConfigHolder(MethaneSettings.class).getConfig();

        HudRenderCallback.EVENT.register(new HudRenderListener());

        ClientPlayNetworking.registerGlobalReceiver(METHANE_STATE_PACKET,((client, handler, buf, responseSender) -> {


            int[] data = buf.readIntArray(); // 0 = enforceModState, 1 = globalModState, 2 = forceMethane (won't ever be used)
            if(intToBoolConversion(data[0])){
            MethaneClient.ToggleMethaneSetBool(client,intToBoolConversion(data[1]));
            Debug.Log("forcing methane config");

            ServerForbidsChanging = true;
            playerBlockingPacket = true;
            }else {
                // if the server allows changes
                Debug.Log("Methane settings prompt open");
                setScreen(new MethaneJoinPopUp(Text.of("Methane Server Settings"), intToBoolConversion(data[1])));
            }

        }));

        if(Methane.settings.destructiveSettings.DestroySky || Methane.settings.destructiveSettings.DestroyWeather || Methane.settings.destructiveSettings.destructiveweatheroptimizations || Methane.settings.destructiveSettings.RenderLayerSkips){
            Methane.MethaneLogger.warn("One or more destructive Methane renderer features are being used. You might experience unusual bugs with other mods.");
        }
    }



    public static boolean intToBoolConversion(int i){
        return i != 0; // if "i" is not zero, return true
    }

}
