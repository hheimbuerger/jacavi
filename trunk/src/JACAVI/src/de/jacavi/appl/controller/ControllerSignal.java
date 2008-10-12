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

    private boolean reset = false;

    public ControllerSignal() {
        thrust = 0;
        trigger = false;
        switchFrontLight = false;
        switchBackLight = false;
        reset = false;
    }

    public ControllerSignal(int speed, boolean trigger) {
        this.thrust = speed;
        this.trigger = trigger;
        switchFrontLight = false;
        switchBackLight = false;
        this.reset = false;
    }

    public ControllerSignal(int speed, boolean trigger, boolean frontLight, boolean backLight) {
        this.thrust = speed;
        this.trigger = trigger;
        this.switchFrontLight = frontLight;
        this.switchBackLight = backLight;
        this.reset = false;
    }

    public ControllerSignal(int speed, boolean trigger, boolean frontLight, boolean backLight, boolean reset) {
        this.thrust = speed;
        this.trigger = trigger;
        this.switchFrontLight = frontLight;
        this.switchBackLight = backLight;
        this.reset = reset;
    }

    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
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

    public void setThrust(int thrust) {
        // Check preconditions thrust must be between 0 and 100 included
        Check.Require(thrust >= 0 && thrust <= 100, "thrust must be between (included) 0 and 100");
        this.thrust = thrust;
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
