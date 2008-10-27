package de.jacavi.appl.controller.agent.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.python.core.PyClass;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.agent.DrivingAgent;
import de.jacavi.appl.controller.agent.ScriptController;



public class JythonScriptController extends ScriptController {

    public JythonScriptController(String name, File agentFile) {
        super(name, agentFile);
    }

    @Override
    protected DrivingAgent compile(File agentFile) throws FileNotFoundException {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.set("DrivingAgent", DrivingAgent.class);
        interpreter.set("ControllerSignal", ControllerSignal.class);
        // interpreter.set("this", Main.class);
        interpreter.execfile(new FileInputStream(agentFile));
        PyClass agentClass = (PyClass) interpreter.get("MyAgent");
        // PyObject locals = interpreter.getLocals();

        PyObject agentInstance = agentClass.__call__();
        Object wrapperObject = agentInstance.__tojava__(DrivingAgent.class);
        DrivingAgent script = (DrivingAgent) wrapperObject;
        return script;
    }

}
