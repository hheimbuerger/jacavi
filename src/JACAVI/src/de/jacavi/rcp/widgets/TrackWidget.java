package de.jacavi.rcp.widgets;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.jacavi.appl.valueobjects.Angle;
import de.jacavi.rcp.Activator;
import de.jacavi.track.Track;
import de.jacavi.track.TrackSection;
import de.jacavi.track.Track.TrackLoadingException;



public class TrackWidget extends Canvas {

    private Point panPosition = new Point(-300, -300);

    private boolean isPanningActive = false;

    private Point panningStartPosition = null;

    private Track currentTrack = null;

    private Image trackImage = null;

    public TrackWidget(Composite parent) throws TrackLoadingException {
        super(parent, SWT.DOUBLE_BUFFERED);

        final Color white = new Color(null, 255, 255, 255);
        setBackground(white);

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

        gc.drawImage(trackImage, panPosition.x, panPosition.y);

        // gc.drawString("text", x, 1);
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
        BufferedImage bi = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setBackground(java.awt.Color.WHITE);
        g.clearRect(0, 0, bi.getWidth(), bi.getHeight());

        Angle currentAngle = new Angle(0);
        Point currentTrackPos = new Point(bi.getWidth() / 2, bi.getHeight() / 2);
        for(TrackSection s: currentTrack.getSections()) {
            Double x, y;

            System.out.println("Track position:          " + currentTrackPos.x + "/" + currentTrackPos.y);

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
            System.out.println("Center drawing position: " + centerDrawingPoint.x + "/" + centerDrawingPoint.y);
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
            System.out.println("New angle: " + currentAngle.angle);
        }
        markPoint(g, currentTrackPos, java.awt.Color.CYAN);

        Display display = Display.getCurrent();
        trackImage = new Image(display, convertToSWT(bi));
    }

    static ImageData convertToSWT(BufferedImage bufferedImage) {
        if(bufferedImage.getColorModel() instanceof DirectColorModel) {
            DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
            PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel
                    .getBlueMask());
            ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel
                    .getPixelSize(), palette);
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[3];
            for(int y = 0; y < data.height; y++) {
                for(int x = 0; x < data.width; x++) {
                    raster.getPixel(x, y, pixelArray);
                    int pixel = palette.getPixel(new RGB(pixelArray[0], pixelArray[1], pixelArray[2]));
                    data.setPixel(x, y, pixel);
                }
            }
            return data;
        } else if(bufferedImage.getColorModel() instanceof IndexColorModel) {
            IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
            int size = colorModel.getMapSize();
            byte[] reds = new byte[size];
            byte[] greens = new byte[size];
            byte[] blues = new byte[size];
            colorModel.getReds(reds);
            colorModel.getGreens(greens);
            colorModel.getBlues(blues);
            RGB[] rgbs = new RGB[size];
            for(int i = 0; i < rgbs.length; i++) {
                rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
            }
            PaletteData palette = new PaletteData(rgbs);
            ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel
                    .getPixelSize(), palette);
            data.transparentPixel = colorModel.getTransparentPixel();
            WritableRaster raster = bufferedImage.getRaster();
            int[] pixelArray = new int[1];
            for(int y = 0; y < data.height; y++) {
                for(int x = 0; x < data.width; x++) {
                    raster.getPixel(x, y, pixelArray);
                    data.setPixel(x, y, pixelArray[0]);
                }
            }
            return data;
        }
        return null;
    }

}
