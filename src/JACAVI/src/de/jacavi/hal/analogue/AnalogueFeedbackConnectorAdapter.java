package de.jacavi.hal.analogue;

import java.net.InetSocketAddress;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarFeedbackConnector;



public class AnalogueFeedbackConnectorAdapter implements SlotCarFeedbackConnector {

    public AnalogueFeedbackConnectorAdapter(InetSocketAddress adress) {

    }

    @Override
    public FeedbackSignal pollFeedback() {
        // TODO Auto-generated method stub
        return new FeedbackSignal(null, "not implemented");
    }

    @Override
    public void resetSignal() {

    }

}
