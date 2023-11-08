package com.modrinth.methane.mixin;

import com.modrinth.methane.Methane;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    // awesome one-liner that gives a neat performance boost by... destroying menu backgrounds.
   @Inject(method = "renderBackground", at =@At("HEAD"), cancellable = true) public void backgroundRemoval(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) { if(Methane.settings.destructiveSettings.DestroyScreens) ci.cancel(); }

}
