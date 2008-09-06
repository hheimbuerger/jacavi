package de.jacavi.appl.racelogic.tda;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;



public class DebugTDA extends TrackDataApproximator {

    @Override
    public void updatePosition(CarPosition carPosition, int gametick, Track track, ControllerSignal controllerSignal,
            FeedbackSignal feedbackSignal) {
        carPosition.stepsFromStart = (carPosition.stepsFromStart + 1) % track.getLaneLength(carPosition.currentLane);
    }

}
