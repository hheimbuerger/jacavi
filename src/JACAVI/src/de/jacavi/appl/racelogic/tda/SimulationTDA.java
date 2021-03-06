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

    @Override
    public void updatePosition(CarPosition carPosition, int gametick, Car car, ControllerSignal controllerSignal,
            FeedbackSignal feedbackSignal) {
        // log a bit
        if(feedbackSignal.getGforce() != null)
            logger.debug("Sensor: " + feedbackSignal.getCheckpoint() + " GForce: " + feedbackSignal.getGforce().getX()
                    + " " + feedbackSignal.getGforce().getY());

        // car acceleration factor is defined in car.xml
        double thrust = controllerSignal.getThrust() * car.getAcceleration();

        // calculate the friction 0.01 is car on concrete
        double friction = (car.getMass() * 0.01) * -1;

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

    }

    /**
     * Get the maximum speed this car can get in steps/gametick
     * <p>
     * 
     * @param controllerSignal
     *            The input ControlerSignal
     * @param car
     *            The current car
     * @return steps/gametick max speed
     */
    private double getMaxSpeed(int controllerSignal, Car car) {
        double result = 0;

        result = (car.getTopSpeed() / 100) * controllerSignal;

        return result;
    }
}
