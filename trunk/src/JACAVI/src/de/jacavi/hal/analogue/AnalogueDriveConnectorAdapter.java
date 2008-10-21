package de.jacavi.hal.analogue;

import java.net.InetSocketAddress;



/**
 * TODO: [ticket #10] Implement this
 */
public class AnalogueDriveConnectorAdapter implements AnalogueDriveConnector {

    @Override
    public void fullBreak() {
        throw new RuntimeException("TODO: [ticket #10] implement me.");
    }

    @Override
    public int getThrust() {
        throw new RuntimeException("TODO: [ticket #10] implement me.");
        // return 0;
    }

    @Override
    public boolean getSwitch() {
        throw new RuntimeException("TODO: [ticket #10] implement me.");
        // return false;
    }

    @Override
    public void setThrust(int speed) {
        throw new RuntimeException("TODO: [ticket #10] implement me.");

    }

    @Override
    public boolean toggleSwitch() {
        throw new RuntimeException("TODO: [ticket #10] implement me.");
    }

    @Override
    public InetSocketAddress getAdress() {
        throw new RuntimeException("TODO: [ticket #10] implement me.");
    }

    @Override
    public int getLane() {
        throw new RuntimeException("TODO: [ticket #10] implement me.");
    }

    @Override
    public void switchBackLight() {
        throw new RuntimeException("TODO: [ticket #10] implement me.");
    }

    @Override
    public void switchFrontLight() {
        throw new RuntimeException("TODO: [ticket #10] implement me.");
    }

    @Override
    public boolean isBackLightOn() {
        throw new RuntimeException("TODO: [ticket #10] implement me.");
    }

    @Override
    public boolean isFrontLightOn() {
        throw new RuntimeException("TODO: [ticket #10] implement me.");
    }

}
