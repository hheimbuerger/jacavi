package de.jacavi.hal.analogue;

import java.net.InetSocketAddress;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.SlotCarFeedbackConnector;



/**
 * TODO: [ticket #10] implement me.
 */
public class AnalogueFeedbackConnectorAdapter implements SlotCarFeedbackConnector {

    public AnalogueFeedbackConnectorAdapter(InetSocketAddress adress) {

    }

    @Override
    public FeedbackSignal pollFeedback() {
        return new FeedbackSignal(null, "not implemented");
    }

}
