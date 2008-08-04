package de.jacavi.rcp.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.jacavi.appl.valueobjects.Track;
import de.jacavi.appl.valueobjects.TrackSection;



public class TrackWidget extends Canvas {

    private Point panPosition = new Point(200, 200);

    private boolean isPanningActive = false;

    private Point panningStartPosition = null;

    private Track currentTrack = null;

    public TrackWidget(Composite parent) {
        super(parent, SWT.DOUBLE_BUFFERED);

        final Color white = new Color(null, 255, 255, 255);
        setBackground(white);

        // HACK: hardcoded track for testing
        int index = 1;
        currentTrack = new Track();
        currentTrack.insertSection(new TrackSection("straight.png", new Point(53, 0), 0), index++);
        currentTrack.insertSection(new TrackSection("turn_90deg.png", new Point(0, -53), -90), index++);
        currentTrack.insertSection(new TrackSection("straight.png", new Point(0, -25), 0), index++);
        currentTrack.insertSection(new TrackSection("turn_90deg.png", new Point(-25 - 25, 0), -90), index++);
        currentTrack.insertSection(new TrackSection("straight.png", new Point(-53, 0), 0), index++);
        currentTrack.insertSection(new TrackSection("straight.png", new Point(-53, 0), 0), index++);
        currentTrack.insertSection(new TrackSection("straight.png", new Point(-53, 0), 0), index++);
        currentTrack.insertSection(new TrackSection("straight.png", new Point(-25, 0), 0), index++);
        currentTrack.insertSection(new TrackSection("turn_90deg.png", new Point(0, 25), -90), index++);
        currentTrack.insertSection(new TrackSection("straight.png", new Point(0, 53), 0), index++);
        currentTrack.insertSection(new TrackSection("turn_90deg.png", new Point(25, 0), -90), index++);
        currentTrack.insertSection(new TrackSection("straight.png", new Point(53, 0), 0), index++);

        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                TrackWidget.this.paintControl(e);
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                TrackWidget.this.handleMouseDoubleClick(e);
            }

            @Override
            public void mouseDown(MouseEvent e) {
                TrackWidget.this.handleMouseDown(e);
            }

            @Override
            public void mouseUp(MouseEvent e) {
                TrackWidget.this.handleMouseUp(e);
            }
        });

        addMouseMoveListener(new MouseMoveListener() {
            @Override
            public void mouseMove(MouseEvent e) {
                TrackWidget.this.handleMouseMove(e);
            }
        });

        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                white.dispose();
            }
        });
    }

    protected void handleMouseUp(MouseEvent e) {
        if(e.button == 3) {
            isPanningActive = false;
            panningStartPosition = null;
        }
    }

    protected void handleMouseDown(MouseEvent e) {
        if(e.button == 3 && !isPanningActive) {
            isPanningActive = true;
            panningStartPosition = new Point(e.x, e.y);
        }
    }

    protected void handleMouseMove(MouseEvent e) {
        if(isPanningActive) {
            panPosition.x += e.x - panningStartPosition.x;
            panPosition.y += e.y - panningStartPosition.y;
            panningStartPosition = new Point(e.x, e.y);
            redraw();
        }
    }

    protected void handleMouseDoubleClick(MouseEvent e) {
    // x += 5;
    // redraw();
    }

    protected void paintControl(PaintEvent e) {
        GC gc = e.gc;

        Point nextDrawingPosition = new Point(panPosition.x, panPosition.y);
        int currentAngle = 0;
        for(TrackSection s: currentTrack.getSections()) {
            gc.drawImage(s.getRotatedImage(currentAngle), nextDrawingPosition.x, nextDrawingPosition.y);
            currentAngle += s.getEntryToExitAngle();
            if(currentAngle < 0)
                currentAngle += 360;
            if(currentAngle >= 360)
                currentAngle -= 360;
            nextDrawingPosition.x += s.getNextSectionOffset().x;
            nextDrawingPosition.y += s.getNextSectionOffset().y;
        }

        /*
         * if(image != null) { gc.drawImage(image, panPosition.x, panPosition.y); }
         */

        // gc.drawString("text", x, 1);
    }
}
