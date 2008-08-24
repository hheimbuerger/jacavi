package de.jacavi.appl.controller.device.impl;

import com.centralnexus.input.Joystick;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.device.DeviceController;



public class GameControllerDevice extends DeviceController {

    private final Joystick device;

    public GameControllerDevice(String name, Joystick device) {
        super(name);
        this.device = device;
    }

    @Override
    public boolean initialize() {
        return true;
    }

    @Override
    public ControllerSignal poll() {
        device.poll();

        int speed = normaliseSpeedSignal(device.getY());
        boolean mainButtonDown = device.isButtonDown(Joystick.BUTTON1);
        return new ControllerSignal(speed, mainButtonDown);
    }

    @Override
    public void cleanup() {}

    @Override
    public int normaliseSpeedSignal(float deviceSpecificInputSpeedSignal) {
        if(deviceSpecificInputSpeedSignal >= -1.0 && deviceSpecificInputSpeedSignal <= 0.0)
            return new Float(deviceSpecificInputSpeedSignal * -100).intValue();
        else
            return 0;
    }
}
