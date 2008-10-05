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

    private int lastGametick = 0;

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
        /*else
            logger.debug("Sensor: " + feedbackSignal.getLastCheckpoint());*/

        // adjusted speed: between -50 and +50
        double adjustedSpeed = ((double) controllerSignal.getSpeed() / 100 - 0.5);

        // acceleration = speed * car acceleration factor
        double acceleration = adjustedSpeed * car.getAcceleration();

        // speed = old speed + acceleration * num adjustments (but limited to 0..topspeed)
        speed = Math.max(Math.min(speed + acceleration * (gametick - lastGametick), car.getTopSpeed()), 0);

        // determine the new position;
        int stepsToMove = ((int) speed) / 10;

        /*logger.debug("acceleration (" + acceleration + "), speed (" + speed + "), stepsToMove (" + stepsToMove
                + "), topSpeed (" + car.getTopSpeed() + ")");*/

        // move
        carPosition.moveSteps(track, stepsToMove, controllerSignal.isTrigger());

        lastGametick = gametick;
    }
}
