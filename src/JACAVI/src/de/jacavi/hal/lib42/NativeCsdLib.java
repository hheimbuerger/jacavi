package de.jacavi.hal.lib42;

import de.jacavi.rcp.util.Check;



/**
 * @author Florian Roth <p> loads the lib42 sensordetection lib and maps its functionality
 */
public class NativeCsdLib {

    private static Lib42FeedbackManager feedbackManager = null;

    private static NativeCsdLib instance = null;

    /**
     * @param feedbackManager
     */
    public static void startLib42Sensordetection(Lib42FeedbackManager infeedbackManager) throws Exception {
        if(instance == null) {
            Check.Require(infeedbackManager != null, "feedbackManager may not be null");
            instance = new NativeCsdLib();
            instance.initSensorDetection("callback");
            feedbackManager = infeedbackManager;
        }
    }

    private NativeCsdLib() throws Exception {

        try {
            System.loadLibrary("CsdLib");
        } catch(UnsatisfiedLinkError ers) {
            throw new Exception("error on loading Clib42", ers);
        } catch(NoClassDefFoundError er) {
            throw new Exception("error on loading Clib42", er);
        } catch(Exception ex) {
            throw new Exception("error on loading Clib42", ex);
        }
    }

    /**
     * Initialize the native sensor detection library
     * 
     * @return
     */
    public native int initSensorDetection(String callbackFunctionName);

    /**
     * Release the native sensor detection library
     */
    public native void releaseSensorDetection();

    /**
     * This function is called by the csdlib (Sensordetection Library) when a sensor is detected
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
