/*
 * Copyright (C) Skillworks AG 2008. All Rights Reserved Confidential
 */
package de.jacavi.appl.track;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;



public abstract class SlotPart {

    public class DirectedPoint {
        public final Point2D point;

        public final Angle angle;

        public DirectedPoint(Point2D point, Angle angle) {
            this.point = point;
            this.angle = angle;
        }
    }

    public final int length;

    public final Point entryPoint;

    public final Point exitPoint;

    public final int entryToExitAngle;

    public SlotPart(int length, Point entryPoint, Point exitPoint, int entryToExitAngle) {
        this.length = length;
        this.entryPoint = entryPoint;
        this.exitPoint = exitPoint;
        this.entryToExitAngle = entryToExitAngle;
    }

    abstract public Shape getShape();

    abstract public DirectedPoint getStepPoint(int position);
}