package de.jacavi.appl.controller.device.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.device.DeviceController;



public class MouseDevice extends DeviceController implements Listener {

    private ControllerSignal currentControllerSignal;

    private boolean isLeftMouseButtonPressed = false;

    private int lastYCoords = 0;

    private int lastSpeed = 0;

    public MouseDevice(String name) {
        super(name);
    }

    @Override
    public void activate() {
        isLeftMouseButtonPressed = false;
        lastYCoords = 0;
        lastSpeed = 0;
        currentControllerSignal = new ControllerSignal();
        Display.getCurrent().addFilter(SWT.MouseDown, this);
        Display.getCurrent().addFilter(SWT.MouseMove, this);
        Display.getCurrent().addFilter(SWT.MouseUp, this);
    }

    @Override
    public void deactivate() {
        Display.getCurrent().removeFilter(SWT.MouseDown, this);
        Display.getCurrent().removeFilter(SWT.MouseMove, this);
        Display.getCurrent().removeFilter(SWT.MouseUp, this);
    }

    @Override
    public ControllerSignal poll() {
        ControllerSignal retVal = currentControllerSignal;
        currentControllerSignal = new ControllerSignal(currentControllerSignal.getSpeed(), false);
        return retVal;
    }

    @Override
    public int normaliseSpeedSignal(float deviceSpecificInputSpeedSignal) {
        return 0;
    }

    @Override
    public void handleEvent(Event event) {
        if(event.type == SWT.MouseDown || event.type == SWT.MouseUp) {
            switch(event.button) {
                // left mouse button
                case 1:
                    if(event.type == SWT.MouseDown) {
                        isLeftMouseButtonPressed = true;
                        lastYCoords = event.y;
                    } else if(event.type == SWT.MouseUp) {
                        isLeftMouseButtonPressed = false;
                        lastSpeed = currentControllerSignal.getSpeed();
                    }
                    break;

                // middle mouse button
                case 2:
                    break;

                // right mouse button
                case 3:
                    handleTrigger(event.type);
                    break;
            }
        } else if(event.type == SWT.MouseMove) {
            if(isLeftMouseButtonPressed) {
                if(lastYCoords > event.y)
                    handleSpeed(lastSpeed + Math.abs(event.y - lastYCoords) - 1);
                else {
                    handleSpeed(lastSpeed - Math.abs(event.y - lastYCoords) - 1);
                }
            }
        }
    }

    private void handleTrigger(int eventType) {
        assert eventType == SWT.MouseDown || eventType == SWT.MouseUp: "Invalid event type received in MouseDevice.handleTrigger()";
        currentControllerSignal.setTrigger(eventType == SWT.MouseDown);
    }

    private void handleSpeed(int speed) {
        currentControllerSignal.setSpeed(Math.min(Math.max(speed, 0), 100));
    }

}
