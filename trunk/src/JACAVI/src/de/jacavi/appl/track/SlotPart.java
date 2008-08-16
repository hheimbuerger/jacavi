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

    public int length;

    public Point entryPoint;

    public Point exitPoint;

    public SlotPart(int length, Point entryPoint, Point exitPoint) {
        this.length = length;
        this.entryPoint = entryPoint;
        this.exitPoint = exitPoint;
    }

    abstract public Shape getShape();

    abstract public DirectedPoint getStepPoint(int position);
}