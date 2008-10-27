/*
 * Copyright (C) Skillworks AG 2008. All Rights Reserved Confidential
 */
package de.jacavi.appl.track;

import java.awt.Point;
import java.awt.Shape;



/**
 * Represents a section of one of a tile's lanes.
 * <p>
 * More detailed information about how {@link Tile}s, {@link Lane}s and LaneSections related to each other can be found
 * in the {@link Lane}'s javadoc.
 * <p>
 * This is an abstract class that is supposed to be subclassed to describe a specific kind of lane section. It
 * implements the general bevaviour common to all lane section types.
 */
public abstract class LaneSection {

    public final int length;

    public final Point entryPoint;

    public final Point exitPoint;

    public final int entryToExitAngle;

    /**
     * Initializes length, entryPoint, exitPoint and entryToExitAngle which are common to all types of lane sections.
     */
    protected LaneSection(int length, Point entryPoint, Point exitPoint, int entryToExitAngle) {
        this.length = length;
        this.entryPoint = entryPoint;
        this.exitPoint = exitPoint;
        this.entryToExitAngle = entryToExitAngle;
    }

    /**
     * Returns a shape corresponding to the lane section.
     * <p>
     * This is mostly used for drawing the lane section for debugging purposes.
     */
    abstract public Shape getShape();

    /**
     * Returns a {@link DirectedPoint} describing the relative point and the angle of a car at the given position.
     * 
     * @param position
     *            the position (step index) inside this lane section to determine the step point of
     * @return a {@link DirectedPoint} describing the relative point and the angle of a car at the given position
     */
    abstract public DirectedPoint getStepPoint(int position);
}