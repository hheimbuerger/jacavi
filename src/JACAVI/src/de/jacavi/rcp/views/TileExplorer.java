package de.jacavi.rcp.views;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.resource.ImageRegistry;
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

import de.jacavi.appl.track.Tile;
import de.jacavi.appl.track.Track;
import de.jacavi.rcp.Activator;
import de.jacavi.rcp.editors.TrackDesigner;



public class TileExplorer extends ViewPart implements IPartListener2 {

    private static Log log = LogFactory.getLog(TileExplorer.class);

    public static final String ID = "JACAVI.tileExplorerView";

    private static final double TILE_IMAGE_SCALE = 0.5;

    private Track currentTrack;

    private final ImageRegistry imageRegistry;

    private ScrolledComposite scrolledComposite;

    private TrackDesigner activeEditor;

    public TileExplorer() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        window.getPartService().addPartListener(this);
        // this.addListenerObject(this);
        imageRegistry = new ImageRegistry();
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
            final Map<String, Tile> tileMap = currentTrack.getTileset().getUsableTiles();

            for(String tileID: tileMap.keySet()) {
                Tile tile = tileMap.get(tileID);

                Image scaledImage = imageRegistry.get(tile.getFilename());
                if(scaledImage == null) {
                    Image image = Activator.getImageDescriptor(tile.getFilename()).createImage();
                    int width = image.getBounds().width;
                    int height = image.getBounds().height;
                    scaledImage = new Image(null, image.getImageData().scaledTo((int) (width * TILE_IMAGE_SCALE),
                            (int) (height * TILE_IMAGE_SCALE)));
                    imageRegistry.put(tile.getFilename(), scaledImage);
                    image.dispose();
                }

                GridData buttonGd = new GridData();
                buttonGd.heightHint = 120;
                buttonGd.widthHint = 120;
                Button tileButton = new Button(innerComposite, SWT.PUSH);
                tileButton.setLayoutData(buttonGd);
                tileButton.setToolTipText(tile.getName());
                tileButton.setImage(scaledImage);

                // // HACK: This hack exists because the DEBUG TileSet is already unscaled small and has not to be
                // scaled.
                // // Why doesn't this just scale to a maximum width and height?
                // if(currentTrack.getTileset().getName().equals("debug")) {
                // tileButton.setImage(image);
                // } else {
                // tileButton.setImage(scaledImage);
                // }

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
    public void setFocus() {}

    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        if(partRef.getId().equals(TrackDesigner.ID)) {
            activeEditor = (TrackDesigner) partRef.getPage().getActiveEditor();
            currentTrack = activeEditor.getTrackWidget().getTrack();
            paintAndRefresh();
        }
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {}

    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
        if(partRef.getId().equals(TrackDesigner.ID)) {
            activeEditor = (TrackDesigner) partRef.getPage().getActiveEditor();
            if(activeEditor == null) {
                scrolledComposite.getContent().dispose();
                scrolledComposite.dispose();
            }
        }
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {}

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {}

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {}

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {}

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {}

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
        imageRegistry.dispose();
        scrolledComposite.getContent().dispose();
        scrolledComposite.dispose();
    }

}
