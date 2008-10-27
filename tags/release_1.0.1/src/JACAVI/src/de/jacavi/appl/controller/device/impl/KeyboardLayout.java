package de.jacavi.appl.controller.device.impl;

import org.eclipse.swt.SWT;



/**
 * @author Fabian Rohn
 */
public class KeyboardLayout {

    private int accelerationButton;

    private int brakeButton;

    private int triggerButton;

    private int frontLightButton;

    private int backLightButton;

    private int resetButton;

    // possible would be also a handbrake or boost button, because the handling with the keyboard isn't the best in
    // comparison to wiimote or gamepad
    public static KeyboardLayout Default = new KeyboardLayout(SWT.ARROW_UP, SWT.ARROW_DOWN, SWT.CONTROL, 'y', 'x', 'r');

    /**
     * @param accelerationButton
     * @param brakeButton
     *            Please use SWT Constants like <code>SWT.ARROW_UP</code> for key specification
     */
    public KeyboardLayout(int accelerationButton, int brakeButton, int triggerButton, int frontLightButton,
            int backLightButton, int resetButton) {
        this.accelerationButton = accelerationButton;
        this.brakeButton = brakeButton;
        this.triggerButton = triggerButton;
        this.frontLightButton = frontLightButton;
        this.backLightButton = backLightButton;
        this.resetButton = resetButton;
    }

    public int getResetButton() {
        return resetButton;
    }

    public void setResetButton(int resetButton) {
        this.resetButton = resetButton;
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

    public int getFrontLightButton() {
        return frontLightButton;
    }

    public void setFrontLightButton(int frontLightButton) {
        this.frontLightButton = frontLightButton;
    }

    public int getBackLightButton() {
        return backLightButton;
    }

    public void setBackLightButton(int backLightButton) {
        this.backLightButton = backLightButton;
    }

}
