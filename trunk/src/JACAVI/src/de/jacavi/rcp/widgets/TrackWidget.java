package de.jacavi.rcp.widgets;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

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

import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.TrackSection;
import de.jacavi.appl.track.Track.TrackLoadingException;
import de.jacavi.appl.valueobjects.Angle;
import de.jacavi.rcp.Activator;
import de.jacavi.rcp.util.Graphics2DRenderer;



public class TrackWidget extends Canvas {

    private Point panPosition = new Point(-300, -300);

    private boolean isCurrentlyPanning = false;

    private boolean isCurrentlyRotating = false;

    private Point panningStartPosition = null;

    private Track currentTrack = null;

    private BufferedImage trackImage = null;

    final Graphics2DRenderer renderer = new Graphics2DRenderer();

    private double zoomLevel = 1.0;

    private Angle rotationAngle = new Angle(0);

    private Point rotationStartPosition = null;

    public TrackWidget(Composite parent) throws TrackLoadingException {
        super(parent, SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND);

        final Color white = new Color(null, 255, 255, 255);
        // setBackground(white);

        // HACK: hardcoded track for testing
        currentTrack = new Track(Activator.getResourceAsStream("/tracks/demo_with30degturns.track.xml"));
        createTrackImage();

        // panPosition = new Point(-500 - this.getSize().x / 2, -500 - this.getSize().y / 2);

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
            isCurrentlyPanning = false;
            panningStartPosition = null;
        } else if(e.button == 2) {
            isCurrentlyRotating = false;
            rotationStartPosition = null;
        }
    }

    protected void handleMouseDown(MouseEvent e) {
        if(e.button == 3 && !isCurrentlyPanning) {
            isCurrentlyPanning = true;
            panningStartPosition = new Point(e.x, e.y);
        } else if(e.button == 2 && !isCurrentlyRotating) {
            isCurrentlyRotating = true;
            rotationStartPosition = new Point(e.x, e.y);
        }
    }

    protected void handleMouseMove(MouseEvent e) {
        boolean doesNeedRedraw = false;
        if(isCurrentlyPanning) {
            panPosition.x += e.x - panningStartPosition.x;
            panPosition.y += e.y - panningStartPosition.y;
            panningStartPosition = new Point(e.x, e.y);
            doesNeedRedraw = true;
        }
        if(isCurrentlyRotating) {
            rotationAngle.turn((e.x - rotationStartPosition.x) / 3);
            zoomLevel += -(e.y - rotationStartPosition.y) * 0.01;
            rotationStartPosition = new Point(e.x, e.y);
            doesNeedRedraw = true;
        }

        if(doesNeedRedraw)
            redraw();
    }

    protected void handleMouseDoubleClick(MouseEvent e) {}

    protected void paintControl(PaintEvent e) {
        // get the SWT graphics context from the event and prepare the Graphics2D renderer
        GC gc = e.gc;
        renderer.prepareRendering(gc);
        // Point controlSize = ((Control) e.getSource()).getSize();

        // gets the Graphics2D context and switch on the antialiasing
        Graphics2D g2d = renderer.getGraphics2D();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // add the rotation and zooming transformations
        g2d.rotate(rotationAngle.getRadians(), 500, 500);
        g2d.scale(zoomLevel, zoomLevel);

        // do the actual drawing of the widget
        g2d.drawImage(trackImage, panPosition.x, panPosition.y, null);

        // now that we are done with Java 2D, renders Graphics2D operation
        // on the SWT graphics context
        renderer.render(gc);
    }

    private void drawByCenter(Graphics2D g, java.awt.Image rotatedImage, Point destination) {
        Point drawingPosition = new Point(destination.x - rotatedImage.getWidth(null) / 2, destination.y
                - rotatedImage.getHeight(null) / 2);
        g.drawImage(rotatedImage, drawingPosition.x, drawingPosition.y, null);
    }

    private void markPoint(Graphics2D g, Point point, java.awt.Color color) {
        g.setColor(color);
        g.drawRect(point.x - 1, point.y - 1, 3, 3);
    }

    private void createTrackImage() {
        // FIXME: size should be dynamic or at least boundaries checked
        trackImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = trackImage.createGraphics();
        g.setBackground(java.awt.Color.WHITE);
        g.clearRect(0, 0, trackImage.getWidth(), trackImage.getHeight());

        Angle currentAngle = new Angle(0);
        Point currentTrackPos = new Point(trackImage.getWidth() / 2, trackImage.getHeight() / 2);
        for(TrackSection s: currentTrack.getSections()) {
            Double x, y;

            // System.out.println("Track position:          " + currentTrackPos.x + "/" + currentTrackPos.y);

            // step #1: rotate tile as necessary
            java.awt.Image rotatedImage = s.getRotatedImage(currentAngle);

            // step #2: calculate entry point relative to the center after rotation
            // <juckto> xnew = x*cos(0) - y*sin(0)
            // <juckto> ynew = x*sin(0) + y*cos(0)
            x = new Double(s.getEntryPoint().x * Math.cos(currentAngle.getRadians()) - s.getEntryPoint().y
                    * Math.sin(currentAngle.getRadians()));
            y = new Double(s.getEntryPoint().x * Math.sin(currentAngle.getRadians()) + s.getEntryPoint().y
                    * Math.cos(currentAngle.getRadians()));
            Point relativeEntryPoint = new Point(x.intValue(), y.intValue());
            // System.out.println("relativeEntryPoint:      " + relativeEntryPoint.x + "/" + relativeEntryPoint.y);

            // step #3: calculate center drawing point and draw the image
            Point centerDrawingPoint = new Point(currentTrackPos.x + (-relativeEntryPoint.x), currentTrackPos.y
                    + (-relativeEntryPoint.y));
            // System.out.println("Center drawing position: " + centerDrawingPoint.x + "/" + centerDrawingPoint.y);
            drawByCenter(g, rotatedImage, centerDrawingPoint);
            markPoint(g, centerDrawingPoint, java.awt.Color.GREEN);

            // step #4: calculate the new track position, by taking the rotated entry point and moving it to the exit
            // point, and calculate the new angle
            x = new Double(s.getExitPoint().x * Math.cos(currentAngle.getRadians()) - s.getExitPoint().y
                    * Math.sin(currentAngle.getRadians()));
            y = new Double(s.getExitPoint().x * Math.sin(currentAngle.getRadians()) + s.getExitPoint().y
                    * Math.cos(currentAngle.getRadians()));
            Point relativeExitPoint = new Point(x.intValue(), y.intValue());
            // System.out.println("relativeExitPoint:       " + relativeExitPoint.x + "/" + relativeExitPoint.y);
            markPoint(g, currentTrackPos, java.awt.Color.CYAN);
            currentTrackPos = new Point(currentTrackPos.x + (relativeExitPoint.x - relativeEntryPoint.x),
                    currentTrackPos.y + (relativeExitPoint.y - relativeEntryPoint.y));
            currentAngle.turn(s.getEntryToExitAngle());
            // System.out.println("New angle: " + currentAngle.angle);
        }
        markPoint(g, currentTrackPos, java.awt.Color.CYAN);

        // Display display = Display.getCurrent();
        // trackImage = new Image(display, convertToSWT(bi));
    }
}
