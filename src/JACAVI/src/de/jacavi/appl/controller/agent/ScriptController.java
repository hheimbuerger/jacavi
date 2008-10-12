package de.jacavi.appl.controller.agent;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.rcp.util.ExceptionHandler;



abstract public class ScriptController extends DrivingAgentController {
    @SuppressWarnings("serial")
    public class ScriptExecutionException extends Exception {
        public ScriptExecutionException(String message) {
            super(message);
        }
    }

    private final File agentFile;

    private DrivingAgent scriptObject;

    public ScriptController(String name, File agentFile) {
        super(name);
        this.agentFile = agentFile;
    }

    public File getAgentFile() {
        return agentFile;
    }

    @Override
    public ControllerSignal poll() {
        if(scriptObject == null)
            return null;
        else
            return scriptObject.poll(/*null, null, null*/);
    }

    @Override
    public void activate() {
        if(scriptObject == null)
            try {
                scriptObject = parseScript();
            } catch(Exception e) {
                ExceptionHandler.handleException(this, e, true);
            }
    }

    private DrivingAgent parseScript() throws Exception {
        return compile(agentFile);
    }

    @Override
    public void deactivate() {
        if(scriptObject != null)
            scriptObject = null;
    }

    public ControllerSignal dryRun() throws ScriptExecutionException {
        try {
            return parseScript().poll();
        } catch(Exception exc) {
            final Writer writer = new StringWriter();
            exc.printStackTrace(new PrintWriter(writer));
            throw new ScriptExecutionException(exc.toString());
        }
    }

    abstract protected DrivingAgent compile(File agentFile) throws Exception;
}
