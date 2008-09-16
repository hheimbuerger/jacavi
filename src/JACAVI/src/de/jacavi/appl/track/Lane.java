package de.jacavi.appl.track;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import de.jacavi.rcp.widgets.TrackWidget;



/**
 * Represents a lane of a tile.
 * <p>
 * Every track consists of one or more lanes. The lane is usually not directly visualized but describes the path used to
 * draw a car when it is driving over a tile.
 * <p>
 * A lane consists of one or more lane sections, each of which describes a part of the path that is taken by the car.
 * This helps describing the lane as different lane part types can be used, most notably line (a straight point A to
 * point B connection) and quad-bezier (using a Beziér curve to describe a curved part of the lane).
 * <p>
 * Instances of this class are created by the {@link TilesetRepository} during initialization and used by the
 * {@link TrackWidget} to draw the cars on the track.
 * <p>
 * Note that the sum of all entryToExitAngles of the curved lane sections should be the same as the tile's
 * entryToExitAngle. While the tile's entryToExitAngle is used to place the rotate the following tile accordingly, the
 * lane sections' entryToExitAngles are added to the following lane sections' angles.
 */
public class Lane {

    /** The list of lane sections this lane consists of. */
    private final List<LaneSection> laneSections = new ArrayList<LaneSection>();

    /** The list of checkpoints this lane has. */
    private final List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();

    /**
     * Appends a new linear lane section at the end of the lane.
     * 
     * @param length
     *            the number of steps a car has to take over this lane section
     * @param x1
     *            the x-coordinate of the starting point of the lane section
     * @param y1
     *            the y-coordinate of the starting point of the lane section
     * @param x2
     *            the x-coordinate of the ending point of the lane section
     * @param y2
     *            the y-coordinate of the ending point of the lane section
     */
    public void addLine(int length, int x1, int y1, int x2, int y2) {
        Point entryPoint = new Point(x1, y1);
        Point exitPoint = new Point(x2, y2);
        laneSections.add(new LaneSectionLine(length, entryPoint, exitPoint));
    }

    /**
     * Appends a new quadratic Beziér curve lane section at the end of the lane.
     * 
     * @param length
     *            the number of steps a car has to take over this lane section
     * @param x1
     *            the x-coordinate of the starting point of the lane section
     * @param y1
     *            the y-coordinate of the starting point of the lane section
     * @param x2
     *            the x-coordinate of the control point of the lane section
     * @param y2
     *            the y-coordinate of the control point of the lane section
     * @param x3
     *            the x-coordinate of the ending point of the lane section
     * @param y3
     *            the y-coordinate of the ending point of the lane section
     * @param entryToExitAngle
     *            the difference between the car's angle at the entry point and the exit point of the lane section
     */
    public void addQuadBezier(int length, int x1, int y1, int x2, int y2, int x3, int y3, int entryToExitAngle) {
        Point entryPoint = new Point(x1, y1);
        Point controlPoint = new Point(x2, y2);
        Point exitPoint = new Point(x3, y3);
        laneSections.add(new LaneSectionQuadBezier(length, entryPoint, controlPoint, exitPoint, entryToExitAngle));
    }

    /**
     * Adds a new checkpoint to this lane's checkpoint list.
     * 
     * @param id
     *            the identifier of this checkpoint
     * @param x
     *            the x-coordinate of the checkpoint
     * @param y
     *            the y-coordinate of the checkpoint
     */
    public void addCheckpoint(String id, int x, int y) {
        checkpoints.add(new Checkpoint(id, new Point(x, y)));
    }

    /**
     * Returns the list of all lane sections of this lane.
     * 
     * @return a list of all lane sections of this lane
     */
    public List<LaneSection> getLaneSections() {
        return laneSections;
    }

    /**
     * Returns the list of all checkpoints defined for this lane.
     * 
     * @return a list of all checkpoints of this lane
     */
    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    /**
     * Returns the total length (number of steps) this lane consists of.
     * <p>
     * The length of a lane is always equal to the sum of all the lengths of all the lane's sections.
     * 
     * @return the total length (number of steps) of this lane
     */
    public int getLength() {
        int length = 0;
        for(LaneSection ls: laneSections)
            length += ls.length;
        return length;
    }

    /**
     * Returns a {@link DirectedPoint} describing the relative point and the angle of a car at the given position.
     * <p>
     * The point is given relative to the tile's center.
     * 
     * @param position
     *            the position (step index) to determine the step point of
     * @return a {@link DirectedPoint} describing the relative point and the angle of a car at the given position
     */
    public DirectedPoint getStepPoint(int position) {
        int currentPos = 0;
        Angle currentAngle = new Angle(0);
        for(LaneSection ls: laneSections) {
            if(position < currentPos + ls.length) {
                DirectedPoint stepPoint = ls.getStepPoint(position - currentPos);
                stepPoint.angle.turn(currentAngle);
                return stepPoint;
            }
            currentPos += ls.length;
            currentAngle.turn(ls.entryToExitAngle);
        }
        return null;
    }

}
