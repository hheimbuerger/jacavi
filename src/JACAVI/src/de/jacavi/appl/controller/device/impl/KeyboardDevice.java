package de.jacavi.appl.controller.device.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.device.DeviceController;



public class KeyboardDevice extends DeviceController implements Listener {

    private ControllerSignal currentControllerSignal;

    private final KeyboardLayout keyboardLayout;

    /**
     * A constant which describes how much speed is added/substraced by button-hit
     */
    private final int speedStepConstant = 5;

    public KeyboardDevice(String name) {
        this(name, KeyboardLayout.Default);
    }

    public KeyboardDevice(String name, KeyboardLayout keyboardLayout) {
        super(name);
        currentControllerSignal = new ControllerSignal();

        this.keyboardLayout = keyboardLayout;
    }

    @Override
    public void activate() {
        Display.getCurrent().addFilter(SWT.KeyDown, this);
        Display.getCurrent().addFilter(SWT.KeyUp, this);
    }

    @Override
    public void deactivate() {
        Display.getCurrent().removeFilter(SWT.KeyDown, this);
        Display.getCurrent().removeFilter(SWT.KeyUp, this);
    }

    @Override
    public ControllerSignal poll() {
        ControllerSignal retVal = currentControllerSignal;
        currentControllerSignal = new ControllerSignal(currentControllerSignal.getThrust(), currentControllerSignal
                .isTrigger());
        return retVal;
    }

    @Override
    public int normaliseSpeedSignal(float deviceSpecificInputSpeedSignal) {
        return 0;
    }

    @Override
    public void handleEvent(Event event) {
        int speed = currentControllerSignal.getThrust();

        // accelerate (takes KeyDown only)
        if(event.type == SWT.KeyDown && event.keyCode == keyboardLayout.getAccelerationButton()) {
            handleAcceleration(speed);
        }

        // brake (takes KeyDown only)
        if(event.type == SWT.KeyDown && event.keyCode == keyboardLayout.getBrakeButton()) {
            handleBrake(speed);
        }

        // frontLight (takes KeyDown only)
        if(event.type == SWT.KeyDown && event.keyCode == keyboardLayout.getFrontLightButton()) {
            handleFrontLight(event.type);
        }

        // backLight (takes KeyDown only)
        if(event.type == SWT.KeyDown && event.keyCode == keyboardLayout.getBackLightButton()) {
            handleBackLight(event.type);
        }

        // trigger (takes both KeyDown and KeyUp -- because it can be held)
        if(event.keyCode == keyboardLayout.getTriggerButton()) {
            handleTrigger(event.type);
        }
    }

    private void handleTrigger(int eventType) {
        currentControllerSignal.setTrigger(eventType == SWT.KeyDown);
    }

    private void handleBrake(int speed) {
        currentControllerSignal.setThrust(Math.max(speed - speedStepConstant, 0));
    }

    private void handleAcceleration(int speed) {
        currentControllerSignal.setThrust(Math.min(speed + speedStepConstant, 100));
    }

    private void handleFrontLight(int eventType) {
        currentControllerSignal.setSwitchFrontLight(eventType == SWT.KeyDown);
    }

    private void handleBackLight(int eventType) {
        currentControllerSignal.setSwitchBackLight(eventType == SWT.KeyDown);
    }
}
