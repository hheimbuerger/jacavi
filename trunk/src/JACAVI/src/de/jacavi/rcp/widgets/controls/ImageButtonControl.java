/*
 * Copyright (C) Skillworks AG 2008. All Rights Reserved Confidential
 */
package de.jacavi.rcp.widgets.controls;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.eclipse.swt.graphics.Point;

import de.jacavi.rcp.util.GrayscalableImage;



public class ImageButtonControl extends InnerControl {
    final static int MARGIN = 6;

    final static int OFFSET = 4;

    final GrayscalableImage image;

    private Point position;

    private final int indexFromRight;

    private final boolean isTop;

    public ImageButtonControl(String imageFilename, int indexFromRight, boolean isTop) throws IOException {
        this.image = new GrayscalableImage(imageFilename, true);
        this.indexFromRight = indexFromRight;
        this.isTop = isTop;
    }

    @Override
    public void draw(Graphics2D g2d, boolean isHoveredOver) {
        final BufferedImage colorProcessedImage = isHoveredOver ? image.getColorImage() : image.getGrayscaleImage();
        g2d.drawImage(colorProcessedImage, null, position.x, position.y);
    }

    @Override
    public void reposition(Point size) {
        final int IMAGE_WIDTH = image.getCurrentImage().getWidth();
        final int IMAGE_HEIGHT = image.getCurrentImage().getHeight();
        position = new Point(size.x - MARGIN - IMAGE_WIDTH - (indexFromRight * (IMAGE_WIDTH + OFFSET)), (isTop ? MARGIN
                : size.y - IMAGE_HEIGHT - MARGIN));
        shape = image.getShape(position);
    }
}