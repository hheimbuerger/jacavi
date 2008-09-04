package de.jacavi.appl.controller.device.impl;

import org.eclipse.swt.SWT;



/**
 * @author Fabian Rohn
 */
public class KeyboardLayout {

    private int accelerationButton;

    private int brakeButton;

    private int triggerButton;

    // possible would be also a handbrake or boost button, because the handling with the keyboard isn't the best in
    // comparison to wiimote or gamepad

    public static KeyboardLayout Default = new KeyboardLayout(SWT.ARROW_UP, SWT.ARROW_DOWN, SWT.CONTROL);

    /**
     * @param accelerationButton
     * @param brakeButton
     *      Please use SWT Constants like <code>SWT.ARROW_UP</code> for key specification
     */
    public KeyboardLayout(int accelerationButton, int brakeButton, int triggerButton) {
        this.accelerationButton = accelerationButton;
        this.brakeButton = brakeButton;
        this.triggerButton = triggerButton;
    }

    public int getAccelerationButton() {
        return accelerationButton;
    }

    public void setAccelerationButton(int accelerationButton) {
        this.accelerationButton = accelerationButton;
    }

    public int getBrakeButton() {
        return brakeButton;
    }

    public void setBrakeButton(int brakeButton) {
        this.brakeButton = brakeButton;
    }

    public int getTriggerButton() {
        return triggerButton;
    }

    public void setTriggerButton(int triggerButton) {
        this.triggerButton = triggerButton;
    }

}
