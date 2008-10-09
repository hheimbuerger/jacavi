package de.jacavi.hal.simulation;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarFeedbackConnector;



public class SimulationFeedbackConnectorAdapter implements SlotCarFeedbackConnector {

    private String name = "";

    public SimulationFeedbackConnectorAdapter(String name) {
        this.name = name;
    }

    @Override
    public FeedbackSignal pollFeedback() {
        return new FeedbackSignal(null, null);
    }

    @Override
    public void resetSignal() {

    }

    public String getName() {
        return name;
    }
}
