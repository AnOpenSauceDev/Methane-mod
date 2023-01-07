package me.wolfie.methane.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Deprecated
@Mixin(value = InGameHud.class,priority = 1100)
public class InGameHudDisabler {

    /**
     * @author AnOpenSauceDev
     * @reason
     */
    @Overwrite
    public void render(MatrixStack matrices, float tickDelta) {
        MinecraftClient.getInstance().textRenderer.draw(matrices, Text.of("¯\\_(ツ)_/¯"),-300,-300,0xFFFFFF);
    }

}
