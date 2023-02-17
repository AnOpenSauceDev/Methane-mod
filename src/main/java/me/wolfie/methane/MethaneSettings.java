package me.wolfie.methane;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "methane")
public class MethaneSettings implements ConfigData {
    //TODO: we should transition to translatable comments (@Comment(Text.translatable("YOUR.THING.HERE")))
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
}
