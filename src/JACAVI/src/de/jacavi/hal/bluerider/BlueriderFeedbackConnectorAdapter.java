package de.jacavi.hal.bluerider;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.Gforce;
import de.jacavi.hal.SlotCarFeedbackConnector;
import de.jacavi.hal.analogue.AnalogueFeedbackConnectorAdapter;
import de.jacavi.rcp.util.Check;



/**
 * @author flo
 *         <p>
 *         The BlueriderFeedbackConnectorAdapter includes an AnalogueFeedbackConnector to get sensor information of the
 *         analogue course. Gforce information from bluerider is given by subscribing it as ComListener on the gForce
 *         ports.
 */
public class BlueriderFeedbackConnectorAdapter implements SlotCarFeedbackConnector, ComListener {

    private AnalogueFeedbackConnectorAdapter analogueSensorDetection = null;

    private double currentXAcceleration;

    private double currentYAcceleration;

    private final Integer log = new Integer(0);

    public BlueriderFeedbackConnectorAdapter(AnalogueFeedbackConnectorAdapter analogFeedback) {
        Check.Require(analogFeedback != null, "AnalogueFeedbackConnector may not be null");
        this.analogueSensorDetection = analogFeedback;
        // subscribe to get gforce feedback
        ComManager.getInstanceOfCM().addComListener(this, ComManager.FIX_1); // Y
        ComManager.getInstanceOfCM().addComListener(this, ComManager.FIX_2); // x
    }

    @Override
    public FeedbackSignal pollFeedback() {
        return new FeedbackSignal(new Gforce(currentXAcceleration, currentYAcceleration), analogueSensorDetection
                .pollFeedback().getCheckpoint());
    }

    @Override
    public void fixDataReceived(byte b, int index) {
        int k = 0;
        k = (short) (0x00FF & b);
        synchronized(log) {
            if(index == ComManager.FIX_2) // Y
            {
                currentYAcceleration = ((((double) k - 128) / 127) * 5 * -1);

            } else if(index == ComManager.FIX_1)// x
            {
                currentXAcceleration = ((((double) k - 128) / 127) * 5 * -1);
            }
        }
    }

    @Override
    public void msgReceived(Message m, int index) {
    // TODO Auto-generated method stub

    }
}
