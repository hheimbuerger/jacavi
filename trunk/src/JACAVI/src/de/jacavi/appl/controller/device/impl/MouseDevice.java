package de.jacavi.appl.controller.device.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.controller.device.DeviceController;
import de.jacavi.rcp.widgets.TrackWidget;



public class MouseDevice extends DeviceController implements Listener {

    private final ControllerSignal currentControllerSignal;

    public MouseDevice(String name) {
        super(name);

        currentControllerSignal = new ControllerSignal();

        // TODO: Listener has to be added in initialize method
        Display.getCurrent().addFilter(SWT.MouseDown, this);
        Display.getCurrent().addFilter(SWT.MouseMove, this);
        Display.getCurrent().addFilter(SWT.MouseUp, this);
    }

    @Override
    public boolean initialize() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ControllerSignal poll() {
        return currentControllerSignal;
    }

    @Override
    public void cleanup() {
        Display.getCurrent().removeFilter(SWT.MouseDown, this);
    }

    @Override
    public int normaliseSpeedSignal(float deviceSpecificInputSpeedSignal) {
        return 0;
    }

    private boolean pressed1 = false;

    private int lastY = 0;

    private int lastSpeed = 0;

    @Override
    public void handleEvent(Event event) {
        // TODO HACK:under some untraceable circumstances, a focus on a combo in the PlayerSettingsDialog fires an
        // event and results a
        // strange behavior --> EventListener(or Filter) has to be added when race starts

        if(event.widget instanceof TrackWidget) {

            switch(event.button) {
                // left mouse
                case 1:
                    if(!pressed1) {
                        pressed1 = true;
                        lastY = event.y;
                        System.out.println(lastY);
                        System.out.println(event.widget);
                    } else if(pressed1) {
                        pressed1 = false;
                        lastSpeed = currentControllerSignal.getSpeed();
                    }

                    break;

                // middle mouse
                case 2:

                    break;

                // right mouse
                case 3:
                    handleTrigger();
                    break;

                default:
                    if(pressed1) {
                        if(lastY > event.y)
                            handleSpeed(lastSpeed + Math.abs(event.y - lastY) - 1);
                        else {
                            handleSpeed(lastSpeed - Math.abs(event.y - lastY) - 1);
                        }
                    }
                    break;
            }
        }
    }

    private void handleTrigger() {
        boolean trigger = currentControllerSignal.isTrigger();
        currentControllerSignal.setTrigger(!trigger);
    }

    private void handleSpeed(int speed) {
        if(speed > 100)
            speed = 100;
        if(speed < 0)
            speed = 0;
        System.out.println("speed:" + speed);
        currentControllerSignal.setSpeed(speed);
    }
}
