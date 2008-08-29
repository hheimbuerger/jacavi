package de.jacavi.hal.lib42;

import de.jacavi.appl.ContextLoader;
import de.jacavi.hal.FeedbackSignal;



/**
 * Adapts the functionality of NativeCsdLib for lib42 sensor detection and gets feedback distributed by the
 * Lib42FeedbackManager
 * 
 * @author fro
 */
public class Lib42FeedbackConnectorAdapter implements Lib42FeedbackConnector {

    private int carID;

    private Lib42FeedbackManager feedbackManager = null;

    // The latest received feedback
    private FeedbackSignal latestFeedback = null;

    public Lib42FeedbackConnectorAdapter(int carID) {
        this.carID = carID;
        // get the feedbackManager and add me as listener on sensor callbacks
        feedbackManager = (Lib42FeedbackManager) ContextLoader.getBean("lib42FeedbackManager");
        feedbackManager.addFeedbackListener(this);
    }

    @Override
    public int getCarID() {
        return carID;
    }

    /**
     * This is the callback on an sensor detection called and distributed by the feedbackManager
     * 
     * @param int sensorID The id of the detected sensor
     */
    @Override
    public void sensorCallback(int sensorID) {
        latestFeedback = new FeedbackSignal(null, sensorID + "");
    }

    @Override
    protected void finalize() throws Throwable {
        feedbackManager.removeFeedbackListener(this);
    }

    @Override
    public FeedbackSignal pollFeedback() {
        return latestFeedback;
    }

}
