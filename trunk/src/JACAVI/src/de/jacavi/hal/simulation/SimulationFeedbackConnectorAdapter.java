package de.jacavi.hal.simulation;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarFeedbackConnector;



public class SimulationFeedbackConnectorAdapter implements SlotCarFeedbackConnector {

    @Override
    public FeedbackSignal pollFeedback() {
        return new FeedbackSignal(null, null);
    }
}
