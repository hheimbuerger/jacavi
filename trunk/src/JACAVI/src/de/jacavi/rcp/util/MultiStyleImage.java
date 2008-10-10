package de.jacavi.rcp.util;

import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.ColorConvertOp;
import java.awt.image.ComponentColorModel;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.eclipse.swt.graphics.Point;

import de.jacavi.rcp.Activator;



public class MultiStyleImage {
    private final BufferedImage colorImage;

    private BufferedImage grayscaleImage = null;

    private BufferedImage highlightedImage = null;

    private BufferedImage transparentImage;

    public MultiStyleImage(String filename) throws IOException {
        InputStream resourceAsStream = Activator.getResourceAsStream(filename);
        if(resourceAsStream == null)
            throw new IllegalArgumentException("The image resource " + filename + " could not be loaded.");
        colorImage = ImageIO.read(resourceAsStream);
        if(!(colorImage.getColorModel() instanceof ComponentColorModel))
            throw new IllegalArgumentException("Image " + filename
                    + " uses a color model that is not supported. Only images with a component"
                    + " color model are supported. Try converting the image to a 24- or 32-bit"
                    + " bitmap, using e.g. the PNG format.");
    }

    public void prepareGrayscaleImage() {
        BufferedImageOp grayscaleOperation = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        grayscaleImage = grayscaleOperation.filter(colorImage, null);
    }

    public void prepareHighlightedImage() {
        byte[] invert = new byte[256];
        byte[] identity = new byte[256];
        for(int i = 0; i < 256; i++) {
            invert[i] = (byte) (255 - i);
            identity[i] = (byte) i;
        }
        byte[][] lookupArray;
        if(colorImage.getColorModel().getComponentSize().length == 3)
            lookupArray = new byte[][] { invert, identity, invert };
        else
            lookupArray = new byte[][] { invert, identity, invert, identity };
        ByteLookupTable lookup = new ByteLookupTable(0, lookupArray);
        BufferedImageOp blueInvertOp = new LookupOp(lookup, null);

        highlightedImage = blueInvertOp.filter(colorImage, null);
    }

    private void prepareTransparentImage() {
        // create a rescale filter op that makes the image 50% opaque
        float[] scales = { 1f, 1f, 1f, 0.5f };
        float[] offsets = new float[4];
        RescaleOp rop = new RescaleOp(scales, offsets, null);

        // filter the image
        transparentImage = rop.filter(colorImage, null);
    }

    public BufferedImage getColorImage() {
        return colorImage;
    }

    public BufferedImage getGrayscaleImage() {
        if(grayscaleImage == null)
            prepareGrayscaleImage();
        return grayscaleImage;
    }

    public BufferedImage getHighlightedImage() {
        if(highlightedImage == null)
            prepareHighlightedImage();
        return highlightedImage;
    }

    public BufferedImage getTransparentImage() {
        if(transparentImage == null)
            prepareTransparentImage();
        return transparentImage;
    }

    public int getWidth() {
        return colorImage.getWidth();
    }

    public int getHeight() {
        return colorImage.getHeight();
    }

    public Shape getShape(Point position) {
        return new Rectangle2D.Double(position.x, position.y, colorImage.getWidth(), colorImage.getHeight());
    }
}
