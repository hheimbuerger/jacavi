package de.jacavi.test.hal.connectors;

import de.jacavi.hal.SlotCarSystemDriveConnector;



public class TestBlueriderDriveConnectorAdapter implements SlotCarSystemDriveConnector {

    public TestBlueriderDriveConnectorAdapter(String comPort) {
    // TODO Auto-generated constructor stub
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
    /**
     * @author Florian Roth
     */
}
