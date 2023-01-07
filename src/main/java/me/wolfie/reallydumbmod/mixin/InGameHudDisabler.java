package me.wolfie.reallydumbmod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
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
