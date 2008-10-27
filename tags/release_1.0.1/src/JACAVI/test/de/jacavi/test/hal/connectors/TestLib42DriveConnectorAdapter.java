package de.jacavi.test.hal.connectors;

import de.jacavi.hal.lib42.Lib42DriveConnector;



public class TestLib42DriveConnectorAdapter implements Lib42DriveConnector {

    private int carID;

    public TestLib42DriveConnectorAdapter(int carID) {
        this.carID = carID;
    }

    @Override
    public void activateFuel() {

    }

    @Override
    public void activatePacecar() {

    }

    @Override
    public void deactivateFuel() {

    }

    @Override
    public void deactivatePacecar() {

    }

    @Override
    public int getPcSwitch() {
        return 0;
    }

    @Override
    public int getPitstop() {
        return 0;
    }

    @Override
    public boolean isPacecarActive() {
        return false;
    }

    @Override
    public void pacecar2box() {

    }

    @Override
    public void programmBreak(int value) {

    }

    @Override
    public void programmCar() {

    }

    @Override
    public void programmFuel(int value) {

    }

    @Override
    public void programmSpeed(int value) {

    }

    @Override
    public void resetCars() {

    }

    @Override
    public void setPacecarSwitch(int value) {

    }

    @Override
    public void setPcPitstop(int pitstop) {

    }

    @Override
    public void setPitstop(int pitstop) {

    }

    @Override
    public int togglePacecarSwitch() {
        return 0;
    }

    @Override
    public int togglePitstop() {
        return 0;
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
        System.out.println("Speed set to:" + speed);
    }

    @Override
    public boolean toggleSwitch() {
        return false;
    }

    @Override
    public int getCarID() {
        return carID;
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
