package de.jacavi.rcp.widgets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.holongate.j2d.J2DUtilities;

import de.jacavi.appl.track.Angle;
import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.TrackSection;
import de.jacavi.appl.track.Track.TrackLoadingException;
import de.jacavi.appl.track.Track.TrackModificationListener;



public class TrackWidget extends J2DCanvas implements IPaintable, TrackModificationListener {

    /** Logger for this class */
    private static final Logger logger = Logger.getLogger(TrackWidget.class);

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

    private Point panPosition = new Point(0, 0);

    private boolean isCurrentlyPanning = false;

    private boolean isCurrentlyRotating = false;

    private Point panningStartPosition = null;

    private Track track = null;

    // private BufferedImage trackImage = null;

    private double zoomLevel = 1.0;

    private Angle rotationAngle = new Angle(0);

    private Point rotationStartPosition = null;

    private List<Shape> currentShapeList;

    private AffineTransform currentTransform;

    private int selectedTile = -1;

    public TrackWidget(Composite parent, Track track) throws TrackLoadingException {
        super(parent, SWT.NONE, null);
        setPaintable(this);

        setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

        Map<RenderingHints.Key, Object> hints = new HashMap<RenderingHints.Key, Object>(3);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
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
        }

        // remove the current selection
        selectedTile = -1;

        // redraw the widget
        repaint();
    }

    public int getSelectedTile() {
        return selectedTile;
    }

    public void setSelectedTile(int selectedTile) {
        this.selectedTile = selectedTile;
        repaint();
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
        // System.out.println("doubleclick: " + e.x + "/" + e.y);
        for(int i = currentShapeList.size() - 1; i >= 0; i--) {
            // System.out.println("shape " + i + ": " + currentShapeList.get(i).getBounds2D().getX() + ", "
            // + currentShapeList.get(i).getBounds2D().getY());
            if(currentShapeList.get(i).contains(e.x, e.y)) {
                // System.out.println("detected hit: " + i);
                selectedTile = i;
                repaint();
                break;
            }
        }
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

    /*private Rectangle2D drawByCenter(Graphics2D g, BufferedImage image, Point destination, Angle rotation) {

        return boundingBoxAfterRotation;
    }*/

    private void markPoint(Graphics2D g, Point2D currentTrackPos, Color color) {
        g.setColor(color);
        g.drawRect(new Double(currentTrackPos.getX() - 1).intValue(),
                new Double(currentTrackPos.getY() - 1).intValue(), 3, 3);
    }

    private void drawTrack(Graphics2D g, Point size) {
        // g.setBackground(Color.WHITE);
        // g.clearRect(0, 0, size.x, size.y);

        Angle currentAngle = new Angle(0);
        Point2D currentTrackPos = new Point2D.Double(0.0, 0.0);
        currentShapeList = new ArrayList<Shape>();
        // int ulx = currentTrackPos.x, uly = currentTrackPos.y, lrx = currentTrackPos.x, lry = currentTrackPos.y;
        Rectangle2D trackBoundingBox = new Rectangle2D.Double();
        int counter = 0;
        for(TrackSection s: track.getSections()) {
            // Double deltax, deltay;

            // System.out.println("Track position:          " + currentTrackPos.x + "/" + currentTrackPos.y);

            // step #1: rotate tile as necessary
            // Image rotatedImage = s.getRotatedImage(currentAngle);
            BufferedImage image = (BufferedImage) s.getImage();

            // step #2: calculate entry point relative to the center after rotation
            // <juckto> xnew = deltax*cos(0) - deltay*sin(0)
            // <juckto> ynew = deltax*sin(0) + deltay*cos(0)
            /*deltax = new Double(s.getEntryPoint().x * Math.cos(currentAngle.getRadians()) - s.getEntryPoint().y
                    * Math.sin(currentAngle.getRadians()));
            deltay = new Double(s.getEntryPoint().x * Math.sin(currentAngle.getRadians()) + s.getEntryPoint().y
                    * Math.cos(currentAngle.getRadians()));
            Point relativeEntryPoint = new Point(deltax.intValue(), deltay.intValue());*/
            // System.out.println("relativeEntryPoint:      " + relativeEntryPoint.x + "/" + relativeEntryPoint.y);
            // step #3: calculate center drawing point and draw the image
            /*Point centerDrawingPoint = new Point(currentTrackPos.x + (-relativeEntryPoint.x), currentTrackPos.y
                    + (-relativeEntryPoint.y));*/
            // System.out.println("Center drawing position: " + centerDrawingPoint.x + "/" + centerDrawingPoint.y);
            AffineTransform rotationTransform = AffineTransform.getRotateInstance(currentAngle.getRadians());
            AffineTransformOp rotationTransformOperation = new AffineTransformOp(rotationTransform,
                    AffineTransformOp.TYPE_BICUBIC);
            // Rectangle2D boundingBoxAfterRotation = rotationTransformOperation.getBounds2D(image);

            // step #2: calculate entry point relative to the center after rotation
            Point2D relativeEntryPoint = new Point2D.Double(s.getEntryPoint().x, s.getEntryPoint().y);
            Point2D relativeExitPoint = new Point2D.Double(s.getExitPoint().x, s.getExitPoint().y);
            Point2D rotatedRelativeEntryPoint = rotationTransformOperation.getPoint2D(relativeEntryPoint, null);
            Point2D rotatedRelativeExitPoint = rotationTransformOperation.getPoint2D(relativeExitPoint, null);
            Vector2D centerToEntryPointVector = new Vector2D(relativeEntryPoint);
            Vector2D imageBaseToCenterVector = new Vector2D(new Point2D.Double(image.getWidth() / 2,
                    image.getHeight() / 2));
            Vector2D imageBaseToEntryPointVector = imageBaseToCenterVector.add(centerToEntryPointVector);
            Vector2D rotatedEntryToExitPointVector = new Vector2D(rotatedRelativeEntryPoint, rotatedRelativeExitPoint);
            // System.out.println("centerToEntryPointVector: " + centerToEntryPointVector);
            // System.out.println("imageBaseToCenterVector: " + imageBaseToCenterVector);
            // System.out.println(rotatedRelativeEntryPoint);

            // AffineTransform imagePlacementTransform = new AffineTransform();
            // imagePlacementTransform.rotate(currentAngle.getRadians());
            // imagePlacementTransform.translate(zoomLevel, zoomLevel);

            /*rotationTransform.concatenate(AffineTransform.getTranslateInstance(boundingBoxAfterRotation.getX(),
                    boundingBoxAfterRotation.getY()));
            rotationTransform.concatenate(AffineTransform.getTranslateInstance(boundingBoxAfterRotation.getWidth() / 2,
                    boundingBoxAfterRotation.getHeight() / 2));*/

            // System.out.println(boundingBoxAfterRotation);
            /*Point drawingPosition = new Point(centerDrawingPoint.x
                    - new Double(boundingBoxAfterRotation.getX()).intValue()
                    - new Double(boundingBoxAfterRotation.getWidth() / 2).intValue(), centerDrawingPoint.y
                    - new Double(boundingBoxAfterRotation.getY()).intValue()
                    - new Double(boundingBoxAfterRotation.getHeight() / 2).intValue());*/
            AffineTransform imagePlacementTransform = new AffineTransform();
            imagePlacementTransform.translate(currentTrackPos.getX(), currentTrackPos.getY());
            imagePlacementTransform.rotate(currentAngle.getRadians());
            imagePlacementTransform.concatenate(imageBaseToEntryPointVector.getInvertedTransform());
            AffineTransformOp imagePlacementOperation = new AffineTransformOp(imagePlacementTransform,
                    AffineTransformOp.TYPE_BICUBIC);
            // Shape s2 = imagePlacementTransform.createTransformedShape(\c)
            // g.drawImage(image, imagePlacementOperation, new Double(currentTrackPos.getX()).intValue(), new
            // Double(currentTrackPos.getY()).intValue());
            g.drawImage(image, imagePlacementOperation, 0, 0);

            markPoint(g, currentTrackPos, Color.GREEN);

            // AffineTransform translateTransform = AffineTransform.getTranslateInstance(currentTrackPos.getX(),
            // currentTrackPos.getY());
            // Shape s3 = imagePlacementTransform.createTransformedShape(imagePlacementOperation.getBounds2D(image));
            Rectangle2D finalImageBoundingBox = imagePlacementOperation.getBounds2D(image);
            Rectangle2D.union(trackBoundingBox, finalImageBoundingBox, trackBoundingBox);

            // g.setColor(Color.YELLOW);
            // g.draw(finalImageBoundingBox);

            g.setColor(Color.YELLOW);
            Rectangle2D r = new Rectangle2D.Double(0.0, 0.0, image.getWidth(), image.getHeight());
            Shape tileShape = imagePlacementTransform.createTransformedShape(r);
            if(counter++ == selectedTile)
                g.draw(tileShape);

            currentShapeList.add(currentTransform.createTransformedShape(tileShape));

            /*g.setColor(Color.YELLOW);
            g.drawRect(centerDrawingPoint.x - new Double(boundingBoxAfterRotation.getWidth() / 2).intValue(),
                    centerDrawingPoint.y - new Double(boundingBoxAfterRotation.getHeight() / 2).intValue(), new Double(
                            boundingBoxAfterRotation.getWidth()).intValue(), new Double(boundingBoxAfterRotation
                            .getHeight()).intValue());*/

            // markPoint(g, centerDrawingPoint, Color.GREEN);
            /*ulx = Math.min(ulx, centerDrawingPoint.x - image.getWidth(null) / 2);
            uly = Math.min(uly, centerDrawingPoint.y - image.getHeight(null) / 2);
            lrx = Math.max(lrx, centerDrawingPoint.x + image.getWidth(null) / 2);
            lry = Math.max(lry, centerDrawingPoint.y + image.getHeight(null) / 2);*/
            /*AffineTransform translationTransform = AffineTransform.getTranslateInstance(centerDrawingPoint.x,
                    centerDrawingPoint.y);
            Rectangle2D translatedBoundingBox = translationTransform.createTransformedShape(boundingBoxAfterRotation)
                    .getBounds2D();*/
            // System.out.println(translatedBoundingBox);
            /*if(trackBoundingBox == null)
                trackBoundingBox = translatedBoundingBox;
            else
                Rectangle2D.union(trackBoundingBox, translatedBoundingBox, trackBoundingBox);*/

            // step #4: calculate the new track position, by taking the rotated entry point and moving it to the exit
            // point, and calculate the new angle
            /*deltax = new Double(s.getExitPoint().x * Math.cos(currentAngle.getRadians()) - s.getExitPoint().y
                    * Math.sin(currentAngle.getRadians()));
            deltay = new Double(s.getExitPoint().x * Math.sin(currentAngle.getRadians()) + s.getExitPoint().y
                    * Math.cos(currentAngle.getRadians()));
            Point relativeExitPoint = new Point(deltax.intValue(), deltay.intValue());
            // System.out.println("relativeExitPoint:       " + relativeExitPoint.x + "/" + relativeExitPoint.y);
            markPoint(g, currentTrackPos, Color.CYAN);
            currentTrackPos = new Point(currentTrackPos.x + (relativeExitPoint.x - relativeEntryPoint.x),
                    currentTrackPos.y + (relativeExitPoint.y - relativeEntryPoint.y));*/

            AffineTransform entryToExitPointTransformation = rotatedEntryToExitPointVector.getTransform();
            currentTrackPos = entryToExitPointTransformation.transform(currentTrackPos, null);
            currentAngle.turn(s.getEntryToExitAngle());
            // System.out.println("currentTrackPos: " + currentTrackPos);

        }
        // markPoint(g, currentTrackPos, Color.CYAN);

        g.setColor(Color.RED);
        g.draw(trackBoundingBox);

        // Display display = Display.getCurrent();
        // trackImage = new Image(display, convertToSWT(bi));
    }

    @Override
    public void trackModified() {
        repaint();
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

    @Override
    public void paint(Control control, Graphics2D g2d) {
        Point size = control.getSize();

        currentTransform = new AffineTransform();
        currentTransform.translate(size.x / 2, size.y / 2);
        currentTransform.translate(panPosition.x, panPosition.y);
        currentTransform.scale(zoomLevel, zoomLevel);
        currentTransform.rotate(rotationAngle.getRadians(), 0, 0);

        // store the current...
        AffineTransform originalTransform = g2d.getTransform();
        g2d.setTransform(currentTransform);
        drawTrack(g2d, size);
        g2d.setTransform(originalTransform);

        /*g2d.setColor(Color.YELLOW);
        for(Shape s: currentShapeList)
            g2d.draw(s);*/

        // drawScrollers(g2d, size);
        // g2d.setTransform(null);
        // do the actual drawing of the widget
        // g2d.drawImage(trackImage, panPosition.x, panPosition.y, null);
    }

    @Override
    public void redraw(Control control, GC gc) {}

    @Override
    public Rectangle2D getBounds(Control control) {
        return J2DUtilities.toRectangle2D(control.getBounds());
    }

}
