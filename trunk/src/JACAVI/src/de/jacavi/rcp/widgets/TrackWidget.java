package de.jacavi.rcp.widgets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
import org.holongate.j2d.J2DUtilities;

import de.jacavi.appl.track.Angle;
import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.TrackSection;
import de.jacavi.appl.track.Slot.SlotPart;
import de.jacavi.appl.track.Track.TrackModificationListener;



/**
 * A widget that displays a track.
 * <p>
 * The view can be panned, zoomed and rotated. Tiles from the track can be selected.
 * <p>
 * The widgets automatically updates when the track changes.
 */
public class TrackWidget extends J2DCanvas implements IPaintable, TrackModificationListener {

    /** The background color of the widget. */
    private static final int BACKGROUND_COLOR = SWT.COLOR_WHITE;

    /** The button used to select a tile on the widget. */
    private static final int SELECTION_BUTTON = 1;

    /** The button used to rotate and zoom the view. */
    private static final int ROTATE_ZOOM_BUTTON = 2;

    /** The button used to pan (scroll) the view. */
    private static final int PAN_BUTTON = 3;

    /** The divisor used to slow down rotation. */
    private static final int ROTATION_SPEED_DIVISOR = 3;

    /** The factor used to slow down zooming. */
    private static final double ZOOM_SPEED_FACTOR = 0.01;

    /** Logger for this class */
    private static final Logger logger = Logger.getLogger(TrackWidget.class);

    /**
     * A helper class that represents a vector of double precision. (To be used with Point2D.Doubles.)
     * <p>
     * Supports adding vectors and creating translation transforms (and inverse translation transforms) corresponding to
     * the vector.
     */
    private class Vector2D {
        public Double deltax, deltay;

        /**
         * Creates a new vector that points from point a to point b.
         */
        public Vector2D(Point2D a, Point2D b) {
            deltax = b.getX() - a.getX();
            deltay = b.getY() - a.getY();
        }

        /**
         * Creates a new vector that points from the origin to point a.
         */
        public Vector2D(Point2D a) {
            deltax = a.getX();
            deltay = a.getY();
        }

        private Vector2D(double deltax, double deltay) {
            this.deltax = deltax;
            this.deltay = deltay;
        }

        @Override
        public String toString() {
            return "Vector2D[" + deltax + ", " + deltay + "]";
        }

        /**
         * Returns a new Vector2D that represents the vector sum of this vector and the given vector.
         */
        public Vector2D add(Vector2D a) {
            return new Vector2D(deltax + a.deltax, deltay + a.deltay);
        }

        /**
         * Returns an affine transform that translates by the vector.
         */
        public AffineTransform getTransform() {
            return AffineTransform.getTranslateInstance(deltax, deltay);
        }

        /**
         * Returns an affine transform that translates by the inverse vector.
         */
        public AffineTransform getInvertedTransform() {
            return AffineTransform.getTranslateInstance(-deltax, -deltay);
        }
    }

    /** The currently displayed track (or null if no track is being displayed). */
    private Track track = null;

    /** Indicates whether the user is currently holding the pan button down. */
    private boolean isCurrentlyPanning = false;

    /** Indicates whether the user is currently holding the rotation/zooming button down. */
    private boolean isCurrentlyRotatingOrZooming = false;

    /** The current viewport pan position. */
    private Point panPosition = new Point(0, 0);

    /** The current viewport zoom. */
    private double zoomLevel = 1.0;

    /** The current viewport rotation. */
    private Angle rotationAngle = new Angle(0);

    /** Used to determine the mouse movement between events during panning. */
    private Point panningStartPosition = null;

    /** Used to determine the mouse movement between events during rotation/zooming. */
    private Point rotationZoomingStartPosition = null;

    /** The currently selected tile, or -1 if no tile is selected */
    private int selectedTile = -1;

    /** The list of tile shapes of the last rendering (used to do hit detection). */
    private List<Shape> lastTileShapeList;

    /** The bounding box of the whole track of the last rendering (used to determine scrolling limits). */
    private Rectangle2D lastTrackBoundingBox;

    private Timer DEBUGanimationTimer;

    private int DEBUGanimationStep = 0;

    private class AnimationTimerHandler extends TimerTask {
        @Override
        public void run() {
            TrackWidget.this.DEBUGanimationStep++;
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    repaint();
                }
            });
        }
    }

    /**
     * Constructor
     * <p>
     * Creates a new widget for displaying a track inside the parent Composite, initially displaying the given track.
     * 
     * @param parent
     *            the parent SWT component to add this widget to
     * @param track
     *            the track to load initially (or null to load none)
     */
    public TrackWidget(Composite parent, Track track) {
        // call the J2DCanvas constructor, then register us as the IPaintable
        super(parent, SWT.NONE, null);
        setPaintable(this);

        // set the background color of the widget
        setBackground(Display.getCurrent().getSystemColor(BACKGROUND_COLOR));

        // configure the rendering hints (currently maximized on quality)
        Map<RenderingHints.Key, Object> hints = new HashMap<RenderingHints.Key, Object>(3);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        J2DRegistry.setHints(hints);

        // check if we're accelerated, log a warning if we're not
        String graphics2DFactoryClassName = getGraphics2DFactory().getClass().getName();
        logger.debug("Current Graphics2DFactory: " + graphics2DFactoryClassName);
        if(graphics2DFactoryClassName.equals("org.holongate.j2d.SWTGraphics2DFactory"))
            logger
                    .warn("No library for native graphics acceleration has been found! Drawing will be very slow. Please check your java.library.path!");

        // we're done with the initialization of the renderer and can initialize the track (this will also trigger the
        // first repaint)
        setTrack(track);

        // add various listeners, all just redirecting the calls to class methods
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

    /**
     * Returns the currently displayed track.
     */
    public Track getTrack() {
        return track;
    }

    /**
     * Change the widget to a new track.
     * <p>
     * The widget will register itself as a modification notification listener on the track and will automatically
     * update as soon as the track changes!
     * <p>
     * NOTE: This method may only be called from the GUI thread!
     * 
     * @param track
     *            the new track to display or null to not show a track anymore
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

            // DEBUG: start the animation timer
            if(DEBUGanimationTimer == null) {
                DEBUGanimationTimer = new Timer("DEBUG-animation");
                DEBUGanimationTimer.scheduleAtFixedRate(new AnimationTimerHandler(), 50, 50);
            }
        }

        // remove the current selection (will also trigger a repaint!)
        setSelectedTile(-1);
    }

    /**
     * Returns the index of the currently selected tile in the thread.
     * 
     * @return the index of the currently selected tile, or -1 if no there is no active selection
     */
    public int getSelectedTile() {
        return selectedTile;
    }

    /**
     * Changes the current selection to the tile with the given index.
     * <p>
     * Use -1 to remove the current selection. Specifying an invalid index will not raise an exception but simply remove
     * any selection.
     * <p>
     * NOTE: This method may only be called from the GUI thread!
     * 
     * @param selectedTile
     *            the index of the tile to select, or -1 to remove the selection
     */
    public void setSelectedTile(int selectedTile) {
        this.selectedTile = selectedTile;
        repaint();
    }

    /**
     * Handles the widget dispose event.
     * <p>
     * Unregisters the listener from the currently displayed track if there is one.
     */
    private void handleDisposeEvent(DisposeEvent e) {
        if(this.track != null)
            this.track.removeListener(this);
        if(this.DEBUGanimationTimer != null)
            this.DEBUGanimationTimer.cancel();
    }

    /**
     * Event handler for mouse up events.
     * <p>
     * Handles clicks (selections) and starts pans, zooms or rotations.
     */
    protected void handleMouseDown(MouseEvent e) {
        switch(e.button) {
            // if the selection button has been pressed down, do a hit detection and chance the current selection
            case SELECTION_BUTTON:
                boolean wasHitDetected = false;
                for(int i = lastTileShapeList.size() - 1; i >= 0; i--) {
                    if(lastTileShapeList.get(i).contains(e.x, e.y)) {
                        setSelectedTile(i);
                        wasHitDetected = true;
                        break;
                    }
                }
                // if no hit was detected, remove the selection
                if(!wasHitDetected)
                    setSelectedTile(-1);
                break;

            // if the pan button has been pressed down, start the panning
            case PAN_BUTTON:
                if(!isCurrentlyPanning) {
                    isCurrentlyPanning = true;
                    panningStartPosition = new Point(e.x, e.y);
                }
                break;

            // if the rotation/zoom button has been pressed down, start the rotation/zooming
            case ROTATE_ZOOM_BUTTON:
                if(!isCurrentlyRotatingOrZooming) {
                    isCurrentlyRotatingOrZooming = true;
                    rotationZoomingStartPosition = new Point(e.x, e.y);
                }
                break;
        }
    }

    /**
     * Event handler for mouse up events.
     * <p>
     * Deactivates any currently active pans, zooms or rotations.
     */
    protected void handleMouseUp(MouseEvent e) {
        switch(e.button) {
            case PAN_BUTTON:
                isCurrentlyPanning = false;
                panningStartPosition = null;
                break;
            case ROTATE_ZOOM_BUTTON:
                isCurrentlyRotatingOrZooming = false;
                rotationZoomingStartPosition = null;
                break;
        }
    }

    /**
     * Event handler for mouse move events.
     * <p>
     * Handles panning, zooming and rotating.
     */
    protected void handleMouseMove(MouseEvent e) {
        boolean doesRequireRepaint = false;

        // if panning is active, pan by the movement since the last event, reset the panning base point and trigger a
        // repaint
        if(isCurrentlyPanning) {
            panPosition.x += e.x - panningStartPosition.x;
            panPosition.y += e.y - panningStartPosition.y;
            panningStartPosition = new Point(e.x, e.y);
            doesRequireRepaint = true;
        }

        // if rotation/zooming is active, rotate/zoom by the movement since the last event, reset the base point and
        // trigger a repaint
        if(isCurrentlyRotatingOrZooming) {
            rotationAngle.turn((e.x - rotationZoomingStartPosition.x) / ROTATION_SPEED_DIVISOR);
            zoomLevel += -(e.y - rotationZoomingStartPosition.y) * ZOOM_SPEED_FACTOR;
            rotationZoomingStartPosition = new Point(e.x, e.y);
            doesRequireRepaint = true;
        }

        if(doesRequireRepaint)
            repaint();
    }

    /**
     * Event handler for mouse double click events.
     * <p>
     * (Currently unused.)
     */
    protected void handleMouseDoubleClick(MouseEvent e) {}

    /**
     * Event handler for modification notifications from the track.
     * <p>
     * Removes the current selection and triggers a repaint, everything else in handled while doing the rendering.
     */
    @Override
    public void handleTrackModified() {
        if(selectedTile != -1)
            if(track.getSections().size() > 0)
                setSelectedTile(Math.min(selectedTile, track.getSections().size() - 1));
            else
                setSelectedTile(-1);
        else
            repaint();
    }

    /**
     * Helper method for drawing a point on the screen. Mostly used for debugging purposes.
     */
    private void markPoint(Graphics2D g, Point2D currentTrackPos, Color color) {
        g.setColor(color);
        g.drawRect(new Double(currentTrackPos.getX() - 1).intValue(),
                new Double(currentTrackPos.getY() - 1).intValue(), 3, 3);
    }

    /**
     * Renders the currently displayed track.
     * <p>
     * This method assumes that the viewport transformations are already activated on the given Graphics2D object.
     * 
     * @param g
     *            the Graphics2D object to draw on
     * @param viewportTransformation
     *            the current viewport transformation
     */
    private void drawTrack(Graphics2D g, AffineTransform viewportTransformation) {
        // back up the current transformation of the graphics context, so we can restore it later on
        AffineTransform originalTransformation = g.getTransform();

        // active the viewport transformation
        g.setTransform(viewportTransformation);

        // initialise the drawing angle and track pos of the next tile
        Angle currentAngle = new Angle(0);
        Point2D currentTrackPos = new Point2D.Double(0.0, 0.0);

        // recreate the shape list (used for doing hit detections) and the track bounding box (used for determining the
        // scrolling bounds)
        lastTileShapeList = new ArrayList<Shape>();
        lastTrackBoundingBox = new Rectangle2D.Double();

        // initialize a counter (used for detected the selected tile)
        int counter = 0;

        // DEBUG: prepare drawing the 'cars'
        int slot1Length = track.getSlot1Length();
        int slot2Length = track.getSlot2Length();
        int slot1Pos = DEBUGanimationStep % slot1Length;
        int slot2Pos = DEBUGanimationStep % slot2Length;
        int[] car1Position = track.determineSectionFromPosition(true, slot1Pos);
        int[] car2Position = track.determineSectionFromPosition(false, slot2Pos);

        // iterate over all track sections of the currently displayed track
        for(TrackSection s: track.getSections()) {

            // create points for the entry and exit points (relative to the tile's center)
            Point2D relativeEntryPoint = new Point2D.Double(s.getEntryPoint().x, s.getEntryPoint().y);
            Point2D relativeExitPoint = new Point2D.Double(s.getExitPoint().x, s.getExitPoint().y);

            // determine the entry and exit points after applying the current rotation (required for the next step)
            AffineTransform rotationTransformation = AffineTransform.getRotateInstance(currentAngle.getRadians());
            AffineTransformOp rotationOperation = new AffineTransformOp(rotationTransformation,
                    AffineTransformOp.TYPE_BICUBIC);
            Point2D rotatedRelativeEntryPoint = rotationOperation.getPoint2D(relativeEntryPoint, null);
            Point2D rotatedRelativeExitPoint = rotationOperation.getPoint2D(relativeExitPoint, null);

            // determine two vectors:
            // a) imageBaseToEntryPointVector: a vector pointing from the upper left corner of the image to the entry
            // point after applying the rotation (used for placing the tile at the correct position)
            // b) entryToExitPointVector: a vector pointing from the entry to the exit point after applying the rotation
            // (used to determine the exit point of this tile and therefore the entry point of the next one)
            Vector2D centerToEntryPointVector = new Vector2D(relativeEntryPoint);
            Vector2D imageBaseToCenterVector = new Vector2D(new Point2D.Double(s.getImage().getWidth() / 2, s
                    .getImage().getHeight() / 2));
            Vector2D imageBaseToEntryPointVector = imageBaseToCenterVector.add(centerToEntryPointVector);
            Vector2D entryToExitPointVector = new Vector2D(rotatedRelativeEntryPoint, rotatedRelativeExitPoint);

            // create a transformation to be used for placing the image -- the transformation includes the rotation, the
            // translation to the entry point and the translation from the entry to the exit point
            AffineTransform imagePlacementTransformation = new AffineTransform();
            imagePlacementTransformation.translate(currentTrackPos.getX(), currentTrackPos.getY());
            imagePlacementTransformation.rotate(currentAngle.getRadians());
            imagePlacementTransformation.concatenate(imageBaseToEntryPointVector.getInvertedTransform());
            AffineTransformOp imagePlacementOperation = new AffineTransformOp(imagePlacementTransformation,
                    AffineTransformOp.TYPE_BICUBIC);

            // now we can draw the image -- the placement is already included in the transformation operation, so the
            // coordinates used here are simple the origin coordinates
            g.drawImage(s.getImage(), imagePlacementOperation, 0, 0);

            // DEBUG: draw the slots on top
            AffineTransform slotPlacementTransformation = new AffineTransform();
            slotPlacementTransformation.translate(currentTrackPos.getX(), currentTrackPos.getY());
            slotPlacementTransformation.rotate(currentAngle.getRadians());
            slotPlacementTransformation.concatenate(centerToEntryPointVector.getInvertedTransform());
            g.setColor(Color.YELLOW);
            for(SlotPart sp: s.getSlot1().getSlotParts())
                g.draw(slotPlacementTransformation.createTransformedShape(sp.getShape()));
            g.setColor(Color.BLUE);
            for(SlotPart sp: s.getSlot2().getSlotParts())
                g.draw(slotPlacementTransformation.createTransformedShape(sp.getShape()));

            // DEBUG: draw the current track position
            markPoint(g, currentTrackPos, Color.GREEN);

            // DEBUG: draw the current 'car' position
            if(counter == car1Position[0]) {
                SlotPart slotPart = s.getSlot1().getSlotParts().get(0);
                markPoint(g, slotPlacementTransformation.transform(slotPart.getStep(car1Position[1]), null), Color.RED);
            }
            if(counter == car2Position[0]) {
                SlotPart slotPart = s.getSlot2().getSlotParts().get(0);
                markPoint(g, slotPlacementTransformation.transform(slotPart.getStep(car2Position[1]), null), Color.RED);
            }

            // union this image's bounding box (rectangular and parallel to the viewport!) with the complete track
            // bounding box -- that way we'll get a bounding box for the whole track in the end
            Rectangle2D finalImageBoundingBox = imagePlacementOperation.getBounds2D(s.getImage());
            Rectangle2D.union(lastTrackBoundingBox, finalImageBoundingBox, lastTrackBoundingBox);

            // create a shape of the exact box of the image (not parallel to the viewport!) by transforming a rectangle
            // corresponding to the image with the viewport transformation
            Rectangle2D r = new Rectangle2D.Double(0.0, 0.0, s.getImage().getWidth(), s.getImage().getHeight());
            Shape tileShape = imagePlacementTransformation.createTransformedShape(r);

            // store that shape, we'll need it later on to do hit detection
            lastTileShapeList.add(viewportTransformation.createTransformedShape(tileShape));

            // if the current image is selected, draw its shape to indicate the selection to the user
            g.setColor(Color.YELLOW);
            if(counter++ == selectedTile)
                g.draw(tileShape);

            // calculate the new track position by taking current track position and applying the
            // entryToExitPointTransformation
            AffineTransform entryToExitPointTransformation = entryToExitPointVector.getTransform();
            currentTrackPos = entryToExitPointTransformation.transform(currentTrackPos, null);

            // calculate the new angle by turning it by the angle this tile is supposedly changing the track direction
            currentAngle.turn(s.getEntryToExitAngle());
        }

        // DEBUG: draw the track bounding box in red
        g.setColor(Color.RED);
        g.draw(lastTrackBoundingBox);

        // restore the old transformation
        g.setTransform(originalTransformation);
    }

    /*private void drawScrollers(Graphics2D g2d, Point size) {
        GeneralPath scroller = new GeneralPath();

        scroller.moveTo(15, 30);
        scroller.lineTo(15, size.y - 30);
        scroller.lineTo(15 + 3, size.y - 30 - 3);
        scroller.lineTo(15 + 3, 33);
        scroller.lineTo(15, 30);

        g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.draw(scroller);
        g2d.setPaint(Color.LIGHT_GRAY);
        g2d.fill(scroller);
    }*/

    /*private int factorial(int x) {
        if(x <= 1)
            return 1;
        else {
            int result = 1;
            for(int i = 2; i <= x; i++)
                result *= i;
            return result;
        }
    }

    private int binomialCoefficient(int n, int k) {
        return factorial(n) / (factorial(k) * factorial(n - k));
    }

    private double[] computeBezierPoint(double[][] referencePoints, double t) {
        double c[] = new double[2];
        int n = referencePoints.length - 1;

        for(int dim = 0; dim < 2; dim++)
            for(int i = 0; i <= n; i++)
                c[dim] += binomialCoefficient(n, i) * Math.pow(t, i) * Math.pow(1 - t, n - i) * referencePoints[i][dim];
        return c;
    }

    private void drawBezierCurve(Graphics2D g, Point startPoint) {
        int steps = 10;

        double[][] referencePoints = new double[3][];
        referencePoints[0] = new double[] { 0.0, 0.0 };
        referencePoints[1] = new double[] { 50.0, -100.0 };
        referencePoints[2] = new double[] { 100.0, 0.0 };

        for(int i = 0; i <= steps; i++) {
            double point[] = computeBezierPoint(referencePoints, (1.0 * i) / steps);
            markPoint(g, new Point2D.Double(point[0], point[1]), Color.BLUE);
        }
    }*/

    /**
     * Invoked by base class methods to trigger an actual repaint. Not to be called directly!
     */
    @Override
    public void paint(Control control, Graphics2D g2d) {
        Point size = control.getSize();

        // update the current viewpoint transformation
        AffineTransform viewportTransformation = new AffineTransform();
        viewportTransformation.translate(size.x / 2, size.y / 2);
        viewportTransformation.translate(panPosition.x, panPosition.y);
        viewportTransformation.scale(zoomLevel, zoomLevel);
        viewportTransformation.rotate(rotationAngle.getRadians(), 0, 0);

        // back up the current transformation, apply the viewport translation and draw the track
        drawTrack(g2d, viewportTransformation);

        /*g2d.setTransform(AffineTransform.getTranslateInstance(50, 150));
        g2d.setColor(Color.YELLOW);
        g2d.draw(new QuadCurve2D.Double(0, 0, 50, -100, 100, 0));
        drawBezierCurve(g2d, new Point(0, 200));*/

        // drawScrollers(g2d, size);
        // g2d.setTransform(null);
        // do the actual drawing of the widget
        // g2d.drawImage(trackImage, panPosition.x, panPosition.y, null);
    }

    /**
     * Invoked by base class methods to do SWT specific redrawing after the Java2D based redraw has finished. Not to be
     * called directly!
     */
    @Override
    public void redraw(Control control, GC gc) {}

    /**
     * Invoked by base class methods to determine the size of the control. Not to be called directly!
     */
    @Override
    public Rectangle2D getBounds(Control control) {
        return J2DUtilities.toRectangle2D(control.getBounds());
    }

}
