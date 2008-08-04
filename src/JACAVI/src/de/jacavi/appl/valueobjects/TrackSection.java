package de.jacavi.appl.valueobjects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import de.jacavi.rcp.Activator;



public class TrackSection {

    private Image sectionImage;

    private Point nextSectionOffset;

    private int entryToExitAngle;

    public TrackSection(String filename, Point nextSectionOffset, int entryToExitAngle) {
        // HACK: these images are not properly disposed yet
        sectionImage = Activator.getImageDescriptor("/tracksections/" + filename).createImage();

        this.nextSectionOffset = nextSectionOffset;
        this.entryToExitAngle = entryToExitAngle;
    }

    public Image getRotatedImage(int rotationAngle) {
        Display display = Display.getCurrent();

        // HACK: rotation by arbitrary angles is required here!
        ImageData newData;
        switch(rotationAngle) {
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
        }

        // TODO: maybe there's a way to enforce proper disposing of these images. Right now, it's up to the caller.
        return new Image(display, newData);
    }

    public Point getNextSectionOffset() {
        return nextSectionOffset;
    }

    public int getEntryToExitAngle() {
        return entryToExitAngle;
    }

    // TODO: this helper method doesn't really belong here
    private static ImageData rotate(ImageData srcData, int direction) {
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
    }

}
