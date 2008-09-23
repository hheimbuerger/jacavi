package de.jacavi.appl.controller.agent;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;
import org.python.core.PyClass;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.rcp.util.ExceptionHandler;



public class DrivingAgentController extends CarController {
    public enum ScriptType {
        PYTHON, GROOVY
    }

    private ScriptType scriptType;

    private final File agentFile;

    private DrivingAgent scriptObject;

    public DrivingAgentController(String name, File agentFile) {
        super(name);
        this.agentFile = agentFile;

        if(agentFile.getName().endsWith(".py"))
            scriptType = ScriptType.PYTHON;
        else if(agentFile.getName().endsWith(".groovy"))
            scriptType = ScriptType.GROOVY;
        else
            throw new RuntimeException("Unknown file extension for driving agent file.");
    }

    public ScriptType getScriptType() {
        return scriptType;
    }

    public File getAgentFile() {
        return agentFile;
    }

    @Override
    public ControllerSignal poll() {
        return scriptObject.poll(/*null, null, null*/);
    }

    @Override
    public void activate() {
        if(scriptObject == null)
            try {
                switch(scriptType) {
                    case PYTHON:
                        scriptObject = getPythonImplementation(agentFile);
                        break;
                    case GROOVY:
                        scriptObject = getGroovyImplementation(agentFile);
                        break;
                    default:
                        throw new RuntimeException("unknown script type");
                }
            } catch(Exception e) {
                ExceptionHandler.handleException(e, true);
            }
    }

    private static DrivingAgent getGroovyImplementation(File scriptFile) throws CompilationFailedException,
            IOException, InstantiationException, IllegalAccessException {
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class<?> agentClass = gcl.parseClass(scriptFile);
        Object wrapperObject = agentClass.newInstance();
        DrivingAgent agentInstance = (DrivingAgent) wrapperObject;

        return agentInstance;
    }

    private static DrivingAgent getPythonImplementation(File scriptFile) throws FileNotFoundException {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.set("DrivingAgent", DrivingAgent.class);
        interpreter.set("ControllerSignal", ControllerSignal.class);
        // interpreter.set("this", Main.class);
        interpreter.execfile(new FileInputStream(scriptFile));
        PyClass agentClass = (PyClass) interpreter.get("MyAgent");
        // PyObject locals = interpreter.getLocals();

        PyObject agentInstance = agentClass.__call__();
        Object wrapperObject = agentInstance.__tojava__(DrivingAgent.class);
        DrivingAgent script = (DrivingAgent) wrapperObject;
        return script;
    }
}
