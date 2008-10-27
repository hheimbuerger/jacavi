package de.jacavi.hal.lib42;

import de.jacavi.hal.SlotCarThrustAdjuster;
import de.jacavi.rcp.util.Check;



public class Lib42DriveConnectorAdapter implements Lib42DriveConnector {

    private final int carID;

    private NativeLib42 lib42 = null;

    private final int maxHALSpeed = 15;

    public Lib42DriveConnectorAdapter(int carID) throws Exception {
        Check.Require(carID > 0, "carID must be >0");
        this.carID = carID;
        lib42 = NativeLib42.getInstance();
        lib42.resetCars();
        lib42.programmCar(carID);
    }

    @Override
    public int getThrust() {
        int halSpeed = lib42.getSpeed(carID);
        Check.Ensure(halSpeed >= 0 && halSpeed <= maxHALSpeed, "Speed is in the wrong range");
        int speed = SlotCarThrustAdjuster.denormalizeThrust(halSpeed, maxHALSpeed);
        return speed;
    }

    @Override
    public boolean getSwitch() {
        return lib42.getSwitch(carID) == 1;
    }

    @Override
    public void setThrust(int speed) {
        int normalizedSpeed = SlotCarThrustAdjuster.normalizeThrust(speed, maxHALSpeed);
        Check.Require(normalizedSpeed >= 0 && normalizedSpeed <= maxHALSpeed, "speed is in the wrong range");
        lib42.setSpeed(carID, normalizedSpeed);
    }

    @Override
    public boolean toggleSwitch() {
        return lib42.toggleSwitch(carID) == 1;
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

    @Override
    public int getCarID() {
        return carID;
    }

    @Override
    public void switchBackLight() {
    // currently not available for lib42

    }

    @Override
    public void switchFrontLight() {
    // currently not available for lib42
    }

    @Override
    public boolean isBackLightOn() {
        // currently not available for lib42 return false;
        return false;
    }

    @Override
    public boolean isFrontLightOn() {
        // currently not available for lib42
        return false;
    }

}
