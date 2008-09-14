package de.jacavi.appl.racelogic.tda;

import de.jacavi.appl.car.Car;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;



abstract public class TrackDataApproximator {

    public abstract void updatePosition(CarPosition carPosition, int gametick, Car car, Track track,
            ControllerSignal controllerSignal, FeedbackSignal feedbackSignal);

}
