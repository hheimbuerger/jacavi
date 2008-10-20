package de.jacavi.test.hal.connectors;

import java.net.InetSocketAddress;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarFeedbackConnector;



public class TestBlueriderFeedbackConnectorAdapter implements SlotCarFeedbackConnector {

    public TestBlueriderFeedbackConnectorAdapter(InetSocketAddress analogueDeviceAdress) {

    }

    @Override
    public FeedbackSignal pollFeedback() {

        return null;
    }

}
