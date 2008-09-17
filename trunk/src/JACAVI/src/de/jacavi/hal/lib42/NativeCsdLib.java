package de.jacavi.hal.lib42;

import de.jacavi.rcp.util.Check;



/**
 * @author Florian Roth
 *         <p>
 *         loads the lib42 sensordetection lib and maps its functionality
 */
public class NativeCsdLib {

    private static Lib42FeedbackManager feedbackManager = null;

    private static NativeCsdLib instance = null;

    static {
        System.loadLibrary("CsdLib");
    }

    /**
     * @param feedbackManager
     */
    public static void startLib42Sensordetection(Lib42FeedbackManager infeedbackManager) {
        if(instance == null) {
            Check.Require(infeedbackManager != null, "feedbackManager may not be null");
            instance = new NativeCsdLib();
            instance.initSensorDetection("callback");
            feedbackManager= infeedbackManager;
        }
    }

    private NativeCsdLib() {}

    /**
     * Initialize the native sensor detection library
     * <p>
     * TODO: Make private or protected (effects new creation of jni interface etc.)
     * 
     * @return
     */
    public native int initSensorDetection(String callbackFunctionName);

    /**
     * Release the native sensor detection library
     * <p>
     * TODO: Make this private or protected
     */
    public native void releaseSensorDetection();

    /**
     * This function is called by the csdlib (Sensordetection Library) when a sensor is detected
     * <p>
     * TODO: make this protected
     * 
     * @param carID
     * @param sensorID
     */
    public static void callback(int carID, int sensorID) {
        feedbackManager.distributeFeedback(carID, sensorID);
    }

    @Override
    protected void finalize() {
        instance.releaseSensorDetection();
    }
}
