package de.jacavi.appl.track;

import java.awt.Point;



public class Checkpoint {

    private final String id;

    private final Point point;

    private final int steps;

    public Checkpoint(String id, int steps, Point point) {
        this.id = id;
        this.steps = steps;
        this.point = point;
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
