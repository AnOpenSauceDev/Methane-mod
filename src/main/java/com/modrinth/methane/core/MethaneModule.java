package com.modrinth.methane.core;

import com.modrinth.methane.util.DynamicArguments;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public abstract class MethaneModule {


    public Logger MODULE_LOGGER;

    public HashMap<String,ModuleFlag<?>> MODULE_FLAGS = new HashMap<>();

    public List<Service<?>> services = new ArrayList<>();

    public void addServices(Service<?>... srvcs){
        services.addAll(Arrays.asList(srvcs));
    }

    public static class ModuleFlag<T>{

        MethaneModule PARENT;

        private String flagName;

        private T FlagValue;

        public ModuleFlag(String name, T flagValue,MethaneModule parent){
            this.FlagValue = flagValue;
            this.flagName = name;
            this.PARENT = parent;

            parent.MODULE_FLAGS.put(flagName,this);

        }

        public String getFlagName(){
            return flagName;
        }

        public String setFlagName(String replacement){
            return flagName = replacement;
        }

        public T getFlagValue(){
            return FlagValue;
        }

        public T setFlagValue(T replacement){
            return FlagValue = replacement;
        }


    }

    public abstract void init(boolean toggleable, Service<?>... services);

    public Metadata moduleMetadata;
    public String path;

    /**
     *  Defines a module for usage with Methane's config.
     * @param toggleable whether the **WHOLE** module can be disabled.
     * @param metadata
     * @param services
     */
    public MethaneModule(boolean toggleable,Metadata metadata, String pathName, Service<?>... services){
        MODULE_LOGGER = LoggerFactory.getLogger(metadata.NAME);
        this.moduleMetadata = metadata;
        this.path = pathName;
        init(toggleable,services);
    }


    public static class Metadata{

        public enum Category{
            OPTIMIZATION,
            QOL,
            FIX,
            SCRIPT_EXTENSION,
            OTHER
        }

        public String NAME = "undefined";
        public String VERSION = "undefined";
        public String[] AUTHORS = {"none provided!"};
        public String SOURCE = "no source code url provided!";
        public String ISSUES = "no issue tracker url provided!";
        public String DESCRIPTION = "no issue tracker url provided!";
        public String LICENSE = "Presumed ARR";
        public Category CATEGORY = Category.OTHER;

        public Metadata(String name,String version, String[] authors, String sourceURL, String issuesURL, String description, Category category, String license){
            this.NAME = name;
            this.VERSION = version;
            this.AUTHORS = authors;
            this.SOURCE = sourceURL;
            this.ISSUES = issuesURL;
            this.DESCRIPTION = description;
            this.CATEGORY = category;
            this.LICENSE = license;
        }

    }

    public static class Service<Data> {

        public MethaneModule PARENT;

        public enum TaskStatus{
            RUNNING, // task is still processing/locked. Currently, no way to detect a freeze.
            WAITING_TO_START,
            PAUSED, // task execution manually halted (NYI)
            SUCCESS,
            FAIL,
            FAIL_MULTIPLE_INSTANCES
        }

        Identifier name;
        private Function<DynamicArguments,Data> task;
        MultiThreadingOptions options;

        int instancesRunning = 0;

        public Service(Identifier name, Function<DynamicArguments,Data> onRun, MultiThreadingOptions multiThreadOptions, MethaneModule parent){
            this.name = name;
            this.task = onRun;
            this.options = multiThreadOptions;
            this.PARENT = parent;
        }


        public AtomicReference<Pair<TaskStatus, Data>> executeService(DynamicArguments inputValues){

            AtomicReference<Pair<TaskStatus,Data>> output = new AtomicReference<>();

            instancesRunning++;
            if(options.multithreaded){

                if(options.onlyOneInstance && instancesRunning > 1){
                    output.set(new Pair<>(TaskStatus.FAIL_MULTIPLE_INSTANCES,null));
                    return output;
                }

                output.set(new Pair<>(TaskStatus.RUNNING,null));

                // repack task
                var thread = new Thread(
                        () -> {
                            try {
                                output.set(new Pair<>(TaskStatus.SUCCESS,task.apply(inputValues)));
                            }catch (Exception fail){
                                PARENT.MODULE_LOGGER.info("EXCEPTION IN SERVICE THREAD: {}", Thread.currentThread().getName());
                                fail.printStackTrace();
                                output.set(new Pair<>(TaskStatus.FAIL, null));
                            }
                            instancesRunning--;
                        });

                thread.setPriority((options.urgent) ? 7 : 5);
                thread.start();
            }else {
                try{
                    output.set(new Pair<>(TaskStatus.SUCCESS,task.apply(inputValues)));
                }catch (Exception ex){

                    PARENT.MODULE_LOGGER.info("EXCEPTION IN MAIN THREAD!");
                    ex.printStackTrace();
                }
                instancesRunning--;
            }

            return output;

        }

        public static class MultiThreadingOptions {

            public MultiThreadingOptions(boolean threaded, boolean queue, boolean oneTask, boolean urgent){
                this.multithreaded = threaded;
                this.queueForTask = queue;
                this.onlyOneInstance = oneTask;
                this.urgent = urgent;
            }

            boolean multithreaded,queueForTask,onlyOneInstance,urgent;

        }

    }

}
