package de.jacavi.test.hal.connectors;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarFeedbackConnector;



public class TestSimulationFeedbackConnectorAdapter implements SlotCarFeedbackConnector {

    private String name;

    public TestSimulationFeedbackConnectorAdapter(String name) {
        this.name = name;
    }

    @Override
    public FeedbackSignal pollFeedback() {
        return new FeedbackSignal(null, "0");
    }

}
