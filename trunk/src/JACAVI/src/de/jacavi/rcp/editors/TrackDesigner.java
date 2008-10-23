package de.jacavi.rcp.editors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
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
import de.jacavi.rcp.dialogs.SafeSaveDialog;
import de.jacavi.rcp.util.ExceptionHandler;
import de.jacavi.rcp.widgets.TrackWidget;
import de.jacavi.rcp.widgets.TrackWidget.TrackWidgetMode;



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

        if(!new File(((TrackDesignerInput) getEditorInput()).getFilename()).exists())
            doSaveAs();
        else {
            String selected = ((TrackDesignerInput) getEditorInput()).getFilename();
            try {
                if(selected != null) {
                    currentTrack.saveToXml(selected);
                    log.info("Track saved to " + selected);
                    isDirty = false;
                    firePropertyChange(IEditorPart.PROP_DIRTY);
                }
            } catch(FileNotFoundException e) {
                ExceptionHandler.handleException(this, e, true);
            } catch(Exception e) {
                ExceptionHandler.handleException(this, e, true);
            }

        }
        monitor.done();
    }

    @Override
    public void doSaveAs() {
        SafeSaveDialog dlg = new SafeSaveDialog(getSite().getShell());
        dlg.setText("Save");
        String[] filterExt = { "*" + Track.FILE_EXTENSION };
        dlg.setFilterExtensions(filterExt);
        String selected = dlg.open();

        try {
            if(selected != null) {
                ((TrackDesignerInput) getEditorInput()).setFilename(selected);
                currentTrack.saveToXml(selected);

                log.info("Track saved to " + selected);
                isDirty = false;
                firePropertyChange(IEditorPart.PROP_DIRTY);
                fireEditorNameModified();
            }
        } catch(FileNotFoundException e) {
            ExceptionHandler.handleException(this, e, true);
        } catch(Exception e) {
            ExceptionHandler.handleException(this, e, true);
        }
    }

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
            trackWidget = new TrackWidget(parent, currentTrack, TrackWidgetMode.DESIGN_MODE);
        } catch(IOException e) {
            throw new RuntimeException(e);
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
            ExceptionHandler.handleException(this, e, true);
        } catch(InitialTileMayNotBeRemoved e) {
            ExceptionHandler.handleException(this, "Initial tile could not be removed!", e, true);
        }
    }

    public void fireTrackModified() {
        isDirty = true;
        firePropertyChange(PROP_DIRTY);
        firePropertyChange(PROP_TRACK_CHANGED);
    }

    public void fireEditorNameModified() {
        setPartName(getEditorInput().getName());
        firePropertyChange(PROP_INPUT);
    }

    public TrackWidget getTrackWidget() {
        return trackWidget;
    }

    public Track getTrack() {
        return currentTrack;
    }

    public String getFilename() {
        return ((TrackDesignerInput) getEditorInput()).getFilename();
    }
}