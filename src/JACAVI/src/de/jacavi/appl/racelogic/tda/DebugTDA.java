package de.jacavi.appl.racelogic.tda;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;



public class DebugTDA extends TrackDataApproximator {
    private static Log logger = LogFactory.getLog(DebugTDA.class);

    @Override
    public void updatePosition(CarPosition carPosition, int gametick, Track track, ControllerSignal controllerSignal,
            FeedbackSignal feedbackSignal) {
        if(feedbackSignal.getGforce() != null)
            logger.debug("Sensor: " + feedbackSignal.getLastCheckpoint() + " GForce: "
                    + feedbackSignal.getGforce().getX() + " " + feedbackSignal.getGforce().getY());
        else
            logger.debug("Sensor: " + feedbackSignal.getLastCheckpoint());
        carPosition.stepsFromStart = (carPosition.stepsFromStart + 2) % track.getLaneLength(carPosition.currentLane);
    }

}
