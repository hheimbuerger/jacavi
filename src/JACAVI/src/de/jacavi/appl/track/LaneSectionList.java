package de.jacavi.appl.track;

import java.awt.Point;
import java.util.ArrayList;



@SuppressWarnings("serial")
public class LaneSectionList extends ArrayList<LaneSection> {
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
        add(new LaneSectionLine(length, entryPoint, exitPoint));
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
        add(new LaneSectionQuadBezier(length, entryPoint, controlPoint, exitPoint, entryToExitAngle));
    }

    public int getLength() {
        int length = 0;
        for(LaneSection ls: this)
            length += ls.length;
        return length;
    }
}
