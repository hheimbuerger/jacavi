package de.jacavi.hal.lib42;

/**
 * @author fro
 *         <p>
 *         Gives the interface on the native Clib42 library and loads it. Singelton
 */
public class NativeLib42 {

    private static NativeLib42 instance = null;

    static {

        System.loadLibrary("Clib42");
    }

    /**
     * @return NativeLib42 the only instance because of read write device problem
     */
    public static NativeLib42 getInstance() {
        if(instance == null) {
            instance = new NativeLib42();
            instance.initLib42(0);
        }
        return instance;
    }

    @Override
    protected void finalize() {
        instance.releaseLib42();
    }

    private NativeLib42() {}

    /**
     * @param mode
     * @return
     */
    public native int initLib42(int mode);

    /**
     * Releases the native lib. ! Dont call this from outside ! TODO: Make this function protected or private. This will
     * effect an new generation of jni header etc.
     * 
     * @param mode
     * @return
     */
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
