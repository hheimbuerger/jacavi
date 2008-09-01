package de.jacavi.test.hal.connectors;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.lib42.Lib42FeedbackConnector;



public class Testlib42FeedbackConnectorAdapter implements Lib42FeedbackConnector {

    private int carid;

    public Testlib42FeedbackConnectorAdapter(int carID) {
        this.carid = carID;
    }

    @Override
    public int getCarID() {
        return carid;
    }

    @Override
    public void sensorCallback(int sensorID) {
    // TODO Auto-generated method stub

    }

    @Override
    public FeedbackSignal pollFeedback() {
        // TODO Auto-generated method stub
        return null;
    }

}
