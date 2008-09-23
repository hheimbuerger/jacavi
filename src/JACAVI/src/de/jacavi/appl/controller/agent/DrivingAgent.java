package de.jacavi.appl.controller.agent;

import de.jacavi.appl.controller.ControllerSignal;



public interface DrivingAgent {
    ControllerSignal poll(/*CarPosition you, CarPosition[] others, Track track*/);
}
