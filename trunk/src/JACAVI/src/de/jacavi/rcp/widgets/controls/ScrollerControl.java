/*
 * Copyright (C) Skillworks AG 2008. All Rights Reserved Confidential
 */
package de.jacavi.rcp.widgets.controls;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

import org.eclipse.swt.graphics.Point;



public class ScrollerControl extends InnerControl {
    final static int MARGIN = 10; // distance from the edge to the scroller (x for vertical, y for horizontal)

    final static int OFFSET_X = 150; // distance from the corner to the scroller

    final static int OFFSET_Y = 50; // distance from the corner to the scroller

    final static int THICKNESS = 4; // thickness of the actual scroller

    public enum ScrollerPosition {
        NORTH, EAST, WEST, SOUTH
    };

    private ScrollerControl.ScrollerPosition position;

    public ScrollerControl(ScrollerPosition position) {
        this.position = position;
    }

    @Override
    public void draw(Graphics2D g2d, boolean isHoveredOver) {
        g2d.setPaint(isHoveredOver ? new Color(0, 200, 0) : Color.LIGHT_GRAY);
        g2d.fill(shape);
        g2d.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(isHoveredOver ? new Color(0, 160, 0) : Color.LIGHT_GRAY);
        g2d.draw(shape);
    }

    @Override
    public void reposition(Point size) {
        GeneralPath scroller = new GeneralPath();

        switch(position) {
            case NORTH:
                scroller.moveTo(OFFSET_X, MARGIN + THICKNESS); // outer, left
                scroller.lineTo(size.x - OFFSET_X, MARGIN + THICKNESS); // outer, right
                scroller.lineTo(size.x - OFFSET_X - THICKNESS, MARGIN); // inner, right
                scroller.lineTo(OFFSET_X + THICKNESS, MARGIN); // inner, left
                break;
            case EAST:
                scroller.moveTo(size.x - MARGIN - THICKNESS, OFFSET_Y); // outer, up
                scroller.lineTo(size.x - MARGIN - THICKNESS, size.y - OFFSET_Y); // outer, down
                scroller.lineTo(size.x - MARGIN, size.y - OFFSET_Y - THICKNESS); // inner, down
                scroller.lineTo(size.x - MARGIN, OFFSET_Y + THICKNESS); // inner, up
                break;
            case SOUTH:
                scroller.moveTo(OFFSET_X, size.y - MARGIN - THICKNESS); // outer, left
                scroller.lineTo(size.x - OFFSET_X, size.y - MARGIN - THICKNESS); // outer, right
                scroller.lineTo(size.x - OFFSET_X - THICKNESS, size.y - MARGIN); // inner, right
                scroller.lineTo(OFFSET_X + THICKNESS, size.y - MARGIN); // inner, left
                break;
            case WEST:
                scroller.moveTo(MARGIN + THICKNESS, OFFSET_Y); // outer, up
                scroller.lineTo(MARGIN + THICKNESS, size.y - OFFSET_Y); // outer, down
                scroller.lineTo(MARGIN, size.y - OFFSET_Y - THICKNESS); // inner, down
                scroller.lineTo(MARGIN, OFFSET_Y + THICKNESS); // inner, up
                break;
        }
        scroller.closePath();

        shape = scroller;
    }
}