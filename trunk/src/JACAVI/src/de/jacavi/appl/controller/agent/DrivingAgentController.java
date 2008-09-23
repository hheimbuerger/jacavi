package de.jacavi.appl.controller.agent;

import java.io.File;

import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.ControllerSignal;



public class DrivingAgentController extends CarController {
    public enum ScriptType {
        PYTHON, GROOVY
    }

    private ScriptType scriptType;

    private final File agentFile;

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
        return new ControllerSignal(60, false);
    }
}
