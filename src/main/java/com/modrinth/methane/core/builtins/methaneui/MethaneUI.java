package com.modrinth.methane.core.builtins.methaneui;

import com.modrinth.methane.core.MethaneModule;

public class MethaneUI extends MethaneModule {

    public MethaneUI() {
        super(false,
                new MethaneModule.Metadata("Methane Config","0.0.1-prototype", new String[]{"AnOpenSauceDev"},"","","builtin config and UI system for Methane.", MethaneModule.Metadata.Category.OPTIMIZATION,"[change later]"),"methane_ui");

        new ModuleFlag<>("use_fallback_config_supplier",false,this); // automatically assigns
    }


    @Override
    public void init(boolean toggleable, Service<?>... services) {

    }

}
