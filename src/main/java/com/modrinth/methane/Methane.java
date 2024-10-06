package com.modrinth.methane;
import com.modrinth.methane.core.MethaneModule;
import com.modrinth.methane.core.builtins.methanecore.MethaneCore;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;


public class Methane implements ModInitializer {

    @Deprecated(forRemoval = true)
    public static boolean ModActive = false; // a remnant of Methane, will be removed

    public static MethaneModule METHANE_CORE = new MethaneCore();

    @Override
    public void onInitialize() {



    }


}
