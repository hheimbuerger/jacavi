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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.track.Tile;
import de.jacavi.appl.track.TilesetRepository;
import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.Track.TrackLoadingException;
import de.jacavi.rcp.Activator;
import de.jacavi.rcp.dlg.SafeSaveDialog;
import de.jacavi.rcp.widgets.TrackWidget;



public class TrackDesigner extends EditorPart {

    private static Log log = LogFactory.getLog(TrackDesigner.class);

    public static String ID = "JACAVI.TrackDesigner";

    boolean isDirty = true;

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
        dlg.setFilterPath("C:/");
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
        firePropertyChange(EditorPart.PROP_DIRTY);

    }

    @Override
    public void doSaveAs() {
    // TODO Auto-generated method stub

    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        setPartName(input.getName());

    }

    @Override
    public boolean isDirty() {
        // TODO Auto-generated method stub
        return isDirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void createPartControl(Composite parent) {
        try {
            parent.setLayout(new GridLayout());

            // fill the toolbar with all available tiles
            ToolBar toolbar = new ToolBar(parent, SWT.BORDER | SWT.WRAP);
            final Map<String, Tile> tileMap = tilesetRepository.getAvailableTiles(currentTrack.getTileset());
            for(String tileID: tileMap.keySet()) {
                Tile tile = tileMap.get(tileID);
                Image image = Activator.getImageDescriptor(tile.getFilename()).createImage();
                usedImages.add(image);

                ToolItem toolItem = new ToolItem(toolbar, SWT.PUSH);
                toolItem.setText(tile.getName());
                toolItem.setImage(image);
                toolItem.addSelectionListener(new SelectionAdapter() {
                    // Append a tile
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        ToolItem selected = (ToolItem) e.widget;
                        for(String tileID: tileMap.keySet()) {
                            Tile tile = tileMap.get(tileID);
                            if(selected.getText().equals(tile.getName()))
                                currentTrack.appendSection(tile);

                        }
                    }
                });
            }

            toolbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            // TODO: track needs to be stored locally and Track(TileSet)
            // constructor should be used (but GUI doesn't
            // allow choosing a TileSet yet)
            new TrackWidget(parent, currentTrack).setLayoutData(new GridData(GridData.FILL_BOTH));
            // currentTrack.appendSection(tileMap.get("straight"));
            // currentTrack.appendSection(tileMap.get("straight"));
            // currentTrack.appendSection(tileMap.get("straight"));
            // currentTrack.appendSection(tileMap.get("straight"));
            // currentTrack.appendSection(tileMap.get("straight"));
            // currentTrack.insertSection(tileMap.get("turn90deg"), 3);
        } catch(TrackLoadingException e) {
            throw new RuntimeException("Error while creating TrackWidget.");
        }
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

}