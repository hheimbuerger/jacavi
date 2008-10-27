/*
 * Copyright (C) Skillworks AG 2008. All Rights Reserved Confidential
 */
package de.jacavi.appl.track;

import java.awt.geom.Point2D;



/**
 * A helper class for storing a point together with an angle at the position of the point.
 */
public class DirectedPoint {
    public final Point2D point;

    public final Angle angle;

    public DirectedPoint(Point2D point, Angle angle) {
        this.point = point;
        this.angle = angle;
    }
}