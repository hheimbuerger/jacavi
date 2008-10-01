package de.jacavi.appl.racelogic.tda;

import java.util.HashMap;
import java.util.Map;

import de.jacavi.appl.car.Car;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;



public class ExperimentalSimulationTDA extends TrackDataApproximator {

    private FeedbackSignal lastFeedback = null;

    private int lastGametick = 0;

    private Map<FeedbackSignal, CarPosition> map = new HashMap<FeedbackSignal, CarPosition>();

    @Override
    public void updatePosition(CarPosition carPosition, int gametick, Car car, Track track,
            ControllerSignal controllerSignal, FeedbackSignal feedbackSignal) {

        if(lastGametick == 0)
            initSensors();

        if(feedbackSignal != lastFeedback) {
            // move the car to detected sensor
            carPosition.moveSteps(track, getStepsToDetectedSensor(feedbackSignal, carPosition), controllerSignal
                    .isTrigger());
            lastFeedback = feedbackSignal;
        } else {
            // let the car role in the way the DebugTDA does
        }
        lastGametick = gametick;
    }

    private void initSensors() {
    // fill the Sensormap here
    // map.put(new FeedbackSignal(null,"key"),bla )
    }

    private int getStepsToDetectedSensor(FeedbackSignal sensor, CarPosition currentPosition) {
        int result = 0;
        if(map.containsKey(sensor)) {
            CarPosition sensorPosition = map.get(sensor);
            // determine stepsToMove from current carposition to sensorposition on track
            // result=stepsToMoveToSensor
        }

        return result;
    }
}
