package de.jacavi.hal.simulation;

import org.apache.log4j.Logger;

import de.jacavi.rcp.util.Check;



public class SimulationDriveConnectorAdapter implements SimulationDriveConnector {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(SimulationDriveConnectorAdapter.class);

    private String name = "";

    private int currentSpeed = 0;

    private boolean currentSwitchState = false;

    private boolean backLight = true;

    private boolean frontLight = true;

    private final String debugName;

    public SimulationDriveConnectorAdapter(String name) {
        Check.Require(name != null && !name.equals(""), "name may not be null or empty");
        this.name = name;
        debugName = this.getClass().toString() + " with name " + name;
        logger.debug("Created " + debugName);
    }

    @Override
    public void fullBreak() {
        logger.debug("fullbreak on " + debugName);
    }

    @Override
    public int getSpeed() {
        return currentSpeed;
    }

    @Override
    public boolean getSwitch() {
        return currentSwitchState;
    }

    @Override
    public void setSpeed(int speed) {
        // logger.debug("Setting speed on " + debugName + " to " + speed);
        currentSpeed = speed;

    }

    @Override
    public boolean toggleSwitch() {
        currentSwitchState = !currentSwitchState;

        return currentSwitchState;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isBackLightOn() {
        return backLight;
    }

    @Override
    public boolean isFrontLightOn() {
        return frontLight;
    }

    @Override
    public void switchBackLight() {
        logger.debug("Switching backlight on " + debugName);
        if(backLight)
            backLight = false;
        else
            backLight = true;
    }

    @Override
    public void switchFrontLight() {
        logger.debug("Switching frontlight on " + debugName);
        if(frontLight)
            frontLight = false;
        else
            frontLight = true;
    }

}
