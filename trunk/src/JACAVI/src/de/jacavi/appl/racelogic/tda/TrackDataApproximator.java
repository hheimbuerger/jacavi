package de.jacavi.appl.racelogic.tda;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;



abstract public class TrackDataApproximator {

    protected final Track track;

    protected TrackDataApproximator(Track track) {
        this.track = track;
    }

    public abstract int determineNewPosition(int gametick, int position, ControllerSignal controllerSignal,
            FeedbackSignal feedbackSignal);

}
