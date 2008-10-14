package de.jacavi.test.hal.connectors;

import java.net.InetSocketAddress;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarFeedbackConnector;



public class TestAnalogueFeddbackConnectorAdapter implements SlotCarFeedbackConnector {

    public TestAnalogueFeddbackConnectorAdapter(InetSocketAddress analogueDeviceAdress, int lane) {
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
