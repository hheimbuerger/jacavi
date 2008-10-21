package de.jacavi.appl.racelogic.tda;

import de.jacavi.appl.car.Car;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;



public class AnalogueTDA extends TrackDataApproximator {

    public AnalogueTDA(Player player, Track track, int racetimerInterval) {
        this.player = player;
        this.track = track;
        this.raceTimerInterval = racetimerInterval;
    }

    /**
     * TODO [ticket #174] implement me if there is analogue hal support available
     */
    @Override
    public void updatePosition(CarPosition carPosition, int gametick, Car car, ControllerSignal controllerSignal,
            FeedbackSignal feedbackSignal) {

    }
}
