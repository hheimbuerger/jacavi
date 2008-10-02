package de.jacavi.test.hal.connectors;

import java.util.ArrayList;
import java.util.List;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarFeedbackConnector;



public class TestSimulationFeedbackConnectorAdapter implements SlotCarFeedbackConnector {

    private String name;

    private List<String> feedbackList = new ArrayList<String>();

    private int feedbackIndex = 0;

    public TestSimulationFeedbackConnectorAdapter(String name) {
        this.name = name;
        initFeedback();
    }

    private void initFeedback() {
        for(int i = 0; i < 27; i++) {
            feedbackList.add(i + "");
        }
    }

    @Override
    public FeedbackSignal pollFeedback() {
        int tmpIndex = feedbackIndex;
        feedbackIndex++;
        if(feedbackIndex >= 6)
            feedbackIndex = 0;
        return new FeedbackSignal(null, feedbackList.get(tmpIndex));
    }

}
