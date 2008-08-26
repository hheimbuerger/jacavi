package de.jacavi.hal.lib42;

import de.jacavi.rcp.util.Check;



/**
 * @author Florian Roth
 *         <p>
 *         loads the lib42 sensordetection lib and maps its functionality
 */
public class NativeCsdLib {

    private Lib42FeedbackConnector feedbackConnector = null;

    static {
        System.loadLibrary("CsdLib");
    }

    public NativeCsdLib(Lib42FeedbackConnector feedbackConnector) {
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
