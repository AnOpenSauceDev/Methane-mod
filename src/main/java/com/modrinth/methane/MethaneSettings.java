package com.modrinth.methane;


import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class MethaneSettings {
    //TODO: we should transition to translatable comments (@Comment(Text.translatable("YOUR.THING.HERE")))

    public static ConfigClassHandler<MethaneSettings> HANDLER = ConfigClassHandler.createBuilder(MethaneSettings.class)
            .id(new Identifier("mymod", "my_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("my_mod.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrint) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();



    @Comment("Methane's initial state. (You should set this to 'Yes' if you use sodium.)")
    public boolean modstate = true;
    @Comment("Toggle whether to keep fog settings even with Methane disabled.")
    public boolean persistFogSettings = false;
    public boolean disableAirFog = false; // the fog pass that obscures terrain
    public boolean disableWaterFog = false; // the fog layer that tints everything blue
    public boolean disableLavaFog = false; // the thing that tints everything orange
    public boolean disablePowderedSnowFog = false; // pretty self-explanatory
    @Comment("(you should probably disable this)")
    public boolean disableThickFog = false; // Nether Fog pass
    @Comment("The fog that covers terrain in")
    public boolean disableSkyFog = false; // I think this is another fog pass


    @Comment("The default world brightness value (15 default and effective max)")
    public double brightness = 1000; // unused for now because of a ton of issues



    @Comment("Render the status messages on the HUD instead of chat?")
    public boolean hudrender = true;
}
