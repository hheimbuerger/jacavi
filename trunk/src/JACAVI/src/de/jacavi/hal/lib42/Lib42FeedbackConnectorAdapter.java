package de.jacavi.hal.lib42;

public class Lib42FeedbackConnectorAdapter implements Lib42FeedbackConnector {

    private NativeCsdLib nativeCsdLib = null;

    public Lib42FeedbackConnectorAdapter() {
        // create a new instance and give this for callback
        nativeCsdLib = new NativeCsdLib(this);
        // init sensor detection
        nativeCsdLib.initSensorDetection();
    }

    /**
     * This is callback function from native lib42 use this in TDA for lib42 sensor detection
     * 
     * @param int carID The id of the car
     * @param int sensorID Th id of the detected sensor
     */
    @Override
    public void sensorCallback(int carID, int sensorID) {
    // TODO:fro: TDA should do something with this method
    }

    @Override
    protected void finalize() throws Throwable {
        // FIXME: problem is that the native library doesnt throw an exception
        nativeCsdLib.releaseSensorDetection();
    }
}
