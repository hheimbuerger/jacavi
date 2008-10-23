package de.jacavi.appl.controller.device.impl;

import java.util.List;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.device.DeviceController;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.Track;



public class MouseKeyboardDeviceAdapter extends DeviceController {
    private MouseDevice mouseDevice = null;

    private KeyboardDevice keyboardDevice = null;

    public MouseKeyboardDeviceAdapter(String name) {
        super(name);
        mouseDevice = new MouseDevice(name);
        keyboardDevice = new KeyboardDevice(name);
    }

    @Override
    public void activate(Track track, List<Player> players) {
        mouseDevice.activate(track, players);
        keyboardDevice.activate(track, players);
    }

    @Override
    public void deactivate() {
        mouseDevice.deactivate();
        keyboardDevice.deactivate();
    }

    @Override
    public void reset() {
        mouseDevice.reset();
        keyboardDevice.reset();
    }

    @Override
    public ControllerSignal poll() {
        ControllerSignal mouse = mouseDevice.poll();
        ControllerSignal keyboard = keyboardDevice.poll();
        return new ControllerSignal(mouse.getThrust(), mouse.isTrigger(), keyboard.isSwitchFrontLight(), keyboard
                .isSwitchBackLight(), mouse.isReset());
    }

    @Override
    public int normaliseThrust(float deviceSpecificInputSpeedSignal) {
        // do nothing here this is done in mouse
        return 0;
    }
}
