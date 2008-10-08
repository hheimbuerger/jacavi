package de.jacavi.test.hal.connectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jacavi.hal.SlotCarSpeedAdjuster;
import de.jacavi.hal.bluerider.BlueriderDriveConnector;
import de.jacavi.hal.bluerider.BlueriderDriveConnectorAdapter;



public class TestBlueriderDriveConnectorAdapter implements BlueriderDriveConnector {

    private static Log log = LogFactory.getLog(BlueriderDriveConnectorAdapter.class);

    public TestBlueriderDriveConnectorAdapter(String comPort) {
    // TODO Auto-generated constructor stub
    }

    @Override
    public void fullBreak() {
    // TODO Auto-generated method stub

    }

    @Override
    public int getThrust() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getSwitch() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setThrust(int speed) {
        log.debug("Bluerider in speed: " + speed);
        int adjustedSpeed = SlotCarSpeedAdjuster.normalizeSpeed(speed, 255);
        log.debug("Bluerider adjusted speed: " + adjustedSpeed);
    }

    @Override
    public boolean toggleSwitch() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean connectBlueRider() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isConnected() {
        // TODO Auto-generated method stub
        return true;
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
