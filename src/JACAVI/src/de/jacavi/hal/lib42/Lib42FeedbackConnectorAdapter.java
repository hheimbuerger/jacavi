package de.jacavi.hal.lib42;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jacavi.appl.ContextLoader;
import de.jacavi.hal.FeedbackSignal;
import de.jacavi.rcp.util.Check;



/**
 * Adapts the functionality of NativeCsdLib for lib42 sensor detection and gets feedback distributed by the
 * Lib42FeedbackManager
 * 
 * @author fro
 */
public class Lib42FeedbackConnectorAdapter implements Lib42FeedbackConnector {
    private static Log log = LogFactory.getLog(Lib42FeedbackConnectorAdapter.class);

    private final int carID;

    private Lib42FeedbackManager feedbackManager = null;

    // The null Feedback
    private FeedbackSignal latestFeedback = null;

    private boolean isNew = false;

    public Lib42FeedbackConnectorAdapter(int carID) {
        Check.Require(carID > 0, "carID must be > 0");
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
        log.debug("Sensor callback for car id: " + carID + " sensor: " + sensorID);
        isNew = true;
        latestFeedback = new FeedbackSignal(null, sensorID + "");
    }

    @Override
    protected void finalize() throws Throwable {
        feedbackManager.removeFeedbackListener(this);
    }

    @Override
    public FeedbackSignal pollFeedback() {
        if(isNew) {
            isNew = false;
            return latestFeedback;
        } else
            return new FeedbackSignal(null, "0");
    }
}
