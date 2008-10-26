package de.jacavi.appl.controller.agent;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.Track;
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

    private ControllerSignal lastSignal;

    private Track track;

    private List<Player> players;

    public ScriptController(String name, File agentFile) {
        super(name);
        this.agentFile = agentFile;
    }

    public File getAgentFile() {
        return agentFile;
    }

    // @SuppressWarnings("unchecked")
    @Override
    public ControllerSignal poll() {
        if(scriptObject == null)
            return null;
        else {
            // determine the arguments to pass to the script
            Player you = null;
            List<Player> others = new ArrayList<Player>();
            for(Player p: players) {
                if(p.getController() == this) {
                    assert you == null: "Error: this controller was apparently assigned to multiple players";
                    you = p;
                } else
                    others.add(p);
            }
            lastSignal = scriptObject.poll(you.getPosition(), others.toArray(new Player[others.size()]), track);
            return lastSignal;
        }
    }

    @Override
    public ControllerSignal getLastSignal() {
        return lastSignal;
    }

    @Override
    public void activate(Track track, List<Player> players) {
        this.track = track;
        this.players = players;
        this.lastSignal = new ControllerSignal();

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
        track = null;
        players = null;
        lastSignal = null;
    }

    public ControllerSignal dryRun() throws ScriptExecutionException {
        try {
            return parseScript().poll(null, null, null);
        } catch(Exception exc) {
            final Writer writer = new StringWriter();
            exc.printStackTrace(new PrintWriter(writer));
            throw new ScriptExecutionException(exc.toString());
        }
    }

    abstract protected DrivingAgent compile(File agentFile) throws Exception;
}
