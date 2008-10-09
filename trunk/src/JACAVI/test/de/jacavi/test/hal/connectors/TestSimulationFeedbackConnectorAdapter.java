package de.jacavi.test.hal.connectors;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarFeedbackConnector;



public class TestSimulationFeedbackConnectorAdapter implements SlotCarFeedbackConnector {

    public TestSimulationFeedbackConnectorAdapter(String name) {}

    @Override
    public FeedbackSignal pollFeedback() {
        return new FeedbackSignal(null, "0");
    }

    @Override
    public void resetSignal() {

    }

}
