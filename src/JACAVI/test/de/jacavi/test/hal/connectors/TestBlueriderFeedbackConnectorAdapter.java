package de.jacavi.test.hal.connectors;

import java.net.InetSocketAddress;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarFeedbackConnector;



public class TestBlueriderFeedbackConnectorAdapter implements SlotCarFeedbackConnector {

    public TestBlueriderFeedbackConnectorAdapter(InetSocketAddress analogueDeviceAdress) {
    // TODO Auto-generated constructor stub
    }

    @Override
    public FeedbackSignal pollFeedback() {
        // TODO Auto-generated method stub
        return null;
    }
    /**
     * @author Florian Roth
     */
}
