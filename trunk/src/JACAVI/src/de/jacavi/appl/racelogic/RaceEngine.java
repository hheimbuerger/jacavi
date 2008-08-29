package de.jacavi.appl.racelogic;

import java.util.Timer;
import java.util.TimerTask;

import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.racelogic.tda.TrackDataApproximator;
import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarSystemConnector;
import de.jacavi.rcp.util.Check;
import de.jacavi.rcp.views.RaceView;



/**
 * @author fro
 */
public class RaceEngine {

    private Race race = null;

    private RaceView raceView = null;

    private Timer raceTimer = null;

    private int raceTimerInterval = 0;

    boolean isTimerRunning = false;

    public RaceEngine(Race race) {
        // preconditions
        Check.Require(race != null, "race may not be null");

        // needs
        this.race = race;

    }

    public void setRaceTimerInterval(int raceTimerInterval) {
        this.raceTimerInterval = raceTimerInterval;
    }

    /**
     * Start the RaceTimerTask
     */
    public void startRaceTimer(RaceView raceView) {
        // TODO: check if the require is correct. Whats if we start a game without a RaceView only hardware
        Check.Require(raceView != null, "raceView may not be null");
        if(!isTimerRunning) {
            this.raceView = raceView;
            // create new timer feed with RaceTimerTask and start it
            raceTimer = new Timer();
            raceTimer.schedule(new RaceTimerTask(), 0, raceTimerInterval);
            isTimerRunning = true;
        }
    }

    /**
     * Stop the RaceTimerTask
     */
    public void stopRaceTimer() {
        if(isTimerRunning) {
            raceTimer.cancel();
            isTimerRunning = false;
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
            for(Player player: race.getPlayers()) {
                int carID = player.getId();
                CarController carController = player.getController();
                SlotCarSystemConnector slotCarSystemConnector = player.getSlotCarSystemConnector();
                ControllerSignal controllerSignal = carController.poll();
                FeedbackSignal feedbackSignal = slotCarSystemConnector.pollFeedback();
                // change track
                if(controllerSignal.isTrigger())
                    slotCarSystemConnector.toggleSwitch(carID);
                // set new speed
                slotCarSystemConnector.setSpeed(carID, controllerSignal.getSpeed());
                // invoke the TDA
                TrackDataApproximator tda = player.getTda();
                player.setPosition(tda.determineNewPosition(gametick, player.getPosition(), controllerSignal,
                        feedbackSignal));
                // repaint the RaceView
                raceView.repaint();
            }
            gametick++;
        }

    }

    public Race getRace() {
        return race;
    }

}