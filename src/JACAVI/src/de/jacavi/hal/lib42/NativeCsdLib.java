package de.jacavi.hal.lib42;

import de.jacavi.rcp.util.Check;



/**
 * @author Florian Roth
 *         <p>
 *         loads the lib42 sensordetection lib and maps its functionality
 */
public class NativeCsdLib {

    private Lib42FeedbackConnector feedbackConnector = null;

    private static NativeCsdLib instance = null;

    private static int usedCount = 0;

    static {
        System.loadLibrary("CsdLib");
    }

    public static NativeCsdLib subscribe(Lib42FeedbackConnector feedbackConnector) {
        if(instance == null) {
            instance = new NativeCsdLib(feedbackConnector);
            instance.initSensorDetection();
        }
        usedCount++;
        return instance;
    }

    public void unsubscribe() {
        usedCount--;
        if(usedCount == 0)
            instance.releaseSensorDetection();
    }

    private NativeCsdLib(Lib42FeedbackConnector feedbackConnector) {
        Check.Require(feedbackConnector != null, "feedbackConnector may not be null");
        this.feedbackConnector = feedbackConnector;
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
    public void callback(int carID, int sensorID) {
        feedbackConnector.sensorCallback(carID, sensorID);
    }
}
