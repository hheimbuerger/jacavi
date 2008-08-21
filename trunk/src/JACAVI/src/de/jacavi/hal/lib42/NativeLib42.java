package de.jacavi.hal.lib42;

/**
 * @author fro 
 * <p>
 * Gives the interface on the native Clib42 library and loads it.
 */
public class NativeLib42 {

    static {
        System.loadLibrary("Clib42");
    }

    public native int initLib42(int mode);

    public native void releaseLib42();

    /*
     * Methods to control a car and its track
     */
    public native void setSpeed(int carID, int speed);

    public native int getSpeed(int carID);

    public native int toggleSwitch(int carID);

    public native int getSwitch(int carID);

    public native void fullBreak(int carID);

    // Handle Pace Car
    public native void activatePacecar();

    public native void deactivatePacecar();

    public native void pacecar2box();

    public native void setPacecarSwitch(int value);

    public native int togglePacecarSwitch();

    public native void setPcPitstop(int pitstop);

    public native int isPacecarActive();

    public native int getPcSwitch();

    // HANDLE PIT STOP
    public native void setPitstop(int carId, int pitstop);

    public native int togglePitstop(int carId);

    public native int getPitstop(int carId);

    // HANDLE FUEL FOR ALL CARS
    public native void activateFuel();

    public native void deactivateFuel();

    /*
     * Methods to programm carsTODO: what is all this
     */
    public native void programmCar(int carID);

    public native void programmSpeed(int value);

    public native void programmBreak(int value);

    public native void programmFuel(int valu);

    public native void resetCars();
}
