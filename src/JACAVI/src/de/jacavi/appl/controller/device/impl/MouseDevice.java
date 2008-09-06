package de.jacavi.appl.controller.device.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.device.DeviceController;



public class MouseDevice extends DeviceController implements Listener {

    private final ControllerSignal currentControllerSignal;

    private boolean isLeftMouseButtonPressed = false;

    private int lastYCoords = 0;

    private int lastSpeed = 0;

    public MouseDevice(String name) {
        super(name);

        currentControllerSignal = new ControllerSignal();
    }

    @Override
    public void unhookListener() {
        Display.getCurrent().removeFilter(SWT.MouseDown, this);
        Display.getCurrent().removeFilter(SWT.MouseMove, this);
        Display.getCurrent().removeFilter(SWT.MouseUp, this);
    }

    @Override
    public void hookListener() {
        isLeftMouseButtonPressed = false;
        lastYCoords = 0;
        lastSpeed = 0;
        Display.getCurrent().addFilter(SWT.MouseDown, this);
        Display.getCurrent().addFilter(SWT.MouseMove, this);
        Display.getCurrent().addFilter(SWT.MouseUp, this);
    }

    @Override
    public boolean initialize() {
        return false;
    }

    @Override
    public ControllerSignal poll() {
        return currentControllerSignal;
    }

    @Override
    public void cleanup() {}

    @Override
    public int normaliseSpeedSignal(float deviceSpecificInputSpeedSignal) {
        return 0;
    }

    @Override
    public void handleEvent(Event event) {
        switch(event.button) {
            // left mouse button
            case 1:
                if(!isLeftMouseButtonPressed) {
                    isLeftMouseButtonPressed = true;
                    lastYCoords = event.y;
                } else if(isLeftMouseButtonPressed) {
                    isLeftMouseButtonPressed = false;
                    lastSpeed = currentControllerSignal.getSpeed();
                }
                break;

            // middle mouse button
            case 2:
                break;

            // right mouse button
            case 3:
                handleTrigger();
                break;

            default:
                if(isLeftMouseButtonPressed) {
                    if(lastYCoords > event.y)
                        handleSpeed(lastSpeed + Math.abs(event.y - lastYCoords) - 1);
                    else {
                        handleSpeed(lastSpeed - Math.abs(event.y - lastYCoords) - 1);
                    }
                }
                break;
        }
    }

    private void handleTrigger() {
        boolean trigger = currentControllerSignal.isTrigger();
        currentControllerSignal.setTrigger(!trigger);
    }

    private void handleSpeed(int speed) {
        currentControllerSignal.setSpeed(Math.min(Math.max(speed, 0), 100));
    }

}
