package de.jacavi.hal.lib42;

public class Lib42FeedbackConnectorAdapter implements Lib42FeedbackConnector {

    private NativeCsdLib nativeCsdLib = null;

    public Lib42FeedbackConnectorAdapter() {
        // subscribe to native lib
        nativeCsdLib = NativeCsdLib.subscribe(this);
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
        nativeCsdLib.unsubscribe();
    }
}
