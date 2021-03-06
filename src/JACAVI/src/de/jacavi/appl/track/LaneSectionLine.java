/*
 * Copyright (C) Skillworks AG 2008. All Rights Reserved Confidential
 */
package de.jacavi.appl.track;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;



/**
 * A lane section representing a path that describes a straight line.
 */
public class LaneSectionLine extends LaneSection {
    public LaneSectionLine(int length, Point entryPoint, Point exitPoint) {
        super(length, entryPoint, exitPoint, 0);
    }

    @Override
    public Shape getShape() {
        return new Line2D.Double(entryPoint.x, entryPoint.y, exitPoint.x, exitPoint.y);
    }

    @Override
    public DirectedPoint getStepPoint(int position) {
        Point2D point = new Point2D.Double(entryPoint.x + (exitPoint.x - entryPoint.x) * position
                / Double.valueOf(length), entryPoint.y + (exitPoint.y - entryPoint.y) * position
                / Double.valueOf(length));
        Angle angle = new Angle(0); // the angle of a line is always zero (relative to the entry angle!)
        return new DirectedPoint(point, angle);
    }
}