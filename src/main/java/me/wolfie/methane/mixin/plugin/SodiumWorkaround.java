package me.wolfie.methane.mixin.plugin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.include.com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static me.wolfie.methane.Methane.MethaneLogger;

public class SodiumWorkaround implements IMixinConfigPlugin {

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        //MethaneLogger.error(String.valueOf(mixinClassName.equals("me.wolfie.methane.mixin.BackgroundMixin")) + " , classname is: " + mixinClassName);
        if(mixinClassName.equals("me.wolfie.methane.mixin.BackgroundMixin")) {
            //System.out.println("round 2 of deciding");
            if (FabricLoader.getInstance().isModLoaded("sodium")) {
                return false;
            } else {
                return true;
            }
        }else {
            //System.out.println("else...?");
            return true;
        }
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
