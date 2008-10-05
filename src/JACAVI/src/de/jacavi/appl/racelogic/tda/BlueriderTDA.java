package de.jacavi.appl.racelogic.tda;

import de.jacavi.appl.car.Car;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;



public class BlueriderTDA extends TrackDataApproximator {

    public BlueriderTDA(Track track, int racetimerIntreval) {
        this.track = track;
        this.raceTimerInterval = racetimerIntreval;
    }

    @Override
    public void updatePosition(CarPosition carPosition, int gametick, Car car, ControllerSignal controllerSignal,
            FeedbackSignal feedbackSignal) {
    // TODO Auto-generated method stub

    }

}
