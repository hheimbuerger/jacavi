package de.jacavi.rcp.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.jacavi.appl.ContextLoader;
import de.jacavi.appl.track.Tile;
import de.jacavi.appl.track.TilesetRepository;
import de.jacavi.appl.track.Track;
import de.jacavi.appl.track.TilesetRepository.TileSet;
import de.jacavi.rcp.Activator;
import de.jacavi.rcp.editors.TrackDesigner;



public class TileExplorer extends ViewPart implements IPartListener2 {

    private static Log log = LogFactory.getLog(TileExplorer.class);

    public static final String ID = "JACAVI.tileExplorerView";

    private static final double TILE_IMAGE_SCALE = 0.5;

    private final TilesetRepository tilesetRepository;

    private Track currentTrack;

    private final List<Image> usedImages = new ArrayList<Image>();

    private ScrolledComposite scrolledComposite;

    private TrackDesigner activeEditor;

    public TileExplorer() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        window.getActivePage().addPartListener(this);
        this.addListenerObject(this);
        tilesetRepository = (TilesetRepository) ContextLoader.getBean("tilesetRepository");
    }

    @Override
    public void createPartControl(Composite parent) {
        scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        paintAndRefresh();
    }

    public void paintAndRefresh() {
        scrolledComposite.setLayout(new FillLayout());

        GridLayout gd = new GridLayout(2, true);
        gd.makeColumnsEqualWidth = true;

        Composite innerComposite = new Composite(scrolledComposite, SWT.NONE);
        innerComposite.setLayout(gd);

        if(currentTrack != null) {
            final Map<String, Tile> tileMap = tilesetRepository.getAvailableTiles(currentTrack.getTileset());

            for(String tileID: tileMap.keySet()) {
                Tile tile = tileMap.get(tileID);
                Image image = Activator.getImageDescriptor(tile.getFilename()).createImage();
                usedImages.add(image);

                // Scale images
                int width = image.getBounds().width;
                int height = image.getBounds().height;
                Image scaledImage = new Image(null, image.getImageData().scaledTo((int) (width * TILE_IMAGE_SCALE),
                        (int) (height * TILE_IMAGE_SCALE)));

                Button tileButton = new Button(innerComposite, SWT.PUSH);
                tileButton.setToolTipText(tile.getName());
                tileButton.setImage(scaledImage);
                tileButton.setLayoutData(new GridData(GridData.FILL_BOTH));

                // HACK: This hack exists because the DEBUG TileSet is already unscaled small and has not to be scaled
                if(currentTrack.getTileset().equals(TileSet.DEBUG)) {
                    tileButton.setImage(image);
                } else {
                    tileButton.setImage(scaledImage);
                }

                tileButton.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        invokeInsertion(tileMap, e);
                    }
                });
            }
            innerComposite.pack();
            scrolledComposite.setContent(innerComposite);
            log.debug("TileExplorer refreshed!");
        }
    }

    @Override
    public void setFocus() {
    // TODO Auto-generated method stub

    }

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        if(partRef.getId().equals(TrackDesigner.ID)) {
            log.debug(partRef.getPartName() + " activated");
            activeEditor = (TrackDesigner) partRef.getPage().getActiveEditor();
            currentTrack = activeEditor.getTrackWidget().getTrack();
            paintAndRefresh();
        }
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
    // TODO Auto-generated method stub

    }

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
    // TODO Auto-generated method stub

    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {
    // TODO Auto-generated method stub

    }

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {
    // TODO Auto-generated method stub

    }

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {
    // TODO Auto-generated method stub

    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {
    // TODO Auto-generated method stub

    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {
    // TODO Auto-generated method stub

    }

    private void invokeInsertion(final Map<String, Tile> tileMap, SelectionEvent e) {
        Button selected = (Button) e.widget;
        for(String tileID: tileMap.keySet()) {
            Tile tile = tileMap.get(tileID);
            if(selected.getToolTipText().equals(tile.getName())) {
                activeEditor.handleAppendage(tile);
            }
        }
    }

    /**
     * Dispose handler that is invoked when the view is closed.
     * <p>
     * Used to dispose the loaded images, because each of them is managing an operating system resource.
     */
    @Override
    public void dispose() {
        super.dispose();
        for(Image image: usedImages)
            image.dispose();
    }

}
