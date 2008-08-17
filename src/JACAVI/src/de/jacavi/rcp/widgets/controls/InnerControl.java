/*
 * Copyright (C) Skillworks AG 2008. All Rights Reserved Confidential
 */
package de.jacavi.rcp.widgets.controls;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;

import org.eclipse.swt.graphics.Point;



public abstract class InnerControl {
    protected Shape shape;

    public boolean doHitDetection(Point2D.Double mousePosition) {
        return shape.contains(mousePosition);
    }

    abstract public void draw(Graphics2D g2d, boolean isHoveredOver);

    abstract public void reposition(Point size);
}