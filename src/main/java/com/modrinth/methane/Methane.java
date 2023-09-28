package com.modrinth.methane;

import com.modrinth.methane.client.MethaneClient;
import com.modrinth.methane.client.MethaneJoinPopUp;
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

import static net.minecraft.client.realms.task.LongRunningTask.setScreen;

public class Methane implements ModInitializer {



    public static boolean ModActive = true; // for toggles

    public static Logger MethaneLogger = LoggerFactory.getLogger("Methane");
    public static MethaneSettings settings;

    public static final Identifier METHANE_STATE_PACKET = new Identifier("methane_server","statepacket");

    public static final Identifier METHANE_RESP_PACKET = new Identifier("methane_server","pong");

    @Override
    public void onInitialize() {
        MethaneLogger.info("Methane has loaded!");
        AutoConfig.register(MethaneSettings.class, GsonConfigSerializer::new);
        settings = AutoConfig.getConfigHolder(MethaneSettings.class).getConfig();

        HudRenderCallback.EVENT.register(new HudRenderListener());

        ClientPlayNetworking.registerGlobalReceiver(METHANE_STATE_PACKET,((client, handler, buf, responseSender) -> {
            ClientPlayNetworking.send(METHANE_RESP_PACKET,PacketByteBufs.empty());
            MethaneLogger.info("recived packet from server");
            int[] data = buf.readIntArray(); // 0 = enforceModState, 1 = globalModState, 2 = forceMethane (won't ever be used)
            if(intToBoolConversion(data[0])){
            MethaneClient.ToggleMethaneSetBool(client,intToBoolConversion(data[1]));
            MethaneLogger.info("forcing methane config");
            }else {
                MethaneLogger.info("Methane settings prompt open");
                setScreen(new MethaneJoinPopUp(Text.of("Methane Server Settings"), intToBoolConversion(data[1])));
            }
        }));
    }



    public boolean intToBoolConversion(int i){
        return i != 0; // if "i" is not zero, return true
    }

}
