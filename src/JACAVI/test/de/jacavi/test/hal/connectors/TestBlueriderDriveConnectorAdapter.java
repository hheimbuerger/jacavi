package de.jacavi.test.hal.connectors;

import de.jacavi.hal.bluerider.BlueriderConnector;



public class TestBlueriderDriveConnectorAdapter implements BlueriderConnector {

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

    @Override
    public boolean connectBlueRider() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isConnected() {
        // TODO Auto-generated method stub
        return false;
    }
}
