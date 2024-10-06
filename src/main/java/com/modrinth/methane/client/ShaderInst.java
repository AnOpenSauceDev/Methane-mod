package com.modrinth.methane.client;

import net.minecraft.client.gl.ShaderProgram;

public class ShaderInst {

    public ShaderInst(ShaderProgram prog){
        program = prog;
    }

    private ShaderProgram program;

    public ShaderProgram getProgram(){
        return program;
    }

    public void setProgram(ShaderProgram shader){
        program = shader;
    }

}
