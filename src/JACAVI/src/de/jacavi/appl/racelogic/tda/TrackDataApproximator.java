package de.jacavi.appl.racelogic.tda;

import java.util.HashMap;
import java.util.Map;

import de.jacavi.appl.car.Car;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.CarPosition;
import de.jacavi.appl.track.Checkpoint;
import de.jacavi.appl.track.Lane;
import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.TrackSection;
import de.jacavi.hal.FeedbackSignal;
import de.jacavi.rcp.util.RingList;



abstract public class TrackDataApproximator {

    // current Track
    protected Track track;

    // the owner of this TDA instance
    protected Player player;

    protected int raceTimerInterval;

    protected double speed = 0;

    protected double acceleration = 0.0;

    protected Map<String, CheckpointData> checkpoints = new HashMap<String, CheckpointData>();

    protected Map<Integer, RingList<CheckpointData>> checkpointsByLane = new HashMap<Integer, RingList<CheckpointData>>();

    public abstract void updatePosition(CarPosition carPosition, int gametick, Car car,
            ControllerSignal controllerSignal, FeedbackSignal feedbackSignal);

    /**
     * Build the CheckpointMap of the track. Do this in your TDA in the constructor if you need.
     * 
     * @param track
     */
    protected void initializeCheckpointMap() {

        for(int trackSectionIndex = 0; trackSectionIndex < track.getSections().size(); trackSectionIndex++) {
            TrackSection section = track.getSections().get(trackSectionIndex);
            for(int laneIndex = 0; laneIndex < section.getTile().getLanes().size(); laneIndex++) {
                Lane lane = section.getLane(laneIndex);
                if(lane.hasCheckpoints()) {
                    for(Checkpoint check: lane.getCheckpoints()) {
                        check.setLaneIndex(laneIndex);
                        CheckpointData newCheckpointData = new CheckpointData(check.getId(), trackSectionIndex,
                                laneIndex, check.getSteps());
                        // add to the simple queue to map feedback to gui
                        checkpoints.put(check.getId(), newCheckpointData);
                        if(checkpointsByLane.get(laneIndex) == null)
                            checkpointsByLane.put(laneIndex, new RingList<CheckpointData>());
                        // add to the data list to remember later needed data -> map<laneIndex,List of checkps
                        checkpointsByLane.get(laneIndex).add(newCheckpointData);
                    }
                }
            }
        }

        // set the steps to next cp
        for(int ilane = 0; ilane < checkpointsByLane.size(); ilane++) {
            RingList<CheckpointData> cpList = checkpointsByLane.get(ilane);
            for(CheckpointData cp: cpList) {
                cp.setStepsToNext(getStepsToNextCheckpoint(cp));
            }
        }
    }

    /**
     * Returns steps from given checkpoint to the next checkpoint on this lane
     * 
     * @param checkpoint
     * @return the steps
     */
    protected int getStepsToNextCheckpoint(CheckpointData checkpoint) {
        int result = 0;

        // get the next Checkpoint on this lane
        CheckpointData nextCheckpoint = checkpointsByLane.get(checkpoint.getLaneIndex()).getNext(checkpoint);

        boolean count = false;
        boolean endDetected = false;

        while(!endDetected) {
            int laneIndex = checkpoint.getLaneIndex();

            for(int trackSectionIndex = 0; trackSectionIndex < track.getSections().size(); trackSectionIndex++) {
                TrackSection currentSection = track.getSections().get(trackSectionIndex);

                // section steps (length) is common + regular
                int currentLaneSteps = currentSection.getLane(laneIndex).getLaneSectionsCommon().getLength()
                        + currentSection.getLane(laneIndex).getLaneSectionsRegular().getLength();

                if(count) {
                    result += currentLaneSteps;
                }

                if(currentSection.getLane(laneIndex).hasCheckpoints()) {
                    // TODO: i sai that there is only one checkpoint do it for all
                    // start counting steps
                    if(currentSection.getLane(laneIndex).getCheckpoints().get(0).getId().equals(checkpoint.getId())) {
                        // start the step count here
                        count = true;
                        // add to result lanesteps - checkpoint steps
                        result += (currentLaneSteps - checkpoints.get(checkpoint.getId()).getSteps());
                    }
                    // stop counting steps
                    else if(count
                            && (currentSection.getLane(laneIndex).getCheckpoints().get(0).getId().equals(nextCheckpoint
                                    .getId()))) {
                        count = false;
                        endDetected = true;
                        // correct the result
                        result -= currentLaneSteps;
                        result += checkpoints.get(nextCheckpoint.getId()).getSteps();
                        break;
                    }
                }
                // set laneIndex for X cross
                laneIndex = currentSection.getLane(laneIndex).getRegularExitLaneIndex();
            }
        }
        return result;
    }

    /**
     * Resets the TDA
     * <p>
     * Currently used if the a crashed car gets a reset ControllerSignal
     */
    public void reset() {
        speed = 0;
        acceleration = 0;
    }

}
