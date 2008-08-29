package de.jacavi.appl.racelogic.tda;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;



public class DebugTDA extends TrackDataApproximator {

    public DebugTDA(Track track) {
        super(track);
    }

    @Override
    public int determineNewPosition(int gametick, int position, ControllerSignal controllerSignal,
            FeedbackSignal feedbackSignal) {
        return position + 1;
    }

}
