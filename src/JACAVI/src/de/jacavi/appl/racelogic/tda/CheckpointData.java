package de.jacavi.appl.racelogic.tda;

public class CheckpointData {

    private String id;

    private int stepsToNext;

    private int steps;

    private int trackSectionIndex;

    private int laneIndex;

    public CheckpointData(String id, int trackSectionIndex, int laneIndex, int steps) {
        this.id = id;
        this.trackSectionIndex = trackSectionIndex;
        this.laneIndex = laneIndex;
        stepsToNext = 0;
    }

    public int getSteps() {
        return steps;
    }

    public int getStepsToNext() {
        return stepsToNext;
    }

    public void setStepsToNext(int stepsToNext) {
        this.stepsToNext = stepsToNext;
    }

    public String getId() {
        return id;
    }

    public int getTrackSectionIndex() {
        return trackSectionIndex;
    }

    public int getLaneIndex() {
        return laneIndex;
    }

}
