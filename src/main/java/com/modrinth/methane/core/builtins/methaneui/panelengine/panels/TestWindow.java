package com.modrinth.methane.core.builtins.methaneui.panelengine.panels;

import com.modrinth.methane.core.builtins.methaneui.panelengine.XRPanel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class TestWindow extends XRPanel {

    public TestWindow() {
        super(Identifier.of("methane","test"), true, true, 640, 480);
    }

    @Override
    public void onRender(MatrixStack viewportMatrices, DrawContext context) {
        context.drawText(MinecraftClient.getInstance().textRenderer,"yipee!!!",0,0,0xFFFFFF,true);
    }
}
