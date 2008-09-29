package de.jacavi.appl.controller.device.impl;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.device.DeviceController;



public class MouseKeyboardDeviceAdapter extends DeviceController {
    private MouseDevice mouseDevice = null;

    private KeyboardDevice keyboardDevice = null;

    public MouseKeyboardDeviceAdapter(String name) {
        super(name);
        mouseDevice = new MouseDevice(name);
        keyboardDevice = new KeyboardDevice(name);
    }

    @Override
    public void activate() {
        mouseDevice.activate();
        keyboardDevice.activate();
    }

    @Override
    public void deactivate() {
        mouseDevice.deactivate();
        keyboardDevice.deactivate();
    }

    @Override
    public ControllerSignal poll() {
        ControllerSignal mouse = mouseDevice.poll();
        ControllerSignal keyboard = keyboardDevice.poll();
        return new ControllerSignal(mouse.getSpeed(), mouse.isTrigger(), keyboard.isSwitchFrontLight(), keyboard
                .isSwitchBackLight());
    }

    @Override
    public int normaliseSpeedSignal(float deviceSpecificInputSpeedSignal) {
        // do nothing here this is done in mouse
        return 0;
    }
}
