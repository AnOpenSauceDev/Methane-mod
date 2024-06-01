package com.modrinth.methane.mixin.plugin;

import com.github.anopensaucedev.libmcdevfabric.Debug;
import com.github.anopensaucedev.libmcdevfabric.Libmcdev;
import com.modrinth.methane.Methane;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MethaneMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if(Objects.equals(mixinClassName, "com.modrinth.methane.mixin.ParticleMixin") && FabricLoader.getInstance().isModLoaded("ironsspellbooks")){
            Methane.MethaneLogger.error("Disabling Methane's Particle Optimization Mixin due to Iron's Spellbooks being installed!");
            return false;
        }
        if(Objects.equals(mixinClassName, "com.modrinth.methane.mixin.ParticleMixin") && FabricLoader.getInstance().isModLoaded("ichor")){
            Methane.MethaneLogger.error("Disabling Methane's Particle Optimization Mixin due to Ichor (Lunar Client)'s game-breaking particle mixin");
            return false;
        }
        if(Objects.equals(mixinClassName, "com.modrinth.methane.mixin.ToastManagerMixin") && FabricLoader.getInstance().isModLoaded("toastkiller")){
            Methane.MethaneLogger.warn("Overriding Methane's toast handler to use The Open Sauce Toast Killer.");
            return false;
        }
        return true;
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
