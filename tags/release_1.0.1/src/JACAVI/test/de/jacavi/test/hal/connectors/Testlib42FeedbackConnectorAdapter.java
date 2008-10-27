package de.jacavi.test.hal.connectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.jacavi.hal.FeedbackSignal;
import de.jacavi.hal.lib42.Lib42FeedbackConnector;



public class Testlib42FeedbackConnectorAdapter implements Lib42FeedbackConnector {

    private final int carID;

    private final List<String> feedbackList = new ArrayList<String>();

    private int feedbackIndex = 1;

    private boolean start = false;

    public Testlib42FeedbackConnectorAdapter(int carID) {
        this.carID = carID;
        initFeedback();
    }

    private void initFeedback() {
        // on the htwg digital track we have sensorst from 1-16
        for(int i = 1; i <= 15; i += 2) {
            feedbackList.add(i + "");
        }
    }

    @Override
    public FeedbackSignal pollFeedback() {
        if(!start) {
            start = true;
            Timer timer = new Timer();
            timer.schedule(new FeedbackTimerTask(), 10, 800);
        }
        return new FeedbackSignal(null, feedbackIndex + "");
    }

    @Override
    public int getCarID() {
        return carID;
    }

    @Override
    public void sensorCallback(int sensorID) {

    }

    class FeedbackTimerTask extends TimerTask {
        @Override
        public void run() {
            feedbackIndex += 2;
            if(feedbackIndex > 15)
                feedbackIndex = 1;
        }
    }

}
