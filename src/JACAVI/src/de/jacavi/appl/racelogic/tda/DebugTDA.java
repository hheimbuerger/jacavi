package de.jacavi.appl.racelogic.tda;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jacavi.appl.car.Car;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;



public class DebugTDA extends TrackDataApproximator {
    private static Log logger = LogFactory.getLog(DebugTDA.class);

    private double speed = 0;

    private double acceleration = 0.0;

    public DebugTDA(Track track, int racetimerInterval) {
        this.track = track;
        this.raceTimerInterval = racetimerInterval;
    }

    @Override
    public void updatePosition(CarPosition carPosition, int gametick, Car car, ControllerSignal controllerSignal,
            FeedbackSignal feedbackSignal) {
        // log a bit
        if(feedbackSignal.getGforce() != null)
            logger.debug("Sensor: " + feedbackSignal.getLastCheckpoint() + " GForce: "
                    + feedbackSignal.getGforce().getX() + " " + feedbackSignal.getGforce().getY());

        // car acceleration factor is defined in car.xml
        double thrust = controllerSignal.getSpeed() * car.getAcceleration();

        // calculate the friction 0.01 is car on concrete
        double friction = (car.getMass() * 0.01) * -1;

        if(carPosition.isOnTrack) {
            acceleration = thrust / car.getMass();
            speed = Math.max(Math.min((acceleration * raceTimerInterval) + (friction * raceTimerInterval) + speed, car
                    .getTopSpeed()), 0);

            if(speed > getMaxSpeed(controllerSignal.getSpeed(), car)) {
                if(getMaxSpeed(controllerSignal.getSpeed(), car) == 0)
                    speed = speed--;
                else
                    speed = getMaxSpeed(controllerSignal.getSpeed(), car);
            }

            if(speed > car.getTopSpeed() * 0.9)
                carPosition.leaveTrack();
            else
                // move
                carPosition.moveSteps(track, (int) speed, controllerSignal.isTrigger());
        }
    }

    private double getMaxSpeed(int controllerSignal, Car car) {
        double result = 0;

        result = (car.getTopSpeed() / 100) * controllerSignal;

        return result;
    }
}
