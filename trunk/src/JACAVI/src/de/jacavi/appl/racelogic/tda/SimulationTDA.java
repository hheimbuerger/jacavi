package de.jacavi.appl.racelogic.tda;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jacavi.appl.car.Car;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;



public class SimulationTDA extends TrackDataApproximator {
    private static Log logger = LogFactory.getLog(SimulationTDA.class);

    public SimulationTDA(Player player, Track track, int racetimerInterval) {
        this.player = player;
        this.track = track;
        this.raceTimerInterval = racetimerInterval;
    }

    /*
    // reset
    if(controllerSignal.isReset()) {
        player.getController().reset();
        slotCarSystemConnector.setThrust(0);
        player.getPosition().reset(track.getStartingPoints()[i]);
    }
    */

    @Override
    public void updatePosition(CarPosition carPosition, int gametick, Car car, ControllerSignal controllerSignal,
            FeedbackSignal feedbackSignal) {
        // log a bit
        if(feedbackSignal.getGforce() != null)
            logger.debug("Sensor: " + feedbackSignal.getLastCheckpoint() + " GForce: "
                    + feedbackSignal.getGforce().getX() + " " + feedbackSignal.getGforce().getY());

        // car acceleration factor is defined in car.xml
        double thrust = controllerSignal.getThrust() * car.getAcceleration();

        // calculate the friction 0.01 is car on concrete
        double friction = (car.getMass() * 0.01) * -1;

        if(carPosition.isOnTrack) {
            acceleration = thrust / car.getMass();
            speed = Math.max(Math.min((acceleration * raceTimerInterval) + (friction * raceTimerInterval) + speed, car
                    .getTopSpeed()), 0);

            if(speed > getMaxSpeed(controllerSignal.getThrust(), car)) {
                if(getMaxSpeed(controllerSignal.getThrust(), car) == 0)
                    speed = speed--;
                else
                    speed = getMaxSpeed(controllerSignal.getThrust(), car);
            }

            // car will crash if thrust is 90% of full speed
            if(speed > car.getTopSpeed() * 0.9)
                carPosition.leaveTrack();
            else
                // move
                carPosition.moveSteps(track, (int) speed, controllerSignal.isTrigger());
        } else {
            if(controllerSignal.isReset()) {
                resetCar();
            }
        }
    }

    private double getMaxSpeed(int controllerSignal, Car car) {
        double result = 0;

        result = (car.getTopSpeed() / 100) * controllerSignal;

        return result;
    }
}
