package de.jacavi.appl.racelogic;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.racelogic.tda.TrackDataApproximator;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarSystemConnector;
import de.jacavi.rcp.util.Check;
import de.jacavi.rcp.views.RaceView;



/**
 * @author fro
 */
public class RaceEngine {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(RaceEngine.class);

    private RaceView raceView = null;

    private Timer raceTimer = null;

    private int raceTimerInterval = 0;

    boolean isTimerRunning = false;

    private Track track;

    private List<Player> players;

    public RaceEngine() {}

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setRaceTimerInterval(int raceTimerInterval) {
        this.raceTimerInterval = raceTimerInterval;
    }

    /**
     * Start the RaceTimerTask
     */
    public void startRace(Track activeTrack, RaceView raceView) {
        Check.Require(activeTrack != null, "activeTrack may not be null");
        Check.Require(raceView != null, "raceView may not be null");

        if(!isTimerRunning) {
            this.track = activeTrack;
            this.raceView = raceView;

            // create new timer feed with RaceTimerTask and start it
            raceTimer = new Timer();
            raceTimer.schedule(new RaceTimerTask(), 0, raceTimerInterval);
            isTimerRunning = true;

            // TODO: assign all cars to their starting position
            int i = 0;
            for(Player player: players)
                player.getPosition().reset(i++ % track.getTileset().getLaneCount());

            // prepare devices
            for(Player player: players)
                player.getController().activate();
        } else {
            logger.error("RaceEngine.startRace() was invoked but timer was already running. Race was *not* started!");
        }
    }

    /**
     * Stop the RaceTimerTask
     */
    public void stopRace() {
        if(isTimerRunning) {
            // disorganize devices
            for(Player player: players) {
                player.getController().deactivate();
            }
            raceTimer.cancel();
            isTimerRunning = false;
        } else {
            logger.error("RaceEngine.stopRace() was invoked but timer was not running. Race was *not* stopped!");
        }
    }

    /**
     * Inner Class
     * <p>
     * Represents the RaceTimer polls the InputDevices and gives the signal to HAL and RaceView
     * 
     * @author fro
     */
    class RaceTimerTask extends TimerTask {

        int gametick = 0;

        @Override
        public void run() {
            for(Player player: players) {
                // get the players CarController
                CarController carController = player.getController();
                // get players hal connector
                SlotCarSystemConnector slotCarSystemConnector = player.getSlotCarSystemConnector();
                // get players controller signal
                ControllerSignal controllerSignal = carController.poll();
                // get the hal feedback signal
                FeedbackSignal feedbackSignal = slotCarSystemConnector.pollFeedback();
                // change track
                if(controllerSignal.isTrigger())
                    slotCarSystemConnector.toggleSwitch();
                // set new speed
                slotCarSystemConnector.setSpeed(controllerSignal.getSpeed());
                // invoke the TDA
                TrackDataApproximator tda = player.getTda();
                tda.updatePosition(player.getPosition(), gametick, track, controllerSignal, feedbackSignal);
                // repaint the RaceView
                raceView.repaint();
            }
            gametick++;
        }
    }
}