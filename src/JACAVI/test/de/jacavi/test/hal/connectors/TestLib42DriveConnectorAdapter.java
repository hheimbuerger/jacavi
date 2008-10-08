package de.jacavi.test.hal.connectors;

import de.jacavi.hal.lib42.Lib42DriveConnector;



public class TestLib42DriveConnectorAdapter implements Lib42DriveConnector {

    private int carID;

    public TestLib42DriveConnectorAdapter(int carID) {
        this.carID = carID;
    }

    @Override
    public void activateFuel() {
    // TODO Auto-generated method stub

    }

    @Override
    public void activatePacecar() {
    // TODO Auto-generated method stub

    }

    @Override
    public void deactivateFuel() {
    // TODO Auto-generated method stub

    }

    @Override
    public void deactivatePacecar() {
    // TODO Auto-generated method stub

    }

    @Override
    public int getPcSwitch() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getPitstop() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isPacecarActive() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void pacecar2box() {
    // TODO Auto-generated method stub

    }

    @Override
    public void programmBreak(int value) {
    // TODO Auto-generated method stub

    }

    @Override
    public void programmCar() {
    // TODO Auto-generated method stub

    }

    @Override
    public void programmFuel(int value) {
    // TODO Auto-generated method stub

    }

    @Override
    public void programmSpeed(int value) {
    // TODO Auto-generated method stub

    }

    @Override
    public void resetCars() {
    // TODO Auto-generated method stub

    }

    @Override
    public void setPacecarSwitch(int value) {
    // TODO Auto-generated method stub

    }

    @Override
    public void setPcPitstop(int pitstop) {
    // TODO Auto-generated method stub

    }

    @Override
    public void setPitstop(int pitstop) {
    // TODO Auto-generated method stub

    }

    @Override
    public int togglePacecarSwitch() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int togglePitstop() {
        // TODO Auto-generated method stub
        return 0;
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
        System.out.println("Speed set to:" + speed);
    }

    @Override
    public boolean toggleSwitch() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getCarID() {
        return carID;
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
