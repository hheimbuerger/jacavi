package de.jacavi.rcp.editors;

import java.io.File;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import de.jacavi.appl.track.Track;



public class TrackDesignerInput implements IEditorInput {

    private Track track;

    private String filename;

    public TrackDesignerInput(String filename, Track track) {
        this.filename = filename;
        this.track = track;
    }

    public boolean exists() {
        return false;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return new File(filename).getName().split(Track.FILE_EXTENSION)[0];
    }

    public IPersistableElement getPersistable() {
        return null;
    }

    public String getToolTipText() {
        return "Track Editor";
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter) {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if(super.equals(obj)) {
            return true;
        }

        if(obj instanceof TrackDesignerInput) {
            return track.getTrackName().equals(((TrackDesignerInput) obj).getName());
        }
        return false;

    }

    @Override
    public int hashCode() {
        return track.getTrackName().hashCode();
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
