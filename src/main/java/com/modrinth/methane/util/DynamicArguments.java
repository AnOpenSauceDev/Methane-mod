package com.modrinth.methane.util;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;

public class DynamicArguments {



    List<Object> arguments = new ArrayList<>();


    // actual warcrime, what the HELL is this
    public <T> DynamicArguments putArg(T object){
        arguments.add(object);
        return this;
    }


    @SuppressWarnings("unchecked")
    public <T> T getArg(int pos, Class<T> cls){

        var value = arguments.get(pos);

        if(value.getClass() == cls){
            return (T) value;
        }else {
            return null;
        }
    }

    // "testing in prod" edition
    @SuppressWarnings("unchecked")
    public <T> T getArg(int pos){

        var value = arguments.get(pos);

        return (T) value;
    }

}
