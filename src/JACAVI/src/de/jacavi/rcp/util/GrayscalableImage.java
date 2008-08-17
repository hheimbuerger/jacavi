package de.jacavi.rcp.util;

import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.graphics.Point;

import de.jacavi.rcp.Activator;



public class GrayscalableImage {
    private BufferedImage colorImage;

    private BufferedImage grayscaleImage;

    private boolean isStateGrayscale;

    public GrayscalableImage(String filename, boolean isInitiallyGrayscale) throws IOException {
        colorImage = ImageIO.read(Activator.getResourceAsStream(filename));

        BufferedImageOp grayscaleOperation = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        grayscaleImage = grayscaleOperation.filter(colorImage, null);

        isStateGrayscale = isInitiallyGrayscale;
    }

    public void setState(boolean isGrayscale) {
        isStateGrayscale = isGrayscale;
    }

    public BufferedImage getCurrentImage() {
        return isStateGrayscale ? grayscaleImage : colorImage;
    }

    public BufferedImage getColorImage() {
        return colorImage;
    }

    public BufferedImage getGrayscaleImage() {
        return grayscaleImage;
    }

    public Shape getShape(Point position) {
        return new Rectangle2D.Double(position.x, position.y, colorImage.getWidth(), colorImage.getHeight());
    }
}
