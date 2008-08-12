package de.jacavi.rcp.widgets;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.holongate.j2d.IPaintable;
import org.holongate.j2d.J2DCanvas;
import org.holongate.j2d.J2DRegistry;
import org.holongate.j2d.J2DSamplePaintable;
import org.holongate.j2d.J2DUtilities;

import de.jacavi.appl.track.Angle;
import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.TrackSection;
import de.jacavi.appl.track.Track.TrackLoadingException;
import de.jacavi.appl.track.Track.TrackModificationListener;



public class TrackWidget extends J2DCanvas implements IPaintable, TrackModificationListener {

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(TrackWidget.class);

    private Point panPosition = new Point(-300, -300);

    private boolean isCurrentlyPanning = false;

    private boolean isCurrentlyRotating = false;

    private Point panningStartPosition = null;

    private Track track = null;

    private BufferedImage trackImage = null;

    private double zoomLevel = 1.0;

    private Angle rotationAngle = new Angle(0);

    private Point rotationStartPosition = null;

    public TrackWidget(Composite parent, Track track) throws TrackLoadingException {
        super(parent, SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND, /*new J2DSamplePaintable("message")*/
        null);
        setPaintable(this);

        setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));

        Map<RenderingHints.Key, Object> hints = new HashMap<RenderingHints.Key, Object>(1);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        J2DRegistry.setHints(hints);

        // System.out.println("java.library.path = " + System.getProperty("java.library.path"));

        // check if we're accelerated
        String graphics2DFactory = getGraphics2DFactory().getClass().getName();
        logger.debug("Current Graphics2DFactory: " + graphics2DFactory);
        if(graphics2DFactory.equals("org.holongate.j2d.SWTGraphics2DFactory"))
            logger
                    .warn("No library for native graphics acceleration has been found! Drawing will be very slow. Please check your java.library.path!");

        setTrack(track);

        /*final Display display = Display.getCurrent();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    display.asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            ((J2DSamplePaintable) getPaintable()).angle++;
                            TrackWidget.this.repaint();
                        }
                    });
                    try {
                        Thread.sleep(10);
                    } catch(InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/

        // panPosition = new Point(-500 - this.getSize().x / 2, -500 - this.getSize().y / 2);
        /*addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                TrackWidget.this.paintControl(e);
            }
        });*/

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
                TrackWidget.this.handleDisposeEvent(e);
            }
        });
    }

    private void handleDisposeEvent(DisposeEvent e) {
        if(this.track != null)
            this.track.removeListener(this);
    }

    public Track getTrack() {
        return track;
    }

    /**
     * Change the widget to a new track.
     * <p>
     * The widget will register itself as a modification notification listener on the track and will automatically
     * update as soon as the track changes!
     * 
     * @param track
     *            the new to display or null to not show a track anymore
     */
    public void setTrack(Track track) {
        // unregister listener from a previously used track
        if(this.track != null)
            this.track.removeListener(this);

        if(track != null) {
            // change the current track
            this.track = track;

            // register listener on the new track
            this.track.addListener(this);

            // create a new track image
            createTrackImage();
        }

        // redraw the widget
        redraw();
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
            repaint();
    }

    protected void handleMouseDoubleClick(MouseEvent e) {
        System.out.println("Triggering repaint");
        ((J2DSamplePaintable) getPaintable()).angle++;
        this.repaint();
    }

    /*protected void paintControl(PaintEvent e) {
        // TODO: track can be null!

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
    }*/

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
        for(TrackSection s: track.getSections()) {
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

    @Override
    public void trackModified() {
        createTrackImage();
        redraw();
    }

    @Override
    public void paint(Control control, Graphics2D g2d) {
        // Point size = control.getSize();

        // add the rotation and zooming transformations
        g2d.rotate(rotationAngle.getRadians(), 500, 500);
        g2d.scale(zoomLevel, zoomLevel);

        // do the actual drawing of the widget
        g2d.drawImage(trackImage, panPosition.x, panPosition.y, null);
    }

    @Override
    public void redraw(Control control, GC gc) {}

    @Override
    public Rectangle2D getBounds(Control control) {
        return J2DUtilities.toRectangle2D(control.getBounds());
    }

}
