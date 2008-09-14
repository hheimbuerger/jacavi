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

    @Override
    public void updatePosition(CarPosition carPosition, int gametick, Car car, Track track,
            ControllerSignal controllerSignal, FeedbackSignal feedbackSignal) {
        // log a bit
        if(feedbackSignal.getGforce() != null)
            logger.debug("Sensor: " + feedbackSignal.getLastCheckpoint() + " GForce: "
                    + feedbackSignal.getGforce().getX() + " " + feedbackSignal.getGforce().getY());
        /*else
            logger.debug("Sensor: " + feedbackSignal.getLastCheckpoint());*/

        // determine acceleration and speed
        double acceleration = ((double) controllerSignal.getSpeed() / 100 - 0.5) * car.getAcceleration();
        speed = Math.max(Math.min(speed + acceleration * (gametick - lastGametick), car.getTopSpeed()), 0);

        // determine the new position;
        int stepsToMove = ((int) speed) / 10;

        logger.debug("acceleration (" + acceleration + "), speed (" + speed + "), stepsToMove (" + stepsToMove
                + "), topSpeed (" + car.getTopSpeed() + ")");

        // move
        carPosition.moveSteps(stepsToMove, track.getLaneLength(carPosition.currentLane));

        lastGametick = gametick;
    }
}
