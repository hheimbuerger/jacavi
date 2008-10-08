package de.jacavi.appl.racelogic.tda;

import de.jacavi.appl.car.Car;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.appl.track.Track;
import de.jacavi.hal.FeedbackSignal;



public class ExperimentalLib42TDA extends TrackDataApproximator {

    private FeedbackSignal lastFeedback = new FeedbackSignal(null, "0");

    private double speed = 0;

    private int stepsAlreadyMoved = 0;

    private int stepsDeltaCPnextCP = 0;

    private int lapsOnLastCheckpoint = 0;

    private double acceleration = 0.0;

    public ExperimentalLib42TDA(Track track, int racetimerIntreval) {
        this.track = track;
        this.raceTimerInterval = racetimerIntreval;

        initializeCheckpointMap();
    }

    @Override
    public void updatePosition(CarPosition carPosition, int gametick, Car car, ControllerSignal controllerSignal,
            FeedbackSignal feedbackSignal) {

        // save current speed
        speed = getStepsPerGametickCalculated(controllerSignal, car, gametick);

        if(carPosition.isOnTrack) {

            // if its not the same checkpoint triggered before
            if(!feedbackSignal.getLastCheckpoint().equals(lastFeedback.getLastCheckpoint())) {

                // check if we have a sensorposition for the feedback
                String key = feedbackSignal.getLastCheckpoint();

                if(checkpoints.containsKey(key)) {
                    CheckpointData checkpointData = checkpoints.get(key);

                    // if currentCheckpoint is the first on this lane and the number of leps on last sensor is the same
                    // as
                    // now but greater 0
                    boolean incrementLap = false;
                    if((checkpointsByLane.get(checkpointData.getLaneIndex()).get(0).getId() == checkpointData.getId())
                            && (lapsOnLastCheckpoint == carPosition.lap)) {
                        incrementLap = true;
                    }

                    // move the car to detected sensor
                    carPosition.setPosition(checkpointData.getTrackSectionIndex(), checkpointData.getLaneIndex(),
                            checkpointData.getSteps(), incrementLap);

                    System.out.println("CheckpintSteps: " + checkpointData.getSteps());

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
                if(speed > 0) {
                    // time to next sensor in gameticks
                    int timeToNextSensor = 1;
                    if(stepsToNextSensor > (int) speed) {
                        timeToNextSensor = stepsToNextSensor / (int) speed;
                    }
                    int stepsToMove = stepsToNextSensor / timeToNextSensor;
                    // set steps already moved
                    stepsAlreadyMoved += stepsToMove;

                    if(stepsToMove > car.getTopSpeed() * 0.9)
                        carPosition.leaveTrack();
                    else
                        carPosition.moveSteps(track, stepsToMove, controllerSignal.isTrigger());
                }
            }
        }
    }

    private int getStepsPerGametickCalculated(ControllerSignal controllerSignal, Car car, int gametick) {

        if(gametick == 0)
            speed = 0;

        // car acceleration factor is defined in car.xml
        double thrust = controllerSignal.getThrust() * car.getAcceleration();

        // calculate the friction 0.01 is car on concrete
        double friction = (car.getMass() * 0.01) * -1;

        acceleration = thrust / car.getMass();
        speed = Math.max(Math.min((acceleration * raceTimerInterval) + (friction * raceTimerInterval) + speed, car
                .getTopSpeed()), 0);

        if(speed > getMaxSpeed(controllerSignal.getThrust(), car)) {
            if(getMaxSpeed(controllerSignal.getThrust(), car) == 0)
                speed--;
            else
                speed = getMaxSpeed(controllerSignal.getThrust(), car);
        }
        return (int) speed;
    }

    private double getMaxSpeed(int controllerSignal, Car car) {
        double result = 0;

        result = (car.getTopSpeed() / 100) * controllerSignal;

        return result;
    }
}
