package com.modrinth.methane.client;

import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class MethaneJoinPopUp extends Screen {

    private Screen parent;
    private boolean statedata;

     public MethaneJoinPopUp(Text title,boolean data) {
        super(Text.of("Methane Server Settings"));
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
       MultilineText txt = MultilineText.create(textRenderer,Text.literal("This server recommends certain methane settings. You can choose whether to follow these settings or not."),256,0xFFFFFF);
       txt.drawCenterWithShadow(context,width / 2, (height - 30) / 2);
    }

    @Override
    protected void init() {
        yes = ButtonWidget.builder(Text.literal("Yes"), button -> {
                    MethaneClient.ToggleMethaneSetBool(client,statedata);
                    close();
                })
                .dimensions(width / 2 - 205, 20, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Accept server config.")))
                .build();
        no = ButtonWidget.builder(Text.literal("No"), button -> {
                    close();
                })
                .dimensions(width / 2 + 5, 20, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Reject server config.")))
                .build();

        addDrawableChild(yes);
        addDrawableChild(no);
    }

}