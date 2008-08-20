package de.jacavi.rcp.editors;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import de.jacavi.appl.track.Tile;
import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.Track.InitialTileMayNotBeRemoved;
import de.jacavi.rcp.dlg.SafeSaveDialog;
import de.jacavi.rcp.util.ExceptionHandler;
import de.jacavi.rcp.widgets.TrackWidget;



public class TrackDesigner extends EditorPart {

    private static Log log = LogFactory.getLog(TrackDesigner.class);

    public static String ID = "JACAVI.TrackDesigner";

    boolean isDirty = false;

    private Track currentTrack;

    private TrackWidget trackWidget;

    /**
     * The following flag is catched from all IPropertyListeners, is fired when the track has changed
     */
    public static final int PROP_TRACK_CHANGED = 1;

    /**
     * The following flag is catched from all IPropertyListeners, is fired when a selection has changed
     */
    public static final int PROP_SELECTION_CHANGED = 2;

    @Override
    protected void setInput(IEditorInput input) {

        super.setInput(input);
        currentTrack = ((TrackDesignerInput) input).getTrack();
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        monitor.beginTask("Save", IProgressMonitor.UNKNOWN);
        SafeSaveDialog dlg = new SafeSaveDialog(getSite().getShell());

        dlg.setText("Save");
        String[] filterExt = { "*" + Track.FILE_EXTENSION };
        dlg.setFilterExtensions(filterExt);
        String selected = dlg.open();
        try {

            // TODO: currentTrack.saveToXml(filename);

            FileOutputStream fo = new FileOutputStream(selected);
            fo.write(10);
            fo.flush();
            fo.close();

            log.info("Track saved to " + selected);
        } catch(FileNotFoundException e) {
            ExceptionHandler.handleException(e, log, "File could not be found", true, "Error", new Status(
                    IStatus.ERROR, "JACAVI", e.toString()));
        } catch(IOException e) {
            ExceptionHandler.handleException(e, log, "An IOException occured", true, "Error", new Status(IStatus.ERROR,
                    "JACAVI", e.toString()));
        }

        monitor.done();
        isDirty = false;
        firePropertyChange(IEditorPart.PROP_DIRTY);

    }

    @Override
    public void doSaveAs() {}

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        setPartName(input.getName());

    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout());

        try {
            trackWidget = new TrackWidget(parent, currentTrack);
        } catch(IOException e) {
            // TODO: do proper error handling here and report to the user
            throw new RuntimeException("Couldn't create widget. [TODO: do proper error handling here]");
        }
        trackWidget.setLayoutData(new GridData(GridData.FILL_BOTH));
        trackWidget.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int selectedPosition = trackWidget.getSelectedTile();
                if(e.keyCode == SWT.DEL && selectedPosition != -1) {
                    handleDeletion(trackWidget, selectedPosition);
                }
            }

        });

        // this Listener is only for synchronization with the Track Outline View
        trackWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                firePropertyChange(PROP_SELECTION_CHANGED);
            }
        });
    }

    @Override
    public void setFocus() {}

    /**
     * @param tile
     */
    public void handleAppendage(Tile tile) {
        currentTrack.appendSection(tile);
        log.debug(tile.getName() + " inserted to track");
        fireTrackModified();
    }

    /**
     * @param trackWidget
     * @param selectedPosition
     */
    public void handleDeletion(final TrackWidget trackWidget, int selectedPosition) {
        try {
            currentTrack.removeSection(selectedPosition);
            log.debug("Delete Tile on Position " + selectedPosition);
            fireTrackModified();
        } catch(IndexOutOfBoundsException e) {
            ExceptionHandler.handleException(e, log, "IndexOutOfBoundsException caught in handleDeletion()", true,
                    "Error", new Status(IStatus.ERROR, "JACAVI", e.toString()));
        } catch(InitialTileMayNotBeRemoved e) {
            ExceptionHandler.handleException(e, log, "Initial tile could not be removed!", true, "Error", new Status(
                    IStatus.WARNING, "JACAVI", e.toString()));
        }
    }

    public void fireTrackModified() {
        isDirty = true;
        firePropertyChange(PROP_DIRTY);
        firePropertyChange(PROP_TRACK_CHANGED);
    }

    public TrackWidget getTrackWidget() {
        return trackWidget;
    }
}