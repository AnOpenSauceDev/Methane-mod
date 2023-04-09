package me.wolfie.methane.client;

import me.wolfie.methane.Methane;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class HudRenderListener implements HudRenderCallback {

    public static int ShowTicks;

    @Override
    public void onHudRender(MatrixStack matrixStack, float v) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        if (ShowTicks > 0){
            ShowTicks -= v; // i assume "V" is the delta
            //Methane.MethaneLogger.error("DEBUG REMOVE ME: " + ShowTicks + ", V: " + v);
            if(Methane.ModActive) {
                MinecraftClient.getInstance().textRenderer.draw(matrixStack, Text.translatable("methane.active"),width-400,height-25,0xFFFFFF);
            }else {
                MinecraftClient.getInstance().textRenderer.draw(matrixStack, Text.translatable("methane.offline"),width-400,height-25,0xFFFFFF);
            }
        }
    }

}
