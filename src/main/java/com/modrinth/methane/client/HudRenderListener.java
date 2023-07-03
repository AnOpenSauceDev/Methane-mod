package com.modrinth.methane.client;

import com.modrinth.methane.Methane;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class HudRenderListener implements HudRenderCallback {

    public static int ShowTicks;



    // possible idea: allow positional overrides. (I believe that a config option is a bit un-intuitive)
    @Override
    public void onHudRender(DrawContext drawContext, float v) {
        if(Methane.settings.hudrender){
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;

            int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int height = MinecraftClient.getInstance().getWindow().getScaledHeight();

            if (ShowTicks > 0){

                ShowTicks -= v; // i assume "V" is the delta

                if(Methane.ModActive)
                {
                    drawContext.drawText(renderer,Text.translatable("methane.active"),width-400,height-250,0xFFFFFF,false);
                }
                else
                {
                    drawContext.drawText(renderer,Text.translatable("methane.offline"),width-400,height-250,0xFFFFFF,false);
                }

            }
        }
    }

}
