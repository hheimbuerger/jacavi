package de.jacavi.rcp.dlg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import de.jacavi.rcp.Activator;



/**
 * This class opens a shell and shows a traffic gif to let the players prepare for race
 * 
 * @author Fabian
 */
public class TrafficLightsDialog {

    private final String gifPath = "icons/traffic/ampel.gif";

    private final Display display = Display.getCurrent();

    private final Shell shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.NO_TRIM);;

    public TrafficLightsDialog() {
        initShell();
        animateGif();
    }

    private void animateGif() {
        ImageLoader imageLoader = new ImageLoader();
        final ImageData[] imageDatas = imageLoader.load(gifPath);

        final Image image = new Image(display, imageDatas[0].width, imageDatas[0].height);
        final Canvas canvas = new Canvas(shell, SWT.NULL);

        canvas.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                e.gc.drawImage(image, 0, 0);
            }
        });

        final GC gc = new GC(image);

        final Thread thread = new Thread() {
            int frameIndex = 0;

            @Override
            public void run() {
                for(@SuppressWarnings("unused") ImageData imageData: imageDatas) {

                    frameIndex %= imageDatas.length;

                    final ImageData frameData = imageDatas[frameIndex];
                    display.asyncExec(new Runnable() {
                        public void run() {
                            if(!gc.isDisposed() && !canvas.isDisposed()) {
                                Image frame = new Image(display, frameData);
                                gc.drawImage(frame, frameData.x, frameData.y);
                                frame.dispose();
                                canvas.redraw();
                            }
                        }
                    });

                    try {
                        // delay
                        Thread.sleep(1000);
                    } catch(InterruptedException e) {
                        return;
                    }

                    frameIndex += 1;
                }
                display.asyncExec(new Runnable() {
                    public void run() {
                        if(!shell.isDisposed()) {
                            shell.dispose();
                        }
                        if(!image.isDisposed()) {
                            image.dispose();
                        }
                        if(!gc.isDisposed()) {
                            gc.dispose();
                        }
                    }
                });
            }
        };

        shell.addShellListener(new ShellAdapter() {
            @Override
            public void shellClosed(ShellEvent e) {
                thread.interrupt();
            }
        });

        shell.open();

        thread.start();

        // Set up the event loop.
        while(!shell.isDisposed()) {
            if(!display.readAndDispatch()) {
                // If no more entries in event queue
                display.sleep();
            }
        }
    }

    /**
     * shell is set into the center and gets the size of the image
     */
    private void initShell() {
        Image gifImage = Activator.getImageDescriptor(gifPath).createImage();

        // setting size
        shell.setLayout(new FillLayout());
        shell.setSize(gifImage.getBounds().width, gifImage.getBounds().height);

        // setting position
        Monitor primary = display.getPrimaryMonitor();
        Rectangle bounds = primary.getBounds();
        Rectangle rect = shell.getBounds();
        int x = bounds.x + (bounds.width - rect.width) / 2;
        int y = bounds.y + (bounds.height - rect.height) / 2;
        shell.setLocation(x, y);
    }

}