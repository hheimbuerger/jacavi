package de.jacavi.hal.lib42;

/**
 * @author Florian Roth
 * <p>
 *	loads the lib42 sensordetection lib and maps its functionality
 */
public class NativeCsdLib {
    static {
        System.loadLibrary("CsdLib");
    }

    /* initialization */
    public native int initSensorDetection();

    public native void releaseSensorDetection();

    /**
     * This function is called by the csdlib (Sensordetection Library) when a sensor is detected
     * 
     * @param carID
     * @param sensorID
     */
    public static void callback(int carID, int sensorID) {
        // TODO:
        System.out.println("callback: carID->" + carID + " sensorID->" + sensorID);
    }
}
