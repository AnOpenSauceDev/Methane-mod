package me.wolfie.methane;

import net.minecraft.client.MinecraftClient;

import static me.wolfie.methane.Methane.MethaneLogger;

public class verifyIfClient {
    public static void Verify(){
        new Thread(() ->{
            try {
                if (Class.forName("net.minecraft.client.MinecraftClient").getClass() != null) {
                    // do nothing, everything went as expected
                }
            }catch (RuntimeException ex){
                ex.printStackTrace();
                MethaneLogger.error("Methane is running on the server! This mod is client-only!");
            }catch (ClassNotFoundException ex){
                ex.printStackTrace();
                MethaneLogger.error("Methane is running on the server! This mod is client-only!");
            }
        }).start();
    }
}
