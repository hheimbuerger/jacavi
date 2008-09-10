package de.jacavi.hal.simulation;

import de.jacavi.hal.SlotCarSystemDriveConnector;



public class SimulationDriveConnectorAdapter implements SlotCarSystemDriveConnector {

    private String name = "";

    public SimulationDriveConnectorAdapter(String name) {
        this.name = name;
    }

    @Override
    public void fullBreak() {
    // TODO Auto-generated method stub

    }

    @Override
    public int getSpeed() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSwitch() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setSpeed(int speed) {
    // TODO Auto-generated method stub

    }

    @Override
    public int toggleSwitch() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isBackLightOn() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isFrontLightOn() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void switchBackLight() {
    // TODO Auto-generated method stub

    }

    @Override
    public void switchFrontLight() {
    // TODO Auto-generated method stub

    }

}
