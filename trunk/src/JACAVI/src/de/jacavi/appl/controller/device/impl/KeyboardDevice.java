package de.jacavi.appl.controller.device.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.device.DeviceController;



public class KeyboardDevice extends DeviceController implements Listener {

    private final ControllerSignal currentControllerSignal;

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
    public void preRace() {
        currentControllerSignal.setSpeed(0);
        currentControllerSignal.setTrigger(false);
        Display.getCurrent().addFilter(SWT.KeyDown, this);
        Display.getCurrent().addFilter(SWT.KeyUp, this);
    }

    @Override
    public void postRace() {
        Display.getCurrent().removeFilter(SWT.KeyDown, this);
        Display.getCurrent().removeFilter(SWT.KeyUp, this);
    }

    @Override
    public void cleanup() {}

    @Override
    public boolean initialize() {
        // Display.getCurrent().addFilter(SWT.KeyDown, this);
        return true;
    }

    @Override
    public ControllerSignal poll() {
        return currentControllerSignal;
    }

    @Override
    public int normaliseSpeedSignal(float deviceSpecificInputSpeedSignal) {
        return 0;
    }

    @Override
    public void handleEvent(Event event) {
        int speed = currentControllerSignal.getSpeed();
        boolean trigger = currentControllerSignal.isTrigger();

        // accelerate
        if(event.keyCode == keyboardLayout.getAccelerationButton()) {
            handleAcceleration(speed);
        }

        // brake
        if(event.keyCode == keyboardLayout.getBrakeButton()) {
            handleBrake(speed);
        }

        // trigger
        if(event.keyCode == keyboardLayout.getTriggerButton()) {
            handleTrigger(trigger);
        }
    }

    private void handleTrigger(boolean trigger) {
        currentControllerSignal.setTrigger(!trigger);
    }

    private void handleBrake(int speed) {
        currentControllerSignal.setSpeed(Math.max(speed - speedStepConstant, 0));
    }

    private void handleAcceleration(int speed) {
        currentControllerSignal.setSpeed(Math.min(speed + speedStepConstant, 100));
    }

}