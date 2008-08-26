package de.jacavi.rcp.views;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.jacavi.appl.track.Track;
import de.jacavi.rcp.editors.TrackDesigner;
import de.jacavi.rcp.widgets.TrackWidget;



/**
 * @author Fabian Rohn View that is used to show realtime Carrera cars on the track
 */
public class TrackView extends ViewPart {

    private static Log log = LogFactory.getLog(TrackView.class);

    public static final String ID = "JACAVI.trackView";

    private final Track currentTrack;

    private TrackWidget trackWidget;

    public TrackView() {
        TrackDesigner active = (TrackDesigner) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .getActiveEditor();
        currentTrack = active.getTrackWidget().getTrack();
    }

    @Override
    public void createPartControl(Composite parent) {
        try {
            trackWidget = new TrackWidget(parent, currentTrack);
        } catch(IOException e) {
            log.error("TrackWidget could not be created", e);
            throw new RuntimeException("Error while creating TrackWidget.");
        }
    }

    @Override
    public void setFocus() {
        trackWidget.setFocus();
    }

}
