package de.jacavi.hal.lib42;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.jacavi.rcp.util.Check;



/**
 * The lib42FeedbackManager starts the native lib42 sensor detection library and gives opportunity to add
 * LIb42FeedbackConnectors as listeners. Listeners could be removed. Listeners will only get their specific callbacks
 * 
 * @author fro
 */
public class Lib42FeedbackManager {
    private static Log log = LogFactory.getLog(Lib42FeedbackManager.class);

    private Map<String, Lib42FeedbackConnector> feedbackConnectors = new TreeMap<String, Lib42FeedbackConnector>();

    public Lib42FeedbackManager() throws Exception {
        // start the only instance of the NativeCsdLib
        NativeCsdLib.startLib42Sensordetection(this);
    }

    /**
     * Add a Lib42FeedbackConnector as listener on his specific sensor callbacks
     * 
     * @param feedbackConnector
     */
    public void addFeedbackListener(Lib42FeedbackConnector feedbackConnector) {
        Check.Require(feedbackConnector != null, "feedbackConnector may not be null");
        // if there already exists a feedbackConnector kill him
        if(feedbackConnectors.containsKey(feedbackConnector.getCarID() + "")) {
            feedbackConnectors.remove(feedbackConnector.getCarID() + "");
        }
        // add the new feedback listener
        feedbackConnectors.put(feedbackConnector.getCarID() + "", feedbackConnector);
    }

    /**
     * Remove a Lib42FeedbackConnector.
     * 
     * @param feedbackConnector
     *            the Lib42FeedbackConnector whos not interessted in further sensorcallbacks
     */
    public void removeFeedbackListener(Lib42FeedbackConnector feedbackConnector) {
        if(feedbackConnectors.containsKey(feedbackConnector.getCarID()))
            feedbackConnectors.remove(feedbackConnector.getCarID() + "");
        else
            throw new IllegalArgumentException("there is no lib42 feedbackConnector with carID: "
                    + feedbackConnector.getCarID() + " registered");
    }

    /**
     * Callback function called from NativeCsdLib. Distributes callbacks from native lib to their specific
     * LIb42FeedbackConnectors
     * 
     * @param carID
     * @param sensorID
     */
    public void distributeFeedback(int carID, int sensorID) {
        log.debug("FeedbackManager distributes feedback to carid: " + carID + " of sensor" + sensorID);
        if(feedbackConnectors.containsKey(carID + ""))
            feedbackConnectors.get(carID + "").sensorCallback(sensorID);
    }

}
