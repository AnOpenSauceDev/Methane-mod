package com.modrinth.methane.core.builtins.methanecore;

import com.modrinth.methane.Methane;
import com.modrinth.methane.core.MethaneModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;

public class ConfigHandler {


    public static final boolean IS_CLIENT = true; // true until I get something set up properly


    //available in the flavour of a function...

    public static boolean writeConfig(MethaneModule sender, String configFileName, Object classToWrite){



        File configFile;
        FileWriter writer;

        if(IS_CLIENT){
            configFile = MinecraftClient.getInstance().runDirectory.toPath().resolve("config/" + sender.path + "/" + configFileName + ".methanecfg").toFile();


            configFile.mkdirs();

            if(configFile.exists()){
                configFile.delete();
            }



        }else {
            sender.MODULE_LOGGER.error("The server config isn't implemented yet! Scram!");
            return false;
        }

        try {
            if (!configFile.createNewFile()) {
                throw new Exception("uhh... the file didn't file!");
            }

            writer = new FileWriter(configFile);

            for (Field field : classToWrite.getClass().getFields()){ /// NOTE: The fields you want to serialize must be public!
                if(field.get(classToWrite) instanceof Number number){
                    String data = field.getName() + ": " + field.getGenericType().getTypeName() + ", " + number + "\n";
                    writer.append(data);
                }

                if(field.get(classToWrite) instanceof Boolean bool){
                    String data = field.getName() + ": " + field.getGenericType().getTypeName() + ", " + bool + "\n";
                    writer.append(data);
                }

                if(field.get(classToWrite) instanceof String string){
                    String data = field.getName() + ": " + field.getGenericType().getTypeName() + ", \"" + string +"\"" + "\n";
                    writer.append(data);
                }

                if(field.get(classToWrite) instanceof byte[] bytes){

                    StringBuilder binString = new StringBuilder();

                    for (byte portion : bytes){
                        binString.append((char)portion);
                        //binString.append(",");
                    }

                    String data = field.getName() + ": BIN, " + binString + "\n";
                    writer.append(data);
                }

            }

            writer.close();

        }catch (Exception e){
            e.printStackTrace();

        }
        return  true;
    }

    // or a dangerous weapon of mass destruction!

    public static MethaneModule.Service<?> ConfigWriterService = new MethaneModule.Service<Object>(Identifier.of("methane_core","config_writer_service"),((values) -> ConfigHandler.writeConfig(values.getArg(0),values.getArg(1),values.getArg(2))), new MethaneModule.Service.MultiThreadingOptions(true,true,false,false),Methane.METHANE_CORE);

}
