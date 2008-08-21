package de.jacavi.appl.controller.device.impl;

import org.eclipse.swt.widgets.Composite;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.device.DeviceController;



public class MouseDeviceController implements DeviceController {

    @Override
    public boolean initialize(Composite guiElement) {
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
