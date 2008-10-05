package de.jacavi.appl.racelogic.tda;

import de.jacavi.appl.car.Car;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;



public class ExperimentalLib42TDA extends TrackDataApproximator {

    private FeedbackSignal lastFeedback = new FeedbackSignal(null, "0");

    private int lastGametick = 0;

    private int currentSpeed = 0;

    private int stepsAlreadyMoved = 0;

    private int stepsDeltaCPnextCP = 0;

    private int lapsOnLastCheckpoint = 0;

    public ExperimentalLib42TDA(Track track, int racetimerIntreval) {
        this.track = track;
        this.raceTimerInterval = racetimerIntreval;
        initializeCheckpointMap();
    }

    @Override
    public void updatePosition(CarPosition carPosition, int gametick, Car car, ControllerSignal controllerSignal,
            FeedbackSignal feedbackSignal) {

        // save current speed
        currentSpeed = getStepsPerGametickCalculated(controllerSignal, car, gametick);

        // if its not the same checkpoint triggered before
        if(!feedbackSignal.getLastCheckpoint().equals(lastFeedback.getLastCheckpoint())) {

            // check if we have a sensorposition for the feedback
            String key = feedbackSignal.getLastCheckpoint();

            if(checkpoints.containsKey(key)) {
                CheckpointData checkpointData = checkpoints.get(key);

                // if currentCheckpoint is the first on this lane and the number of leps on last sensor is the same as
                // now but greater 0
                boolean incrementLap = false;
                if((checkpointsByLane.get(checkpointData.getLaneIndex()).get(0).getId() == checkpointData.getId())
                        && (lapsOnLastCheckpoint == carPosition.lap)) {
                    incrementLap = true;
                }

                // move the car to detected sensor
                carPosition.setPosition(checkpointData.getTrackSectionIndex(), checkpointData.getLaneIndex(),
                        checkpointData.getSteps(), incrementLap);
                // init stepsMoved since this checkpoint
                stepsAlreadyMoved = 0;
                // set steps to next cp
                stepsDeltaCPnextCP = checkpoints.get(feedbackSignal.getLastCheckpoint()).getStepsToNext();
                lapsOnLastCheckpoint = carPosition.lap;
            }
            lastFeedback = feedbackSignal;
        } else {
            // no new checkpoint triggered
            // int stepsXY = sensorPositions.get(lastFeedback.getLastCheckpoint()).getStepsToNext();
            int stepsToNextSensor = stepsDeltaCPnextCP - stepsAlreadyMoved;
            if(currentSpeed > 0) {
                // time to next sensor in gameticks
                int timeToNextSensor = 1;
                if(stepsToNextSensor > currentSpeed) {
                    timeToNextSensor = stepsToNextSensor / currentSpeed;
                }
                int stepsToMove = stepsToNextSensor / timeToNextSensor;
                // set steps already moved
                stepsAlreadyMoved += stepsToMove;
                carPosition.moveSteps(track, stepsToMove, controllerSignal.isTrigger());
            }
        }
        lastGametick = gametick;
    }

    private int getStepsPerGametickCalculated(ControllerSignal controllerSignal, Car car, int gametick) {

        if(gametick == 0)
            currentSpeed = 0;

        // what we have
        double carMass = car.getMass();
        double carAcceleration = car.getAcceleration();
        double carTopSpped = car.getTopSpeed();
        double carInertia = car.getInertia();

        int lastSpeed = currentSpeed;

        // what we can get

        // now we have the way since raceTimerInterval to now in steps
        double DeltaXY = currentSpeed / raceTimerInterval;

        // TODO: hate physics
        // FIXME:
        return controllerSignal.getSpeed() / 2;
    }
}
