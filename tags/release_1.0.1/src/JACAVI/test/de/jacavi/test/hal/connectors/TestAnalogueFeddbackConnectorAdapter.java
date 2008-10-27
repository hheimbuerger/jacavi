package de.jacavi.test.hal.connectors;

import java.net.InetSocketAddress;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarFeedbackConnector;



public class TestAnalogueFeddbackConnectorAdapter implements SlotCarFeedbackConnector {

    public TestAnalogueFeddbackConnectorAdapter(InetSocketAddress analogueDeviceAdress, int lane) {}

    @Override
    public FeedbackSignal pollFeedback() {

        return null;
    }

}
