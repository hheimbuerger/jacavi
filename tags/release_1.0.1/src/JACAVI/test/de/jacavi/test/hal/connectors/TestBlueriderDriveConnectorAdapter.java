package de.jacavi.test.hal.connectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jacavi.hal.SlotCarThrustAdjuster;
import de.jacavi.hal.bluerider.BlueriderDriveConnector;
import de.jacavi.hal.bluerider.BlueriderDriveConnectorAdapter;



public class TestBlueriderDriveConnectorAdapter implements BlueriderDriveConnector {

    private static Log log = LogFactory.getLog(BlueriderDriveConnectorAdapter.class);

    public TestBlueriderDriveConnectorAdapter(String comPort) {

    }

    @Override
    public void fullBreak() {

    }

    @Override
    public int getThrust() {
        return 0;
    }

    @Override
    public boolean getSwitch() {
        return false;
    }

    @Override
    public void setThrust(int speed) {
        log.debug("Bluerider in speed: " + speed);
        int adjustedSpeed = SlotCarThrustAdjuster.normalizeThrust(speed, 255);
        log.debug("Bluerider adjusted speed: " + adjustedSpeed);
    }

    @Override
    public boolean toggleSwitch() {
        return false;
    }

    @Override
    public boolean connectBlueRider() {
        return true;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isBackLightOn() {
        return false;
    }

    @Override
    public boolean isFrontLightOn() {
        return false;
    }

    @Override
    public void switchBackLight() {

    }

    @Override
    public void switchFrontLight() {

    }
}
