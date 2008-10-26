package de.jacavi.appl.controller.device.impl;

import com.centralnexus.input.Joystick;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.device.DeviceController;
import de.jacavi.appl.controller.device.impl.GameControllerDeviceManager.GameControllerDescriptor;



public class GameControllerDevice extends DeviceController {

    private final Joystick device;

    private final GameControllerDescriptor descriptor;

    public GameControllerDevice(String name, Joystick device, GameControllerDescriptor gameController) {
        super(name);
        this.device = device;
        this.descriptor = gameController;
    }

    @Override
    public ControllerSignal poll() {
        device.poll();

        int speed = normaliseThrust(device.getY());
        boolean mainButtonDown = device.isButtonDown(Joystick.BUTTON1);
        boolean frontLightButton = device.isButtonDown(Joystick.BUTTON2);
        boolean backLighButton = device.isButtonDown(Joystick.BUTTON3);
        boolean reset = device.isButtonDown(Joystick.BUTTON4);
        return new ControllerSignal(speed, mainButtonDown, frontLightButton, backLighButton, reset);
    }

    @Override
    public int normaliseThrust(float deviceSpecificInputSpeedSignal) {
        if(deviceSpecificInputSpeedSignal >= -1.0 && deviceSpecificInputSpeedSignal <= 0.0)
            return new Float(deviceSpecificInputSpeedSignal * -100).intValue();
        else
            return 0;
    }

    public int getNumAxes() {
        return descriptor.numAxes;
    }

    public int getNumButtons() {
        return descriptor.numButtons;
    }

    public String[] getCapabilities() {
        return descriptor.capabilities;
    }

}
