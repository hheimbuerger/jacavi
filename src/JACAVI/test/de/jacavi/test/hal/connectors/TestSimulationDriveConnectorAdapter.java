package de.jacavi.test.hal.connectors;

import de.jacavi.hal.simulation.SimulationDriveConnector;



public class TestSimulationDriveConnectorAdapter implements SimulationDriveConnector {

    private int speed = 0;

    private boolean frontLight = false;

    private boolean backLight = false;

    public TestSimulationDriveConnectorAdapter(String name) {}

    @Override
    public void fullBreak() {
        speed = 0;
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public int getSwitch() {
        return 0;
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
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public void switchBackLight() {
        if(backLight)
            backLight = false;
        else
            backLight = true;
    }

    @Override
    public void switchFrontLight() {
        if(frontLight)
            frontLight = false;
        else
            frontLight = true;
    }

    @Override
    public int toggleSwitch() {
        return 0;
    }

}
