package de.jacavi.appl.controller.agent.impl;

import groovy.lang.GroovyClassLoader;

import java.io.File;

import de.jacavi.appl.controller.agent.DrivingAgent;
import de.jacavi.appl.controller.agent.ScriptController;



public class GroovyScriptController extends ScriptController {

    public GroovyScriptController(String name, File agentFile) {
        super(name, agentFile);
    }

    @Override
    protected DrivingAgent compile(File agentFile) throws Exception {
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class<?> agentClass = gcl.parseClass(agentFile);
        Object wrapperObject = agentClass.newInstance();
        DrivingAgent agentInstance = (DrivingAgent) wrapperObject;

        return agentInstance;
    }

}
