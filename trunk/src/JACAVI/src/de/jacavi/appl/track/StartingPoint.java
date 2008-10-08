package de.jacavi.appl.track;

public class StartingPoint {
    private final int laneIndex;

    private final int steps;

    public StartingPoint(int laneIndex, int steps) {
        this.laneIndex = laneIndex;
        this.steps = steps;
    }

    public int getLaneIndex() {
        return laneIndex;
    }

    public int getSteps() {
        return steps;
    }

}
