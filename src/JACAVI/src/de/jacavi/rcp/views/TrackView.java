package de.jacavi.rcp.views;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.jacavi.rcp.widgets.TrackWidget;



/**
 * @author Fabian Rohn View that is used to show realtime Carrera cars on the track
 */
public class TrackView extends ViewPart {

    public static final String ID = "JACAVI.trackView";

    public TrackView() {
    // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl(Composite parent) {
        try {
            new TrackWidget(parent);
        } catch(IOException e) {
            throw new RuntimeException("Error while creating TrackWidget.");
        }
        // parent.setBackgroundImage(Activator.getImageDescriptor("/icons/strecke.jpg").createImage());
    }

    @Override
    public void setFocus() {
    // TODO Auto-generated method stub

    }

}
