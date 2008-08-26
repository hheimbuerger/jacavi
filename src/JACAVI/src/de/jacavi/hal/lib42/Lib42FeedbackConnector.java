package de.jacavi.hal.lib42;

import de.jacavi.hal.SlotCarFeedbackConnector;



public interface Lib42FeedbackConnector extends SlotCarFeedbackConnector {
    void sensorCallback(int carID, int sensorID);
}
