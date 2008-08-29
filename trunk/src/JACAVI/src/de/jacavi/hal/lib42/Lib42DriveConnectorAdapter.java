package de.jacavi.hal.lib42;

import de.jacavi.hal.SlotCarSpeedAdjuster;



public class Lib42DriveConnectorAdapter implements Lib42DriveConnector {

    private int carID;

    private NativeLib42 lib42 = null;

    private final int maxHALSpeed = 15;

    public Lib42DriveConnectorAdapter(int carID) {
        this.carID = carID;
        lib42 = NativeLib42.getInstance();
    }

    @Override
    public int getSpeed() {
        int speed = SlotCarSpeedAdjuster.denormalizeSpeed(lib42.getSpeed(carID), maxHALSpeed);
        return speed;
    }

    @Override
    public int getSwitch() {
        return lib42.getSwitch(carID);
    }

    @Override
    public void setSpeed(int speed) {
        int normalizedSpeed = SlotCarSpeedAdjuster.normalizeSpeed(speed, maxHALSpeed);
        lib42.setSpeed(carID, normalizedSpeed);
    }

    @Override
    public int toggleSwitch() {
        return lib42.toggleSwitch(carID);
    }

    @Override
    public void fullBreak() {
        lib42.fullBreak(carID);
    }

    @Override
    public int getPitstop() {

        return lib42.getPitstop(carID);
    }

    @Override
    public void setPitstop(int pitstop) {
        lib42.setPitstop(carID, pitstop);
    }

    @Override
    public int togglePitstop() {
        return lib42.togglePitstop(carID);
    }

    @Override
    public void activateFuel() {

        lib42.activateFuel();

    }

    @Override
    public void deactivateFuel() {
        lib42.deactivateFuel();

    }

    @Override
    public void programmBreak(int value) {
        lib42.programmBreak(value);
    }

    @Override
    public void programmFuel(int value) {
        lib42.programmFuel(value);
    }

    @Override
    public void programmSpeed(int value) {
        lib42.programmSpeed(value);
    }

    @Override
    public void resetCars() {
        lib42.resetCars();
    }

    @Override
    public void activatePacecar() {
        lib42.activatePacecar();
    }

    @Override
    public void deactivatePacecar() {
        lib42.deactivatePacecar();
    }

    @Override
    public int getPcSwitch() {
        return lib42.getPcSwitch();

    }

    @Override
    public boolean isPacecarActive() {
        boolean retVal = false;
        if(lib42.isPacecarActive() == 1)
            retVal = true;
        return retVal;
    }

    @Override
    public void pacecar2box() {
        lib42.pacecar2box();
    }

    @Override
    public void setPacecarSwitch(int value) {
        lib42.setPacecarSwitch(value);
    }

    @Override
    public void setPcPitstop(int pitstop) {
        lib42.setPcPitstop(pitstop);

    }

    @Override
    public int togglePacecarSwitch() {
        return lib42.togglePacecarSwitch();
    }

    @Override
    public void programmCar() {
        lib42.programmCar(carID);
    }

}
