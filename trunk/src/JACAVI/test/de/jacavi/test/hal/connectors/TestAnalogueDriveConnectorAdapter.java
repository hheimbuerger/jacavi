package de.jacavi.test.hal.connectors;

import java.net.InetSocketAddress;

import de.jacavi.hal.analogue.AnalogueDriveConnector;



public class TestAnalogueDriveConnectorAdapter implements AnalogueDriveConnector {

    private int lane;

    public TestAnalogueDriveConnectorAdapter(InetSocketAddress analogueDeviceAdress, int lane) {
        this.lane = lane;
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
    // TODO Auto-generated method stub

    }

    @Override
    public boolean toggleSwitch() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public InetSocketAddress getAdress() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getLane() {
        return lane;
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
