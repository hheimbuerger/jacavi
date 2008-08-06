package de.jacavi.appl.valueobjects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.eclipse.swt.graphics.Point;



public class TrackSection {

    private final Tile tile;

    public TrackSection(Tile tile) {
        this.tile = tile;
    }

    public Angle getEntryToExitAngle() {
        return tile.getEntryToExitAngle();
    }

    public Point getEntryPoint() {
        return tile.getEntryPoint();
    }

    public Point getExitPoint() {
        return tile.getExitPoint();
    }

    public java.awt.Image getRotatedImage(Angle currentAngle) throws IOException {
        int maxDimension = Math.max(tile.getSectionImage().getWidth(), tile.getSectionImage().getHeight());
        BufferedImage targetBI = new BufferedImage(maxDimension * 2, maxDimension * 2, BufferedImage.TYPE_INT_ARGB);

        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(currentAngle.angle), (targetBI.getWidth() / 2), (targetBI.getHeight() / 2));
        Graphics2D g = (Graphics2D) targetBI.getGraphics();
        g.setBackground(Color.WHITE);
        g.setTransform(at);

        g.drawImage(tile.getSectionImage(), (targetBI.getWidth() / 2 - tile.getSectionImage().getWidth() / 2),
                (targetBI.getHeight() / 2 - tile.getSectionImage().getHeight() / 2), null);
        // return convertToTransparent(targetBI, transparentColor);
        return targetBI;

        // Display display = Display.getCurrent();

        // HACK: rotation by arbitrary angles is required here!
        // ImageData newData;
        /*switch(rotationAngle) {
            case 0:
                newData = sectionImage.getImageData();
                break;
            case 90:
                newData = rotate(sectionImage.getImageData(), SWT.RIGHT);
                break;
            case 180:
                newData = rotate(sectionImage.getImageData(), SWT.DOWN);
                break;
            case 270:
                newData = rotate(sectionImage.getImageData(), SWT.LEFT);
                break;
            default:
                throw new RuntimeException("Rotating by arbitrary angles is not yet supported!");
        }*/
        // newData = rotate(null, 45);
        // TODO: maybe there's a way to enforce proper disposing of these images. Right now, it's up to the caller.
        // return new Image(display, newData);
    }

    /*    private BufferedImage convertToTransparent(BufferedImage src, Color transparentColor) {
            int w = src.getWidth();
            int h = src.getHeight();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
            BufferedImage dest = gc.createCompatibleImage(w, h, Transparency.BITMASK);
            int trgb;
            if(transparentColor != null) {
                trgb = transparentColor.getRGB() | 0xff000000;
            } else {
                trgb = 0xffff00ff;
            }
            // Copy pixels a scan line at a time
            int buf[] = new int[w];
            for(int y = 0; y < h; y++) {
                src.getRGB(0, y, w, 1, buf, 0, w);
                for(int x = 0; x < w; x++) {
                    if(buf[x] == trgb) {
                        buf[x] = 0;
                    }
                }
                dest.setRGB(0, y, w, 1, buf, 0, w);
            }
            return dest;
        }
    */
    // TODO: this helper method doesn't really belong here
    /*private static ImageData rotate(ImageData srcData, int direction) {
        int bytesPerPixel = srcData.bytesPerLine / srcData.width;
        int destBytesPerLine = (direction == SWT.DOWN) ? srcData.width * bytesPerPixel : srcData.height * bytesPerPixel;
        byte[] newData = new byte[srcData.data.length];
        int width = 0, height = 0;
        for(int srcY = 0; srcY < srcData.height; srcY++) {
            for(int srcX = 0; srcX < srcData.width; srcX++) {
                int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
                switch(direction) {
                    case SWT.LEFT: // left 90 degrees
                        destX = srcY;
                        destY = srcData.width - srcX - 1;
                        width = srcData.height;
                        height = srcData.width;
                        break;
                    case SWT.RIGHT: // right 90 degrees
                        destX = srcData.height - srcY - 1;
                        destY = srcX;
                        width = srcData.height;
                        height = srcData.width;
                        break;
                    case SWT.DOWN: // 180 degrees
                        destX = srcData.width - srcX - 1;
                        destY = srcData.height - srcY - 1;
                        width = srcData.width;
                        height = srcData.height;
                        break;
                }
                destIndex = (destY * destBytesPerLine) + (destX * bytesPerPixel);
                srcIndex = (srcY * srcData.bytesPerLine) + (srcX * bytesPerPixel);
                System.arraycopy(srcData.data, srcIndex, newData, destIndex, bytesPerPixel);
            }
        }
        // destBytesPerLine is used as scanlinePad to ensure that no padding is
        // required
        return new ImageData(width, height, srcData.depth, srcData.palette, destBytesPerLine, newData);
    }*/

}
