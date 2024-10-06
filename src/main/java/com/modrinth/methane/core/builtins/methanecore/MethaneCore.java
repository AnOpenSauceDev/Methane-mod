package com.modrinth.methane.core.builtins.methanecore;

import com.modrinth.methane.core.MethaneModule;
import com.modrinth.methane.util.DynamicArguments;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public class MethaneCore extends MethaneModule {

    public MethaneCore() {
        super(false,
                new MethaneModule.Metadata("Methane Core","0.0.1-prototype", new String[]{"AnOpenSauceDev"},"","","The absolute core of Methane. Contains all optimizations and a very barebones config if not using a module that supplies it.", MethaneModule.Metadata.Category.OPTIMIZATION,"[change later]"),"methane_core");

        new ModuleFlag<>("config_supplier",this,this); // automatically assigns
    }

    Service<Screen> getFallbackScreenService = new Service<Screen>(Identifier.of("methane_core","fallbackconfig"),((values)-> {return new FallbackConfig(); }),new Service.MultiThreadingOptions(true,false,true,true),this);

    @Override
    public void init(boolean toggleable, Service<?>... services) {

        MODULE_LOGGER.info("core loaded!");
        addServices(getFallbackScreenService,ConfigHandler.ConfigWriterService);
        ConfigHandler.ConfigWriterService.executeService(new DynamicArguments().putArg(this).putArg("test_config").putArg(new TestConfig()));

    }

    public boolean useFallbackConfig(){
        return (MODULE_FLAGS.get("config_supplier").getFlagValue() instanceof MethaneModule);
    }


}
