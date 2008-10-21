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

    }

    @Override
    public boolean toggleSwitch() {
        return false;
    }

    @Override
    public InetSocketAddress getAdress() {
        return null;
    }

    @Override
    public int getLane() {
        return lane;
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
