package de.jacavi.appl.racelogic;

import java.util.Timer;
import java.util.TimerTask;

import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.hal.TechnologyController;
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
        this.raceView = raceView;
        // create new timer feed with RaceTimerTask and start it
        raceTimer = new Timer();
        raceTimer.schedule(new RaceTimerTask(), 0, raceTimerInterval);
    }

    /**
     * Stop the RaceTimerTask
     */
    public void stopRaceTimer() {
        raceTimer.cancel();
    }

    /**
     * Inner Class
     * <p>
     * Represents the RaceTimer polls the InputDevices and gives the signal to HAL and RaceView
     * 
     * @author fro
     */
    class RaceTimerTask extends TimerTask {

        @Override
        public void run() {
            for(Player player: race.getPlayers()) {
                int carID = player.getId();
                CarController carController = player.getController();
                TechnologyController technologyController = player.getTechnologyController();
                ControllerSignal signal = carController.poll();
                // change track
                if(signal.isTrigger())
                    technologyController.toggleSwitch(carID);
                // set new speed
                technologyController.setSpeed(carID, signal.getSpeed());
                // repaint the RaceView
                raceView.repaint();
            }
        }

    }

}