/*
 * Copyright (C) Skillworks AG 2008. All Rights Reserved Confidential
 */
package de.jacavi.appl.track;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;



public class LineSlotPart extends SlotPart {
    public LineSlotPart(int length, Point entryPoint, Point exitPoint) {
        super(length, entryPoint, exitPoint);
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
        Angle angle = new Angle(0); // FIXME: not yet implemented
        return new DirectedPoint(point, angle);
    }
}