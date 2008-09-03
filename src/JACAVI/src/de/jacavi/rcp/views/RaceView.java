package de.jacavi.rcp.views;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.jacavi.appl.track.Track;
import de.jacavi.rcp.editors.TrackDesigner;
import de.jacavi.rcp.widgets.TrackWidget;
import de.jacavi.rcp.widgets.TrackWidget.TrackWidgetMode;



/**
 * @author Fabian Rohn View that is used to show realtime Carrera cars on the track
 */
public class RaceView extends ViewPart implements IPerspectiveListener {

    private static Log log = LogFactory.getLog(RaceView.class);

    public static final String ID = "JACAVI.trackView";

    private final Track currentTrack;

    private TrackWidget trackWidget;

    public RaceView() {
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(this);
        currentTrack = getActiveTrack();
    }

    @Override
    public void createPartControl(Composite parent) {
        try {
            trackWidget = new TrackWidget(parent, currentTrack, TrackWidgetMode.RACE_MODE);
        } catch(IOException e) {
            log.error("TrackWidget could not be created", e);
            throw new RuntimeException("Error while creating TrackWidget.");
        }
    }

    @Override
    public void setFocus() {
        trackWidget.setFocus();
    }

    @Override
    public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
        trackWidget.setTrack(getActiveTrack());
    }

    @Override
    public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {}

    /**
     * @return the actual track of the active editor
     */
    private Track getActiveTrack() {
        TrackDesigner active = (TrackDesigner) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getActiveEditor();
        return active.getTrackWidget().getTrack();
    }

    public void repaint() {
        // the repaint() method of the TrackWidget may only be executed on the event dispatcher thread!
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if(!trackWidget.isDisposed())
                    trackWidget.repaint();
            }
        });
    }
}
