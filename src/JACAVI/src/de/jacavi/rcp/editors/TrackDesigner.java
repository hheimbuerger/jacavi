package de.jacavi.rcp.editors;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.track.Tile;
import de.jacavi.appl.track.TilesetRepository;
import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.TilesetRepository.TileSet;
import de.jacavi.appl.track.Track.InitialTileMayNotBeRemoved;
import de.jacavi.rcp.Activator;
import de.jacavi.rcp.dlg.SafeSaveDialog;
import de.jacavi.rcp.widgets.TrackWidget;



public class TrackDesigner extends EditorPart {

    private static Log log = LogFactory.getLog(TrackDesigner.class);

    public static String ID = "JACAVI.TrackDesigner";

    private static final double TILE_IMAGE_SCALE = 0.5;

    boolean isDirty = false;

    private TilesetRepository tilesetRepository;

    private Track currentTrack;

    private final List<Image> usedImages = new ArrayList<Image>();

    public TrackDesigner() {
        tilesetRepository = (TilesetRepository) ContextLoader.getBean("tilesetRepository");
    }

    /**
     * Dispose handler that is invoked when the editor is closed.
     * <p>
     * Used to dispose the loaded images, because each of them is managing an operating system resource.
     */
    @Override
    public void dispose() {
        super.dispose();
        for(Image image: usedImages)
            image.dispose();
    }

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
            handleException(e);
        } catch(IOException e) {
            handleException(e);
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

        final Map<String, Tile> tileMap = tilesetRepository.getAvailableTiles(currentTrack.getTileset());

        CoolBar tilesCoolbar = new CoolBar(parent, SWT.NONE);
        tilesCoolbar.setLocked(true);

        CoolItem singleCoolItem = new CoolItem(tilesCoolbar, SWT.NONE);

        Composite tilesComposite = new Composite(tilesCoolbar, SWT.None);
        tilesComposite.setLayout(new GridLayout(tileMap.size(), false));
        tilesComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));

        for(String tileID: tileMap.keySet()) {
            Tile tile = tileMap.get(tileID);
            Image image = Activator.getImageDescriptor(tile.getFilename()).createImage();
            usedImages.add(image);

            // Scale images
            int width = image.getBounds().width;
            int height = image.getBounds().height;
            Image scaledImage = new Image(null, image.getImageData().scaledTo((int) (width * TILE_IMAGE_SCALE),
                    (int) (height * TILE_IMAGE_SCALE)));

            // Toolbar is embedded in a CoolItem
            ToolBar toolbar = new ToolBar(tilesComposite, SWT.WRAP);
            toolbar.setLayoutData(new GridData(GridData.FILL_VERTICAL));

            ToolItem tileToolItem = new ToolItem(toolbar, SWT.PUSH);
            tileToolItem.setToolTipText(tile.getName());

            // HACK: This hack exists because the DEBUG TileSet is already unscaled small and has not to be scaled
            if(currentTrack.getTileset().equals(TileSet.DEBUG)) {
                tileToolItem.setImage(image);
            } else {
                tileToolItem.setImage(scaledImage);
            }

            tileToolItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    handleAppendage(tileMap, e);
                }
            });
        }
        tilesComposite.pack();
        singleCoolItem.setSize(tilesComposite.getSize());
        singleCoolItem.setControl(tilesComposite);

        final TrackWidget trackWidget;
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
    }

    @Override
    public void setFocus() {}

    private void handleException(Exception e) {
        log.error("Save failed", e);
    }

    public void setTilesetRepository(TilesetRepository tilesetRepository) {
        this.tilesetRepository = tilesetRepository;
    }

    public TilesetRepository getTilesetRepository() {
        return tilesetRepository;
    }

    /**
     * @param trackWidget
     * @param selectedPosition
     */
    private void handleDeletion(final TrackWidget trackWidget, int selectedPosition) {
        try {
            currentTrack.removeSection(selectedPosition);
            log.debug("Delete Tile on Position " + selectedPosition);
            fireTrackModified();
        } catch(IndexOutOfBoundsException e) {
            log.error("IndexOutOfBoundsException caught in handleDeletion()", e);
            ErrorDialog.openError(getEditorSite().getShell(), "Error", "An Exception occured while tile deletion.",
                    new Status(IStatus.WARNING, "JACAVI", e.toString()));
        } catch(InitialTileMayNotBeRemoved e) {
            log.error("InitialTileMayNotBeRemoved caught in handleDeletion()", e);
            ErrorDialog.openError(getEditorSite().getShell(), "Warning", "Initital Tile may not be removed.",
                    new Status(IStatus.WARNING, "JACAVI", e.toString()));
        }
    }

    /**
     * Appends a tile on the last position
     * 
     * @param tileMap
     * @param e
     */
    private void handleAppendage(final Map<String, Tile> tileMap, SelectionEvent e) {
        ToolItem selected = (ToolItem) e.widget;
        for(String tileID: tileMap.keySet()) {
            Tile tile = tileMap.get(tileID);
            if(selected.getToolTipText().equals(tile.getName())) {
                currentTrack.appendSection(tile);
                log.debug(tile.getName() + " inserted to track");
                fireTrackModified();
            }
        }
    }

    private void fireTrackModified() {
        isDirty = true;
        firePropertyChange(PROP_DIRTY);
    }
}