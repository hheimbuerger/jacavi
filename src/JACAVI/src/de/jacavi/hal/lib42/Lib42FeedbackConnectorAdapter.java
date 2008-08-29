package de.jacavi.hal.lib42;

import java.util.PriorityQueue;
import java.util.Queue;

import de.jacavi.hal.FeedbackSignal;



public class Lib42FeedbackConnectorAdapter implements Lib42FeedbackConnector {

    private final Queue<FeedbackSignal> signalQueue = new PriorityQueue<FeedbackSignal>();

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

    }

    @Override
    protected void finalize() throws Throwable {
        nativeCsdLib.unsubscribe();
    }

    @Override
    public FeedbackSignal pollFeedback() {

        return null;
    }
}
