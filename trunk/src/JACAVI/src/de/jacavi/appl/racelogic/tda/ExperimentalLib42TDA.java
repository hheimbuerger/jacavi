package de.jacavi.appl.racelogic.tda;

import java.util.HashMap;
import java.util.Map;

import de.jacavi.appl.car.Car;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.appl.track.Checkpoint;
import de.jacavi.appl.track.Lane;
import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.TrackSection;
import de.jacavi.hal.FeedbackSignal;



public class ExperimentalLib42TDA extends TrackDataApproximator {

    private FeedbackSignal lastFeedback = new FeedbackSignal(null, "0");

    private int lastGametick = 0;

    private String startFinishSensorKey = "";

    private Map<String, SensorPosition> sensorPositions = new HashMap<String, SensorPosition>();

    class SensorPosition {
        private int trackSectionIndex;

        private int stepsInTile;

        public int getTrackSectionIndex() {
            return trackSectionIndex;
        }

        public int getStepsInTile() {
            return stepsInTile;
        }

        public int getLaneIndex() {
            return laneIndex;
        }

        private int laneIndex;

        public SensorPosition(int trackSectionIndex, int stepsInTile, int laneIndex) {
            this.trackSectionIndex = trackSectionIndex;
            this.stepsInTile = stepsInTile;
            this.laneIndex = laneIndex;
        }
    }

    @Override
    public void updatePosition(CarPosition carPosition, int gametick, Car car, Track track,
            ControllerSignal controllerSignal, FeedbackSignal feedbackSignal) {

        if(gametick == 0)
            initSensorMap(track);

        // if its not the same checkpoint triggered before
        if(!feedbackSignal.getLastCheckpoint().equals(lastFeedback.getLastCheckpoint())) {

            // check if we have a sensorposition for the feedback
            String key = feedbackSignal.getLastCheckpoint();
            if(sensorPositions.containsKey(key)) {
                SensorPosition sensorPosition = sensorPositions.get(key);
                // move the car to detected sensor
                carPosition.setPosition(sensorPosition.getTrackSectionIndex(), sensorPosition.getLaneIndex(),
                        sensorPosition.getStepsInTile(),/*TODO*/false);
            }
            lastFeedback = feedbackSignal;
        } else {
            // let the car role in the way the DebugTDA does
        }
        lastGametick = gametick;
    }

    /**
     * Build the SensorMap
     * 
     * @param track
     */
    private void initSensorMap(Track track) {
        boolean found = false;
        int trackSectionIndex = 0;
        for(TrackSection section: track.getSections()) {
            int laneIndex = 0;
            for(Lane lane: section.getTile().getLanes()) {
                if(lane.hasCheckpoints()) {
                    for(Checkpoint check: lane.getCheckpoints()) {
                        // remember the first(start/finsh) sensor
                        if(!found) {
                            startFinishSensorKey = check.getId();
                            found = true;
                        }
                        sensorPositions.put(check.getId(), new SensorPosition(trackSectionIndex, check.getSteps(),
                                laneIndex));
                    }
                }
                laneIndex++;
            }
            trackSectionIndex++;
        }
    }
}
