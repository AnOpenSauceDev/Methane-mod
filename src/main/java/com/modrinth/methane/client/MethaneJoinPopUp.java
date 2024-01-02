package com.modrinth.methane.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import static com.modrinth.methane.client.MethaneClient.METHANE_RESP_PACKET;

@Environment(EnvType.CLIENT)
public class MethaneJoinPopUp extends Screen {

    private Screen parent;
    private boolean statedata;

     public MethaneJoinPopUp(Text title,boolean data) {
        super(Text.translatable("methane.serverpopup.settings"));
         statedata = data;
         this.parent = parent;
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    public ButtonWidget yes;
    public ButtonWidget no;

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
       MultilineText txt = MultilineText.create(textRenderer,Text.translatable("methane.serverpopup.info"),256,0xFFFFFF);
       txt.drawCenterWithShadow(context,width / 2, (height - 30) / 2);
    }

    @Override
    protected void init() {
        yes = ButtonWidget.builder(Text.translatable("methane.yes"), button -> {
                    MethaneClient.ToggleMethaneSetBool(client,statedata);
                    ClientPlayNetworking.send(METHANE_RESP_PACKET, PacketByteBufs.empty());
                    close();
                })
                .dimensions(width / 2 - 205, 20, 200, 20)
                .tooltip(Tooltip.of(Text.translatable("methane.accept")))
                .build();
        no = ButtonWidget.builder(Text.translatable("methane.no"), button -> {
                    ClientPlayNetworking.send(METHANE_RESP_PACKET,PacketByteBufs.empty());
                    close();
                })
                .dimensions(width / 2 + 5, 20, 200, 20)
                .tooltip(Tooltip.of(Text.translatable("methane.reject")))
                .build();

        addDrawableChild(yes);
        addDrawableChild(no);
    }

}
