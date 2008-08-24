package de.jacavi.appl.controller.device.impl;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.device.DeviceController;



public class KeyboardDevice extends DeviceController {
    public KeyboardDevice(String name) {
        super(name);
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
        // FIXME: Returning a fixed signal for testing here!
        return new ControllerSignal(50, false);
    }

    @Override
    public int normaliseSpeedSignal(float deviceSpecificInputSpeedSignal) {
        // TODO Auto-generated method stub
        return 0;
    }

}
