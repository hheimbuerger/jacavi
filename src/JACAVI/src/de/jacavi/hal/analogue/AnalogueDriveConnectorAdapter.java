package de.jacavi.hal.analogue;

import java.net.InetSocketAddress;



public class AnalogueDriveConnectorAdapter implements AnalogueDriveConnector {

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

    @Override
    public InetSocketAddress getAdress() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getLane() {
        // TODO Auto-generated method stub
        return 0;
    }
}