package de.jacavi.appl.track;

import java.awt.Point;



public class Checkpoint {

    private final String id;

    private final Point point;

    private final int steps;

    private int laneIndex = 0;

    public int getLaneIndex() {
        return laneIndex;
    }

    public void setLaneIndex(int laneIndex) {
        this.laneIndex = laneIndex;
    }

    public Checkpoint(String id, int steps, Point point) {
        this.id = id;
        this.steps = steps;
        this.point = point;
    }

    public Checkpoint(String id, int steps, Point point, int trackSectionIndex, int laneIndex) {
        this.id = id;
        this.steps = steps;
        this.point = point;
        this.laneIndex = laneIndex;
    }

    public String getId() {
        return id;
    }

    public Point getPoint() {
        return point;
    }

    public int getSteps() {
        return steps;
    }

}
