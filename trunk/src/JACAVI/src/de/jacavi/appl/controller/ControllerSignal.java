package de.jacavi.appl.controller;

import de.jacavi.rcp.util.Check;



/**
 * Speed must be 0-100
 * 
 * @author fro
 */
public class ControllerSignal {

    private int speed = 0;

    private boolean trigger = false;

    public ControllerSignal() {
        speed = 0;
        trigger = false;
    }

    public void setSpeed(int speed) {
        // Check preconditions speed must be between 0 and 100 included
        Check.Require(speed >= 0 && speed <= 100, "speed must be between (included) 0 and 100");
        this.speed = speed;
    }

    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }

    public ControllerSignal(int speed, boolean trigger) {
        this.speed = speed;
        this.trigger = trigger;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isTrigger() {
        return trigger;
    }

}
