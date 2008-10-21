package de.jacavi.appl.racelogic.tda;

import de.jacavi.appl.car.Car;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;



public class BlueriderTDA extends TrackDataApproximator {

    public BlueriderTDA(Player player, Track track, int racetimerIntreval) {
        this.player = player;
        this.track = track;
        this.raceTimerInterval = racetimerIntreval;
    }

    /**
     * TODO [ticket #175] implement me.
     */
    @Override
    public void updatePosition(CarPosition carPosition, int gametick, Car car, ControllerSignal controllerSignal,
            FeedbackSignal feedbackSignal) {

    }

}
