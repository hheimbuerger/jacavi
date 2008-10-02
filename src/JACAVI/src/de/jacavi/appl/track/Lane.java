package de.jacavi.appl.track;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;



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
 * Optionally, the lane sections can be separated into three groups:
 * <ul>
 * <li><strong>common</strong> lane sections are always taken
 * <li><strong>regular</strong> lane sections are taken after the common ones if the car does not take the lane change
 * path
 * <li><strong>change</strong> lane sections are taken after the common ones if the car does take the lane change path
 * </ul>
 * <p>
 * Instances of this class are created by the {@link TilesetRepository} during initialization and used by the
 * {@link TrackWidget} to draw the cars on the track.
 * <p>
 * Note that the sum of all entryToExitAngles of the curved lane sections should be the same as the tile's
 * entryToExitAngle. While the tile's entryToExitAngle is used to place the rotate the following tile accordingly, the
 * lane sections' entryToExitAngles are added to the following lane sections' angles.
 */
public class Lane {

    /** The list of base lane sections this lane consists of. */
    private final LaneSectionList laneSectionsCommon = new LaneSectionList();

    /** The list of regular lane sections this lane consists of. */
    private final LaneSectionList laneSectionsRegular = new LaneSectionList();

    /** The list of change lane sections this lane consists of. */
    private final LaneSectionList laneSectionsChange = new LaneSectionList();

    /** The list of checkpoints this lane has. */
    private final List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();

    /** The lane index this lane ends on in the regular case. */
    private int regularExitLaneIndex;

    /** The lane index this land ends on in the lane change case. */
    private int changeExitLaneIndex;

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
    public void addCheckpoint(String id, int steps, int x, int y) {
        checkpoints.add(new Checkpoint(id, steps, new Point(x, y)));
    }

    public LaneSectionList getLaneSectionsCommon() {
        return laneSectionsCommon;
    }

    public LaneSectionList getLaneSectionsRegular() {
        return laneSectionsRegular;
    }

    public LaneSectionList getLaneSectionsChange() {
        return laneSectionsChange;
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
     * Returns an boolean if there are checkpoints on this lane
     * 
     * @return boolean true if the Lane has checkpoints otherwise false
     */
    public boolean hasCheckpoints() {
        if(checkpoints.size() > 0)
            return true;
        else
            return false;
    }

    public void setExits(int regularExit, int changeExit) {
        regularExitLaneIndex = regularExit;
        changeExitLaneIndex = changeExit;
    }

    public int getRegularExitLaneIndex() {
        return regularExitLaneIndex;
    }

    public int getChangeExitLaneIndex() {
        return changeExitLaneIndex;
    }

    /**
     * Returns a {@link DirectedPoint} describing the relative point and the angle of a car at the given position.
     * <p>
     * The point is given relative to the tile's center.
     * 
     * @param position
     *            the position to determine the step point of
     * @return a {@link DirectedPoint} describing the relative point and the angle of a car at the given position
     */
    public DirectedPoint getStepPoint(CarPosition position) {
        int currentPos = 0;
        Angle currentAngle = new Angle(0);
        for(LaneSection ls: laneSectionsCommon) {
            if(position.stepsInTile < currentPos + ls.length) {
                DirectedPoint stepPoint = ls.getStepPoint(position.stepsInTile - currentPos);
                stepPoint.angle.turn(currentAngle);
                return stepPoint;
            }
            currentPos += ls.length;
            currentAngle.turn(ls.entryToExitAngle);
        }
        return null;
    }

}
