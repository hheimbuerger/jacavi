package de.jacavi.appl.controller.device.impl;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.device.DeviceController;



public class MouseDevice extends DeviceController {
    public MouseDevice(String name) {
        super(name);
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

    @Override
    public void cleanup() {
    // TODO Auto-generated method stub

    }

    @Override
    public int normaliseSpeedSignal(float deviceSpecificInputSpeedSignal) {
        // TODO Auto-generated method stub
        return 0;
    }

}
