package de.jacavi.appl.racelogic;

import java.util.Timer;
import java.util.TimerTask;

import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.hal.TechnologyController;



public class RaceEngine extends TimerTask {

    private Race race = null;

    private Timer raceTimer = null;

    private int raceTimerInterval = 0;

    public Timer getRaceTimer() {
        return raceTimer;
    }

    public void setRaceTimerInterval(int raceTimerInterval) {
        this.raceTimerInterval = raceTimerInterval;
    }

    public RaceEngine(Race race) {
        // preconditions
        if(race == null)
            throw new IllegalArgumentException("race may not be null");

        // needs
        this.race = race;
        raceTimer = new Timer();
        startTimer();
    }

    private void startTimer() {
        raceTimer.schedule(this, 0, raceTimerInterval);
    }

    public void stopTimer() {
        raceTimer.cancel();
    }

    @Override
    public void run() {

        for(Player player: race.getPlayers()) {
            int carID = player.getId();
            CarController carController = player.getController();
            TechnologyController technologyController = player.getTechnologyController();
            ControllerSignal signal = carController.poll();
            // change track
            if(signal.trigger)
                technologyController.toggleSwitch(carID);
            // set new speed
            technologyController.setSpeed(carID, signal.speed);
        }
    }
}