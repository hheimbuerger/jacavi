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

}
