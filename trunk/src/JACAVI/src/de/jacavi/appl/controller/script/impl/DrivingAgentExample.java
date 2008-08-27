package de.jacavi.appl.controller.script.impl;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.script.DrivingAgentController;



public class DrivingAgentExample extends DrivingAgentController {

    public DrivingAgentExample() {
        super("DEBUG"); // FIXME: name should be taken from constructor argument
    }

    @Override
    public void cleanup() {
    // TODO Auto-generated method stub

    }

    @Override
    public boolean initialize() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ControllerSignal poll() {
        // TODO Auto-generated method stub
        return null;
    }

}
