package de.jacavi.appl.controller;

import de.jacavi.rcp.util.Check;



/**
 * Speed must be 0-100
 * 
 * @author fro
 */
public class ControllerSignal {

    private int thrust = 0;

    private boolean trigger = false;

    private boolean switchFrontLight = false;

    private boolean switchBackLight = false;

    public ControllerSignal() {
        thrust = 0;
        trigger = false;
        switchFrontLight = false;
        switchBackLight = false;
    }

    public ControllerSignal(int speed, boolean trigger) {
        this.thrust = speed;
        this.trigger = trigger;
        switchFrontLight = false;
        switchBackLight = false;

    }

    public ControllerSignal(int speed, boolean trigger, boolean frontLight, boolean backLight) {
        this.thrust = speed;
        this.trigger = trigger;
        this.switchFrontLight = frontLight;
        this.switchBackLight = backLight;
    }

    public boolean isSwitchBackLight() {
        return switchBackLight;
    }

    public void setSwitchBackLight(boolean switchBackLight) {
        this.switchBackLight = switchBackLight;
    }

    public void setSwitchFrontLight(boolean switchFrontLight) {
        this.switchFrontLight = switchFrontLight;
    }

    public void setThrust(int speed) {
        // Check preconditions speed must be between 0 and 100 included
        Check.Require(speed >= 0 && speed <= 100, "speed must be between (included) 0 and 100");
        this.thrust = speed;
    }

    public boolean isSwitchFrontLight() {
        return switchFrontLight;
    }

    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }

    public int getThrust() {
        return thrust;
    }

    public boolean isTrigger() {
        return trigger;
    }

}
