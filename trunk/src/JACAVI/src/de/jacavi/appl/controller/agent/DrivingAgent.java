package de.jacavi.appl.controller.agent;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.appl.track.Track;



public interface DrivingAgent {
    ControllerSignal poll(CarPosition you, Player[] others, Track track);
}
