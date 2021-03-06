package de.jacavi.rcp.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.holongate.j2d.IPaintable;
import org.holongate.j2d.J2DCanvas;
import org.holongate.j2d.J2DRegistry;
import org.holongate.j2d.J2DUtilities;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.car.Car;
import de.jacavi.appl.controller.CarController;
import de.jacavi.appl.controller.ControllerSignal;
import de.jacavi.appl.racelogic.Player;
import de.jacavi.appl.track.Angle;
import de.jacavi.appl.track.CarScreenPosition;
import de.jacavi.appl.track.Checkpoint;
import de.jacavi.appl.track.LaneSection;
import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.TrackSection;
import de.jacavi.appl.track.Track.TrackModificationListener;
import de.jacavi.rcp.Activator;
import de.jacavi.rcp.preferences.MainPreferencePage;
import de.jacavi.rcp.widgets.controls.ImageButtonControl;
import de.jacavi.rcp.widgets.controls.InnerControl;
import de.jacavi.rcp.widgets.controls.ScrollerControl;



/**
 * A widget that displays a track.
 * <p>
 * The view can be panned, zoomed and rotated. Tiles from the track can be selected.
 * <p>
 * The widgets automatically updates when the track changes.
 */
public class TrackWidget extends J2DCanvas implements IPaintable, TrackModificationListener {

    /**
     * Used to define the mode the widget is operating in during construction.
     * <p>
     * The major differences are:
     * <ul>
     * <li>RACE_MODE:
     * <ul>
     * <li>shows thrust gauges
     * <li>shows cars
     * <li>redraws triggered by {@link RaceEngine}
     * </ul>
     * <li>DESIGN_MODE:
     * <ul>
     * <li>allows selecting track sections
     * <li>redraws triggered by events or by internal thread when inner control is clicked and held
     * </ul>
     * </ul>
     */
    public enum TrackWidgetMode {
        RACE_MODE, DESIGN_MODE
    }

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

    /** The minimum zoom level ("zoomed all out"). */
    private static final double ZOOM_MIN = 0.5;

    /** The adjustment to the zoom level taken for every click on the control. */
    private static final double ZOOM_STEPS = 0.05;

    /** The maximum zoom level ("zoomed all in"). */
    private static final double ZOOM_MAX = 5.0;

    private static final String ICON_ROTATION_CLOCKWISE = "/images/widget_buttons/rotate_clockwise_16x16.png";

    private static final String ICON_ROTATION_COUNTER_CLOCKWISE = "/images/widget_buttons/rotate_anticlockwise_16x16.png";

    private static final String ICON_ROTATION_RESET = "/images/widget_buttons/reset_16x16.png";

    private static final String ICON_ZOOM_IN = "/images/widget_buttons/zoom_in_16x16.png";

    private static final String ICON_ZOOM_OUT = "/images/widget_buttons/zoom_out_16x16.png";

    private static final String ICON_ZOOM_RESET = "/images/widget_buttons/reset_16x16.png";

    /** Logger for this class */
    private static final Logger logger = Logger.getLogger(TrackWidget.class);

    /**
     * A helper class that represents a vector of double precision. (To be used with Point2D.Doubles.)
     * <p>
     * Supports adding vectors and creating translation transforms (and inverse translation transforms) corresponding to
     * the vector.
     */
    private static class Vector2D {
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
    private final Point panPosition = new Point(0, 0);

    /** The current viewport zoom. */
    private double zoomLevel = 1.0;

    /** The current viewport rotation. */
    private final Angle rotationAngle = new Angle(0);

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

    private final Font widgetFont;

    private static enum InnerControlID {
        NONE, SCROLLER_TOP, SCROLLER_RIGHT, SCROLLER_BOTTOM, SCROLLER_LEFT, ROTATION_CLOCKWISE, ROTATION_COUNTER_CLOCKWISE, ROTATION_RESET, ZOOM_IN, ZOOM_OUT, ZOOM_RESET
    }

    private InnerControlID hoveredInnerControl = InnerControlID.NONE;

    private final Map<InnerControlID, InnerControl> innerControls = new HashMap<InnerControlID, InnerControl>();

    private Timer clickEventRepetitionTimer;

    private boolean isMouseOnWidget = false;

    private Date lastFrameCounterUpdate = new Date();

    private int lastFrameCount = 0;

    private int frameCounter = 0;

    private final TrackWidgetMode widgetMode;

    private List<Player> playersBean;

    private BufferedImage cachedTrackImage;

    private Map<TrackSection, AffineTransformOp> imagePlacementOperations;

    private Map<TrackSection, AffineTransform> lanePlacementTransformations;

    private Map<TrackSection, Integer> carDrawingAngles;

    private boolean isDrawing = false;

    private int droppedFrameCounter;

    private int lastDroppedFrameCount;

    private AffineTransform viewportTransformation;

    private final IPropertyChangeListener preferenceChangeListener;

    private class ClickEventRepetitionHandler extends TimerTask {
        private final InnerControlID heldControl;

        public ClickEventRepetitionHandler(InnerControlID heldControl) {
            this.heldControl = heldControl;
        }

        @Override
        public void run() {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    if(!TrackWidget.this.isDisposed() && (TrackWidget.this.hoveredInnerControl == heldControl)) {
                        TrackWidget.this.handleInnerControlClick(heldControl);
                    }
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
     * @param widgetMode
     *            if MODE_RACE, the track widget is initialized in race mode and shows only race mode elements, if
     *            MODE_DESIGN, the track widget is initialized in design mode and shows the design mode elements
     * @throws IOException
     *             if one of the bitmaps can't be loaded
     */
    @SuppressWarnings("unchecked")
    public TrackWidget(Composite parent, Track track, TrackWidgetMode widgetMode) throws IOException {
        // call the J2DCanvas constructor, then register us as the IPaintable
        super(parent, SWT.NONE, null);
        setPaintable(this);
        this.widgetMode = widgetMode;

        // set the background color of the widget
        setBackground(Display.getCurrent().getSystemColor(BACKGROUND_COLOR));

        // configure the rendering hints (currently maximized on quality)
        updateRenderingHints();

        // check if we're accelerated, log a warning if we're not
        String graphics2DFactoryClassName = getGraphics2DFactory().getClass().getName();
        logger.debug("Current Graphics2DFactory: " + graphics2DFactoryClassName);
        if(graphics2DFactoryClassName.equals("org.holongate.j2d.SWTGraphics2DFactory")) {
            logger
                    .warn("No library for native graphics acceleration has been found! Drawing will be very slow. Please check your java.library.path!");
        }

        // get the players bean needed to display the cars if we're in race mode
        if(widgetMode == TrackWidgetMode.RACE_MODE) {
            playersBean = (List<Player>) ContextLoader.getBean("playersBean");
        }

        // prepare the font
        widgetFont = new Font("Arial", Font.BOLD, 10);

        // initialize the inner controls
        initializeInnerControls();

        // we're done with the initialization of the renderer and can initialize the track (this will also trigger the
        // first repaint)
        setTrack(track);

        // register to be informed about preference changes
        preferenceChangeListener = new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                TrackWidget.this.preferencesChanged(e);
            }
        };
        Activator.getStore().addPropertyChangeListener(preferenceChangeListener);

        // add various listeners, all just redirecting the calls to class methods
        addMouseListener(new MouseAdapter() {
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

        addMouseTrackListener(new MouseTrackAdapter() {
            @Override
            public void mouseEnter(MouseEvent e) {
                TrackWidget.this.handleMouseEnter(e);
            }

            @Override
            public void mouseExit(MouseEvent e) {
                TrackWidget.this.handleMouseExit(e);
            }
        });

        addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                TrackWidget.this.handleControlResizedEvent(e);
            }
        });

        addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                TrackWidget.this.handleDisposeEvent(e);
            }
        });
    }

    private void updateRenderingHints() {
        Map<RenderingHints.Key, Object> hints = new HashMap<RenderingHints.Key, Object>(3);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        int viewportRenderingQuality = Activator.getStore().getInt(MainPreferencePage.PREF_VIEWPORT_QUALITY);
        if(viewportRenderingQuality == AffineTransformOp.TYPE_BICUBIC) {
            hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        } else if(viewportRenderingQuality == AffineTransformOp.TYPE_BILINEAR) {
            hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        } else if(viewportRenderingQuality == AffineTransformOp.TYPE_NEAREST_NEIGHBOR) {
            hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        }
        J2DRegistry.setHints(hints);
    }

    private void initializeInnerControls() throws IOException {
        // create the controls
        innerControls.put(InnerControlID.SCROLLER_TOP, new ScrollerControl(ScrollerControl.ScrollerPosition.NORTH));
        innerControls.put(InnerControlID.SCROLLER_RIGHT, new ScrollerControl(ScrollerControl.ScrollerPosition.EAST));
        innerControls.put(InnerControlID.SCROLLER_BOTTOM, new ScrollerControl(ScrollerControl.ScrollerPosition.SOUTH));
        innerControls.put(InnerControlID.SCROLLER_LEFT, new ScrollerControl(ScrollerControl.ScrollerPosition.WEST));
        innerControls.put(InnerControlID.ROTATION_COUNTER_CLOCKWISE, new ImageButtonControl(
                ICON_ROTATION_COUNTER_CLOCKWISE, 2, true));
        innerControls.put(InnerControlID.ROTATION_RESET, new ImageButtonControl(ICON_ROTATION_RESET, 1, true));
        innerControls.put(InnerControlID.ROTATION_CLOCKWISE, new ImageButtonControl(ICON_ROTATION_CLOCKWISE, 0, true));
        innerControls.put(InnerControlID.ZOOM_IN, new ImageButtonControl(ICON_ZOOM_IN, 2, false));
        innerControls.put(InnerControlID.ZOOM_RESET, new ImageButtonControl(ICON_ZOOM_RESET, 1, false));
        innerControls.put(InnerControlID.ZOOM_OUT, new ImageButtonControl(ICON_ZOOM_OUT, 0, false));
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
        if(this.track != null) {
            this.track.removeListener(this);
        }

        if(track != null) {
            // change the current track
            this.track = track;

            // register listener on the new track
            this.track.addListener(this);
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
        clearCache();
        repaint();
    }

    /**
     * Handles the widget dispose event.
     * <p>
     * Unregisters the listener from the currently displayed track if there is one.
     */
    private void handleDisposeEvent(DisposeEvent e) {
        if(track != null) {
            track.removeListener(this);
        }
        if(clickEventRepetitionTimer != null) {
            clickEventRepetitionTimer.cancel();
        }
        if(preferenceChangeListener != null) {
            Activator.getStore().removePropertyChangeListener(preferenceChangeListener);
        }
    }

    /**
     * Triggers a redraw when the user changes the preferences.
     */
    private void preferencesChanged(PropertyChangeEvent e) {
        if(!isDisposed()) {
            // if the viewport quality was changed, the rendering hints have to be updated
            if(e.getProperty().equals(MainPreferencePage.PREF_VIEWPORT_QUALITY)) {
                updateRenderingHints();
            }

            // generally , we have to clear the cache and rerender
            clearCache();
            repaint();
        }
    }

    /**
     * Event handler for mouse up events.
     * <p>
     * Handles clicks (selections) and starts pans, zooms or rotations.
     */
    protected void handleMouseDown(MouseEvent e) {
        switch(e.button) {
            // if the selection button has been pressed down, do a hit detection and change the current selection
            case SELECTION_BUTTON:
                boolean wasHitDetected = false;
                // if a control is active, process the hit on that
                if(hoveredInnerControl != null) {
                    handleInnerControlClick(hoveredInnerControl);
                    clickEventRepetitionTimer = new Timer("clickEventRepetition");
                    clickEventRepetitionTimer.schedule(new ClickEventRepetitionHandler(hoveredInnerControl), 50, 50);
                    wasHitDetected = true;
                }
                // if that isn't the case, check if there's a hit on any of the track sections
                else if(widgetMode == TrackWidgetMode.DESIGN_MODE) {
                    for(int i = lastTileShapeList.size() - 1; i >= 0; i--) {
                        if(viewportTransformation.createTransformedShape(lastTileShapeList.get(i)).contains(e.x, e.y)) {
                            setSelectedTile(i);
                            wasHitDetected = true;
                            break;
                        }
                    }
                }
                // if no hit was detected, remove the selection
                if(!wasHitDetected) {
                    setSelectedTile(-1);
                }
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

    private void handleInnerControlClick(InnerControlID control) {
        switch(control) {
            case SCROLLER_TOP:
                adjustPanPositionBounded(0, +10);
                break;
            case SCROLLER_RIGHT:
                adjustPanPositionBounded(-10, 0);
                break;
            case SCROLLER_BOTTOM:
                adjustPanPositionBounded(0, -10);
                break;
            case SCROLLER_LEFT:
                adjustPanPositionBounded(+10, 0);
                break;
            case ROTATION_CLOCKWISE:
                this.rotationAngle.turn(2);
                break;
            case ROTATION_COUNTER_CLOCKWISE:
                this.rotationAngle.turn(-2);
                break;
            case ROTATION_RESET:
                rotationAngle.set(0);
                break;
            case ZOOM_IN:
                adjustZoomLevelBounded(+ZOOM_STEPS);
                break;
            case ZOOM_OUT:
                adjustZoomLevelBounded(-ZOOM_STEPS);
                break;
            case ZOOM_RESET:
                zoomLevel = 1.0;
                break;
            default:
                // throw new RuntimeException("Event handler missing for control " + control.toString() +
                // " in TrackWidget.handleInnerControlClick()");
                logger
                        .warn("Event handler missing for control "
                                + control.toString()
                                + " in TrackWidget.handleInnerControlClick(). This should never occur and probably indicates a race condition.");
        }

        // trigger a repaint
        updateViewportTransformation();
        repaint();
    }

    private void updateViewportTransformation() {
        Point size = getSize();

        // update the current viewpoint transformation
        viewportTransformation = new AffineTransform();
        viewportTransformation.translate(size.x / 2, size.y / 2);
        viewportTransformation.translate(panPosition.x, panPosition.y);
        viewportTransformation.scale(zoomLevel, zoomLevel);
        viewportTransformation.rotate(rotationAngle.getRadians(), 0, 0);
    }

    /**
     * Event handler for mouse up events.
     * <p>
     * Deactivates any currently active pans, zooms or rotations.
     */
    protected void handleMouseUp(MouseEvent e) {
        switch(e.button) {
            case SELECTION_BUTTON:
                if(clickEventRepetitionTimer != null) {
                    clickEventRepetitionTimer.cancel();
                    clickEventRepetitionTimer = null;
                }
                break;
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
            adjustPanPositionBounded(e.x - panningStartPosition.x, e.y - panningStartPosition.y);
            panningStartPosition = new Point(e.x, e.y);
            doesRequireRepaint = true;
        }

        // if rotation/zooming is active, rotate/zoom by the movement since the last event, reset the base point and
        // trigger a repaint
        if(isCurrentlyRotatingOrZooming) {
            rotationAngle.turn((e.x - rotationZoomingStartPosition.x) / ROTATION_SPEED_DIVISOR);
            adjustZoomLevelBounded(-(e.y - rotationZoomingStartPosition.y) * ZOOM_SPEED_FACTOR);
            rotationZoomingStartPosition = new Point(e.x, e.y);
            doesRequireRepaint = true;
        }

        // do hit testing on the inner controls
        if(hitTestInnerControls(e)) {
            // the current control hovering has changed!
            doesRequireRepaint = true;
            if(clickEventRepetitionTimer != null) {
                clickEventRepetitionTimer.cancel();
                clickEventRepetitionTimer = null;
            }
        }

        if(doesRequireRepaint) {
            updateViewportTransformation();
            repaint();
        }
    }

    protected void handleControlResizedEvent(ControlEvent e) {
        // tell all the inner controls to reposition themselves
        for(InnerControl c: innerControls.values()) {
            c.reposition(getSize());
        }
    }

    /**
     * Performs a hit test on the inner controls and updates the hoveredInnerControl field.
     * 
     * @return true, if the control the mouse cursor is hovering over changed, false otherwise
     */
    private boolean hitTestInnerControls(MouseEvent e) {
        InnerControlID previouslyHoveredInnerControl = hoveredInnerControl;
        Point2D.Double p = new Point2D.Double(e.x, e.y);

        hoveredInnerControl = null;
        for(InnerControlID id: innerControls.keySet()) {
            if(innerControls.get(id).doHitDetection(p)) {
                hoveredInnerControl = id;
                break;
            }
        }

        return hoveredInnerControl != previouslyHoveredInnerControl;
    }

    protected void handleMouseEnter(MouseEvent e) {
        isMouseOnWidget = true;
    }

    protected void handleMouseExit(MouseEvent e) {
        isMouseOnWidget = false;
        repaint();
    }

    /**
     * Event handler for modification notifications from the track.
     * <p>
     * Removes the current selection and triggers a repaint, everything else in handled while doing the rendering.
     */
    @Override
    public void handleTrackModified() {
        if(selectedTile != -1) {
            if(track.getSections().size() > 0) {
                setSelectedTile(Math.min(selectedTile, track.getSections().size() - 1));
            } else {
                setSelectedTile(-1);
            }
        } else {
            clearCache();
            repaint();
        }
    }

    /**
     * Adjusts the pan position by the given amount while restricting it to certain bounds.
     * <p>
     * The bounds are currently set so that 10% of the track have to be visible in both dimensions.
     * 
     * @param xAdjustment
     *            the adjustment in pixels (negative values mean to the left, positive to the right)
     * @param yAdjustment
     *            the adjustment in pixels (negative values mean to up, positive mean down)
     */
    private void adjustPanPositionBounded(int xAdjustment, int yAdjustment) {
        /*int upperBound = (int) (-lastTrackBoundingBox.getMinY() + panPosition.y - lastTrackBoundingBox.getHeight() * 0.9);
        int rightBound = (int) (-lastTrackBoundingBox.getMaxX() + panPosition.x + getSize().x + lastTrackBoundingBox
                .getWidth() * 0.9);
        int lowerBound = (int) (-lastTrackBoundingBox.getMaxY() + panPosition.y + getSize().y + lastTrackBoundingBox
                .getHeight() * 0.9);
        int leftBound = (int) (-lastTrackBoundingBox.getMinX() + panPosition.x - lastTrackBoundingBox.getWidth() * 0.9);
        panPosition.x = Math.max(Math.min(panPosition.x + xAdjustment, rightBound), leftBound);
        panPosition.y = Math.max(Math.min(panPosition.y + yAdjustment, lowerBound), upperBound);*/
        panPosition.x += xAdjustment;
        panPosition.y += yAdjustment;
    }

    /**
     * Adjusts the zoom level by the given amount while restricting it to the static bounds.
     * 
     * @param adjustment
     *            the amount of adjustment (a negative value indicates zooming out, a positive value indicates zooming
     *            in)
     */
    private void adjustZoomLevelBounded(double adjustment) {
        zoomLevel = Math.max(Math.min(zoomLevel + adjustment, ZOOM_MAX), ZOOM_MIN);
    }

    private void clearCache() {
        cachedTrackImage = null;
        /*cachedTrackImageTransform = null;
        isTrackBoundingBoxDirty = true;
        lanePlacementTransformations = null;
        carDrawingAngles = null;*/
    }

    private void doCalculations() {
        // recreate the shape list (used for doing hit detections) and the track bounding box (used for determining the
        // scrolling bounds)
        lastTileShapeList = new ArrayList<Shape>();
        lastTrackBoundingBox = null;

        // initialise the drawing angle and track pos of the next tile
        Angle currentAngle = new Angle(0);
        Point2D currentTrackPos = new Point2D.Double(0.0, 0.0);

        imagePlacementOperations = new HashMap<TrackSection, AffineTransformOp>();
        lanePlacementTransformations = new HashMap<TrackSection, AffineTransform>();
        carDrawingAngles = new HashMap<TrackSection, Integer>();

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
            // a) imageBaseToEntryPointVector: a vector pointing from the upper left corner of the image to the
            // entry
            // point after applying the rotation (used for placing the tile at the correct position)
            // b) entryToExitPointVector: a vector pointing from the entry to the exit point after applying the
            // rotation
            // (used to determine the exit point of this tile and therefore the entry point of the next one)
            Vector2D centerToEntryPointVector = new Vector2D(relativeEntryPoint);
            Vector2D imageBaseToCenterVector = new Vector2D(new Point2D.Double(s.getImage().getWidth() / 2, s
                    .getImage().getHeight() / 2));
            Vector2D imageBaseToEntryPointVector = imageBaseToCenterVector.add(centerToEntryPointVector);
            Vector2D entryToExitPointVector = new Vector2D(rotatedRelativeEntryPoint, rotatedRelativeExitPoint);

            // create a transformation to be used for placing the image -- the transformation includes the rotation,
            // the
            // translation to the entry point and the translation from the entry to the exit directedPoint
            AffineTransform imagePlacementTransformation = new AffineTransform();
            imagePlacementTransformation.translate(currentTrackPos.getX(), currentTrackPos.getY());
            imagePlacementTransformation.rotate(currentAngle.getRadians());
            imagePlacementTransformation.concatenate(imageBaseToEntryPointVector.getInvertedTransform());
            AffineTransformOp imagePlacementOperation = new AffineTransformOp(imagePlacementTransformation, Activator
                    .getStore().getInt(MainPreferencePage.PREF_TILE_QUALITY));

            // create the lane placement transformation -- it's needed to draw the lanes (optional, used for
            // debugging
            // during lane definion) and to draw the cars
            AffineTransform lanePlacementTransformation = new AffineTransform();
            lanePlacementTransformation.translate(currentTrackPos.getX(), currentTrackPos.getY());
            lanePlacementTransformation.rotate(currentAngle.getRadians());
            lanePlacementTransformation.concatenate(centerToEntryPointVector.getInvertedTransform());

            // store all the transformations and angles
            imagePlacementOperations.put(s, imagePlacementOperation);
            lanePlacementTransformations.put(s, lanePlacementTransformation);
            carDrawingAngles.put(s, currentAngle.angle);

            // union this image's bounding box (rectangular and parallel to the viewport!) with the complete track
            // bounding box -- that way we'll get a bounding box for the whole track in the end
            Rectangle2D finalImageBoundingBox = imagePlacementOperation.getBounds2D(s.getImage().getColorImage());
            if(lastTrackBoundingBox == null) {
                lastTrackBoundingBox = finalImageBoundingBox;
            } else {
                Rectangle2D.union(lastTrackBoundingBox, finalImageBoundingBox, lastTrackBoundingBox);
            }

            // create a shape of the exact box of the image (not parallel to the viewport!) by transforming a
            // rectangle
            // corresponding to the image with the viewport transformation
            Rectangle2D r = new Rectangle2D.Double(0.0, 0.0, s.getImage().getWidth(), s.getImage().getHeight());
            Shape tileShape = imagePlacementTransformation.createTransformedShape(r);

            // store that shape, we'll need it later on to do hit detection
            lastTileShapeList.add(tileShape);

            // calculate the new track position by taking current track position and applying the
            // entryToExitPointTransformation
            AffineTransform entryToExitPointTransformation = entryToExitPointVector.getTransform();
            currentTrackPos = entryToExitPointTransformation.transform(currentTrackPos, null);

            // calculate the new angle by turning it by the angle this tile is supposedly changing the track
            // direction
            currentAngle.turn(s.getEntryToExitAngle());
        }
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
    private void drawTrack(Graphics2D targetG2D, AffineTransform viewportTransformation) {
        // back up the current transformation of the graphics context, so we can restore it later on
        AffineTransform originalTransformation = targetG2D.getTransform();

        // activate the viewport transformation
        targetG2D.setTransform(viewportTransformation);

        // if the currently cached off-screen image has been flagged as dirty, we'll need to regenerate it
        if(cachedTrackImage == null) {
            logger.debug("Regenerating off-screen image.");
            long baseTime = new Date().getTime();

            // recalculate the transformations and the bounding box
            doCalculations();
            long calculationTime = new Date().getTime() - baseTime;
            baseTime = new Date().getTime();

            // recreate the off-screen image
            createOffScreenImage();

            long drawingTime = new Date().getTime() - baseTime;
            logger.debug("Calculation took " + calculationTime + "ms, drawing the off-screen image took " + drawingTime
                    + "ms.");
        }

        // blit the off-screen image to the screen
        targetG2D.drawImage(cachedTrackImage, (int) lastTrackBoundingBox.getX(), (int) lastTrackBoundingBox.getY(),
                null);

        // restore the old transformation
        targetG2D.setTransform(originalTransformation);
    }

    private void createOffScreenImage() {
        // create a new off-screen image
        cachedTrackImage = new BufferedImage((int) lastTrackBoundingBox.getWidth(), (int) lastTrackBoundingBox
                .getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = cachedTrackImage.createGraphics();

        // clear the off-screen image with a white background
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, cachedTrackImage.getWidth(), cachedTrackImage.getHeight());

        // the calculations were performed around 0,0 so some areas might actually be in negative areas -- we can't draw
        // there on the off-screen image, so we'll translate all the upcoming drawing operations by the upper left
        // corner of the bounding box
        g
                .setTransform(AffineTransform.getTranslateInstance(-lastTrackBoundingBox.getX(), -lastTrackBoundingBox
                        .getY()));

        // initialize a counter (used for detected the selected tile)
        int counter = 0;

        // iterate over all track sections of the currently displayed track
        for(TrackSection s: track.getSections()) {
            // draw the image (using the negated version if it's the currently selected one) -- the placement is
            // already included in the transformation operation, so the coordinates used here are simple the origin
            // coordinates
            /*long baseTime = new Date().getTime();*/
            if(counter++ == selectedTile) {
                g.drawImage(s.getImage().getHighlightedImage(), imagePlacementOperations.get(s), 0, 0);
            } else {
                g.drawImage(s.getImage().getColorImage(), imagePlacementOperations.get(s), 0, 0);
                /*logger.debug("Drawing this tile took " + (new Date().getTime() - baseTime) + "ms.");*/
            }

            // draw the lanes on top if the user has requested that
            if(Activator.getStore().getBoolean(MainPreferencePage.PREF_SHOW_LANES)) {
                final Color[] laneColors = new Color[] { Color.YELLOW, Color.BLUE, Color.CYAN, Color.MAGENTA };
                if(track.getTileset().getLaneCount() > 4) {
                    throw new RuntimeException("The TrackWidget doesn't support more than four lanes yet.");
                }
                for(int laneIndex = 0; laneIndex < track.getTileset().getLaneCount(); laneIndex++) {
                    g.setColor(laneColors[laneIndex]);
                    for(LaneSection ls: s.getLane(laneIndex).getLaneSectionsCommon()) {
                        g.draw(lanePlacementTransformations.get(s).createTransformedShape(ls.getShape()));
                    }
                    for(LaneSection ls: s.getLane(laneIndex).getLaneSectionsRegular()) {
                        g.draw(lanePlacementTransformations.get(s).createTransformedShape(ls.getShape()));
                    }
                    for(LaneSection ls: s.getLane(laneIndex).getLaneSectionsChange()) {
                        g.draw(lanePlacementTransformations.get(s).createTransformedShape(ls.getShape()));
                    }
                    for(Checkpoint c: s.getLane(laneIndex).getCheckpoints()) {
                        g.draw(lanePlacementTransformations.get(s).createTransformedShape(
                                new Rectangle2D.Double(c.getPoint().x - 1, c.getPoint().y - 1, 3, 3)));
                    }
                }
            }
        }

        /*File outputfile = new File("f:\\test.png");
        try {
            ImageIO.write(cachedTrackImage, "png", outputfile);
        } catch(IOException e) {
            e.printStackTrace();
        }
        System.exit(1);*/
    }

    private void drawCars(Graphics2D g, AffineTransform viewportTransformation) {
        // back up the current transformation of the graphics context, so we can restore it later on
        AffineTransform originalTransformation = g.getTransform();

        // activate the viewport transformation
        g.setTransform(viewportTransformation);

        // determine the screen position of all cars
        CarScreenPosition carPosition[] = new CarScreenPosition[playersBean.size()];
        for(int i = 0; i < playersBean.size(); i++) {
            Player p = playersBean.get(i);
            carPosition[i] = track.determineScreenPositionFromPosition(p.getPosition());
        }

        // draw the cars, cars that have left the track using the transparent image
        for(int i = 0; i < playersBean.size(); i++) {
            Car car = playersBean.get(i).getCar();
            Image carImage = (carPosition[i].isOnTrack()) ? car.getImage().getColorImage() : car.getImage()
                    .getTransparentImage();
            drawCar(g, carImage, lanePlacementTransformations.get(carPosition[i].getSection()).transform(
                    carPosition[i].getDirectedPoint().point, null), new Angle(
                    carPosition[i].getDirectedPoint().angle.angle + carDrawingAngles.get(carPosition[i].getSection())));
        }

        // restore the old transformation
        g.setTransform(originalTransformation);
    }

    private void drawCar(Graphics2D g, Image carImage, Point2D position, Angle carDirection) {
        AffineTransform carRotationTransformation = new AffineTransform();
        carRotationTransformation.translate(position.getX(), position.getY());
        carRotationTransformation.rotate(carDirection.getRadians());
        carRotationTransformation.translate(-carImage.getWidth(null) / 2, -carImage.getHeight(null) / 2);

        g.drawImage(carImage, carRotationTransformation, null);
    }

    private void drawThrustGauges(Graphics2D g) {
        final BasicStroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
        final int LEFT_MARGIN = 40;
        final int TOP_MARGIN = 40;
        final int WIDTH = 20;
        final int HEIGHT = 100;
        final int OFFSET = 20;

        int i = 0;
        for(Player p: playersBean) {
            boolean isOnTrack = p.getPosition().isOnTrack;
            CarController dc = p.getController();
            String name = p.getName();
            ControllerSignal signal = dc.getLastSignal();
            if(signal != null) {
                int thrust = isOnTrack ? signal.getThrust() : 100;
                boolean isTriggered = isOnTrack ? signal.isTrigger() : false;

                // calculate the gauge positions
                int leftOffset = LEFT_MARGIN + (i * (WIDTH + OFFSET));
                RoundRectangle2D outerGauge = new RoundRectangle2D.Double(leftOffset, TOP_MARGIN, WIDTH, HEIGHT, 5, 5);
                Rectangle2D gradientClip = new Rectangle2D.Double(leftOffset, TOP_MARGIN + (HEIGHT - thrust), WIDTH,
                        HEIGHT - (HEIGHT - thrust));
                Paint fillPattern;
                if(p.getPosition().isOnTrack) {
                    fillPattern = new GradientPaint(new Point2D.Double(leftOffset + WIDTH / 2, TOP_MARGIN - 20),
                            Color.GREEN, new Point2D.Double(leftOffset + WIDTH / 2, TOP_MARGIN + HEIGHT + 20),
                            Color.RED);
                } else {
                    BufferedImage image = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d = image.createGraphics();
                    g2d.setBackground(Color.WHITE);
                    g2d.clearRect(0, 0, image.getWidth(), image.getHeight());
                    g2d.setColor(Color.BLUE);
                    g2d.drawLine(0, 0, 4, 4);
                    fillPattern = new TexturePaint(image, new Rectangle2D.Double(0, 0, 5, 5));
                }

                // draw the gauge
                g.setPaint(fillPattern);
                g.setClip(gradientClip);
                g.fill(outerGauge);
                g.setClip(null);
                g.setStroke(stroke);
                g.setColor(isTriggered ? Color.MAGENTA : Color.BLACK);
                g.draw(outerGauge);

                // draw the current speed into the gauge
                if(p.getPosition().isOnTrack) {
                    g.setFont(widgetFont);
                    g.setColor(Color.BLACK);
                    int thrustWidth = g.getFontMetrics(widgetFont).stringWidth(String.valueOf(thrust));
                    g.drawString(String.valueOf(thrust), leftOffset + WIDTH / 2 - thrustWidth / 2 + 1, TOP_MARGIN
                            + HEIGHT - 20);
                }

                // draw the player name below the gauge
                int nameWidth = g.getFontMetrics(widgetFont).stringWidth(name);
                g.setFont(widgetFont);
                g
                        .drawString(name, leftOffset + WIDTH / 2 - nameWidth / 2, TOP_MARGIN + HEIGHT
                                + (i % 2 == 0 ? 15 : 28));

                i++;
            }
        }
    }

    /**
     * Invoked by base class methods to trigger an actual repaint. Not to be called directly!
     */
    @Override
    public void paint(Control control, Graphics2D g2d) {
        isDrawing = true;

        // prepare the viewport transformation
        updateViewportTransformation();

        // update the frame counter
        if(new Date().getTime() - lastFrameCounterUpdate.getTime() >= 1000) {
            lastFrameCount = frameCounter;
            lastDroppedFrameCount = droppedFrameCounter;
            lastFrameCounterUpdate = new Date();
            frameCounter = 0;
            droppedFrameCounter = 0;
        }
        frameCounter++;

        // draw the track
        drawTrack(g2d, viewportTransformation);

        // draw the cars
        if(widgetMode == TrackWidgetMode.RACE_MODE) {
            drawCars(g2d, viewportTransformation);
        }

        // draw the inner controls
        if(isMouseOnWidget) {
            for(InnerControlID id: innerControls.keySet()) {
                innerControls.get(id).draw(g2d, id == hoveredInnerControl);
            }
        }

        // draw the thrust gauges
        if(widgetMode == TrackWidgetMode.RACE_MODE) {
            drawThrustGauges(g2d);
        }

        // draw the frame counter
        g2d.setFont(widgetFont);
        g2d.setColor(Color.BLACK);
        g2d.drawString(lastFrameCount + "fps (" + lastDroppedFrameCount + " dropped)", 10, 20);

        isDrawing = false;
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

    public void repaintIfIdle() {
        if(isDrawing) {
            droppedFrameCounter++;
        } else {
            // the repaint() method of the TrackWidget may only be executed on the event dispatcher thread!
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    if(!isDisposed()) {
                        repaint();
                    }
                }
            });
        }
    }

}
